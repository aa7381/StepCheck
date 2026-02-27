package com.example.stepcheck;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ShiftService extends Service {
    private static final String CHANNEL_ID = "ShiftServiceChannel";
    
    // שעות קבועות למשמרת בדו"ח המרוכז
    private static final String FIXED_START_TIME = "08:00:00";
    private static final String FIXED_END_TIME = "17:00:00";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ValueEventListener shiftListener;
    private DatabaseReference shiftRef;
    
    private double workLat = 32.0853; 
    private double workLon = 34.7818;
    private static final float MAX_DISTANCE_METERS = 200; 
    
    private boolean isOnBreak = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();
        observeShiftStatus();
        
        // מתזמן רק אם זה יום עבודה (א-ה)
        if (isWorkDay()) {
            scheduleEndOfDaySync();
        }
    }

    /**
     * בודק אם היום הוא יום עבודה (ראשון עד חמישי)
     */
    private static boolean isWorkDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.FRIDAY && dayOfWeek != Calendar.SATURDAY;
    }

    private void scheduleEndOfDaySync() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ShiftSyncReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void observeShiftStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        
        String workerId = user.getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdfDate.format(calendar.getTime());
        
        shiftRef = FBRef.refBase5.child(workerId).child(currentDate);
        shiftListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean pauseEnabled = snapshot.child("buttonPauseEnabled").getValue(Boolean.class);
                    Boolean pauseEndEnabled = snapshot.child("buttonPauseEndEnabled").getValue(Boolean.class);
                    isOnBreak = (pauseEnabled != null && pauseEnabled) && (pauseEndEnabled == null || !pauseEndEnabled);
                    updateNotification(isOnBreak ? "אתה בהפסקה" : "משמרת פעילה - המערכת מוודאת נוכחות");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShiftService", "Firebase error: " + error.getMessage());
            }
        };
        shiftRef.addValueEventListener(shiftListener);
    }

    private void updateNotification(String message) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("סטטוס משמרת")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build();
        
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    public static void checkEndOfDayAndSync(final String date) {
        if (!isWorkDay()) return;

        FBRef.refBase5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot presenceSnapshot) {
                final ArrayList<String> activeWorkerIds = new ArrayList<>();
                for (DataSnapshot workerNode : presenceSnapshot.getChildren()) {
                    if (workerNode.hasChild(date)) {
                        Boolean isStarted = workerNode.child(date).child("is_startShift").getValue(Boolean.class);
                        if (isStarted != null && isStarted) {
                            activeWorkerIds.add(workerNode.getKey());
                        }
                    }
                }

                if (!activeWorkerIds.isEmpty()) {
                    FBRef.refBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot workersSnapshot) {
                            ArrayList<Worker> dailyWorkers = new ArrayList<>();
                            for (String id : activeWorkerIds) {
                                Worker w = workersSnapshot.child(id).getValue(Worker.class);
                                if (w != null) dailyWorkers.add(w);
                            }
                            Shift dailySummary = new Shift(date.replace("/", "-"), dailyWorkers, FIXED_START_TIME, FIXED_END_TIME);
                            FirebaseDatabase.getInstance().getReference("FinishedShifts")
                                    .child(dailySummary.getShift_Id()).setValue(dailySummary);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("workLat")) {
            workLat = intent.getDoubleExtra("workLat", workLat);
            workLon = intent.getDoubleExtra("workLon", workLon);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("משמרת פעילה")
                .setContentText("מעקב מיקום פועל")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        startLocationUpdates();

        if (isWorkDay()) {
            Calendar cal = Calendar.getInstance();
            String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());
            checkEndOfDayAndSync(today);
        }

        return START_NOT_STICKY;
    }

    private void setupLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    checkDistance(location);
                }
            }
        };
    }

    private void checkDistance(Location currentLocation) {
        if (isOnBreak) return;
        float[] results = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), workLat, workLon, results);
        if (results[0] > MAX_DISTANCE_METERS) {
            autoEndShift();
        }
    }

    private void autoEndShift() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String workerId = user.getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = sdfDate.format(calendar.getTime());
        String currentTime = sdfTime.format(calendar.getTime());

        DatabaseReference ref = FBRef.refBase5.child(workerId).child(currentDate);
        ref.child("end_your_Shift").setValue(currentTime);
        ref.child("buttonEndEnabled").setValue(true);
        
        if (isWorkDay()) checkEndOfDayAndSync(currentDate);

        stopForeground(true);
        stopSelf();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) fusedLocationClient.removeLocationUpdates(locationCallback);
        if (shiftRef != null && shiftListener != null) shiftRef.removeEventListener(shiftListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Shift Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }
}
