package com.example.stepcheck.utils;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.stepcheck.R;
import com.example.stepcheck.Receiver.ShiftSyncReceiver;
import com.example.stepcheck.models.Shift;
import com.example.stepcheck.models.Worker;
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

/**
 * A foreground service that manages active shifts.
 * It tracks the worker's location to verify presence at the workplace,
 * handles automatic shift ending if the worker leaves the area,
 * and synchronizes shift data with Firebase.
 */
public class ShiftService extends Service {
    private static final String CHANNEL_ID = "ShiftServiceChannel";

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

    // משתנים לטיימר של 5 דקות
    private Runnable autoEndRunnable;
    private boolean autoEndScheduled = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Called when the service is first created.
     * Initializes location services, sets up shift observation, and schedules end-of-day sync.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();
        observeShiftStatus();

        if (isWorkDay()) {
            scheduleEndOfDaySync();
        }
    }

    /**
     * Checks if the current day is a working day (Sunday through Thursday).
     * @return true if it's a work day, false otherwise.
     */
    private static boolean isWorkDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.FRIDAY && dayOfWeek != Calendar.SATURDAY;
    }

    /**
     * Schedules a daily synchronization task to run at the end of the day (23:59).
     * Uses {@link AlarmManager} to trigger {@link ShiftSyncReceiver}.
     */
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

    /**
     * Sets up a listener on the worker's shift data in Firebase.
     * Monitors whether the worker is currently on break to adjust location tracking behavior.
     */
    private void observeShiftStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String workerId = user.getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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

                    // ביטול טיימר אם בהפסקה
                    if (isOnBreak) cancelAutoEnd();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShiftService", "Firebase error: " + error.getMessage());
            }
        };
        shiftRef.addValueEventListener(shiftListener);
    }

    /**
     * Updates the persistent notification with the current shift status.
     * @param message The message to display in the notification.
     */
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

    /**
     * Synchronizes all active shifts for a specific date to the 'FinishedShifts' node in Firebase.
     * @param date The date for which to perform the synchronization (formatted as yyyy-MM-dd).
     */
    public static void checkEndOfDayAndSync(String date) {
        if (!isWorkDay()) return;

        final String firebasePath = date; 
        final String summaryDate = date;

        FBRef.refBase5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot presenceSnapshot) {
                final ArrayList<String> activeWorkerIds = new ArrayList<>();

                for (DataSnapshot workerNode : presenceSnapshot.getChildren()) {
                    String workerId = workerNode.getKey();
                    DataSnapshot dateNode = workerNode.child(firebasePath);

                    if (dateNode.exists()) {
                        Boolean isStarted = dateNode.child("is_startShift").getValue(Boolean.class);
                        Boolean isEnded = dateNode.child("buttonEndEnabled").getValue(Boolean.class);

                        if (isStarted != null && isStarted) {
                            activeWorkerIds.add(workerId);
                            
                            // סגירה אוטומטית רק אם עברה שעת הסיום של היום (23:59)
                            if (isEnded == null || !isEnded) {
                                DatabaseReference ref = FBRef.refBase5.child(workerId).child(firebasePath);
                                ref.child("end_your_Shift").setValue("23:59:59");
                                ref.child("buttonEndEnabled").setValue(true);
                                ref.child("is_startShift").setValue(false);
                                
                                FBRef.refBase.child(workerId).child("inShift").setValue(false);
                            }
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

                            Shift dailySummary = new Shift(
                                    summaryDate,
                                    dailyWorkers,
                                    FIXED_START_TIME,
                                    FIXED_END_TIME
                            );

                            FirebaseDatabase.getInstance()
                                    .getReference("FinishedShifts")
                                    .child(dailySummary.getShift_Id())
                                    .setValue(dailySummary);
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

    /**
     * Called when the service is started via {@link Context#startService}.
     * Sets the service as a foreground service and begins location updates.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // בדיקת הרשאות מיקום - אם אין הרשאה, לא ניתן להתחיל משמרת והשירות יסתיים
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("ShiftService", "Location permission missing. Ending shift.");
            autoEndShift();
            return START_NOT_STICKY;
        }

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

        return START_NOT_STICKY;
    }

    /**
     * Initializes the location callback for receiving location updates.
     */
    private void setupLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    checkDistance(location);
                    updateWorkerLocationInFirebase(location);
                }
            }
        };
    }

    /**
     * Updates the worker's current coordinates in the Firebase Realtime Database.
     * @param location The current location of the device.
     */
    private void updateWorkerLocationInFirebase(Location location) {
        if (shiftRef != null) {
            shiftRef.child("latitude").setValue(location.getLatitude());
            shiftRef.child("longitude").setValue(location.getLongitude());
        }
    }

    /**
     * Calculates the distance between the current location and the workplace.
     * Triggers an automatic shift end timer if the worker leaves the allowed radius.
     * @param currentLocation The current location of the device.
     */
    private void checkDistance(Location currentLocation) {
        if (isOnBreak) {
            cancelAutoEnd(); 
            return;
        }

        float[] results = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), workLat, workLon, results);

        if (results[0] > MAX_DISTANCE_METERS) {
            scheduleAutoEnd(); 
        } else {
            cancelAutoEnd(); 
        }
    }

    /**
     * Schedules a task to automatically end the shift after 5 minutes of being outside the workplace area.
     */
    private void scheduleAutoEnd() {
        if (autoEndScheduled) return; 

        autoEndScheduled = true;
        autoEndRunnable = new Runnable() {
            @Override
            public void run() {
                autoEndShift();
                autoEndScheduled = false;
            }
        };

        handler.postDelayed(autoEndRunnable, 5 * 60 * 1000); 
    }

    /**
     * Cancels the scheduled automatic shift end task.
     */
    private void cancelAutoEnd() {
        if (autoEndScheduled && autoEndRunnable != null) {
            handler.removeCallbacks(autoEndRunnable);
            autoEndScheduled = false;
        }
    }

    /**
     * Automatically records the end time of the current shift in Firebase and stops the service.
     */
    private void autoEndShift() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String workerId = user.getUid();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = sdfDate.format(calendar.getTime());
        String currentTime = sdfTime.format(calendar.getTime());

        DatabaseReference ref = FBRef.refBase5.child(workerId).child(currentDate);
        ref.child("end_your_Shift").setValue(currentTime);
        ref.child("buttonEndEnabled").setValue(true);
        ref.child("is_startShift").setValue(false);
        FBRef.refBase.child(workerId).child("inShift").setValue(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        stopSelf();
    }

    /**
     * Requests location updates from the FusedLocationProviderClient.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Called when the service is destroyed.
     * Stops location updates and removes Firebase listeners.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) fusedLocationClient.removeLocationUpdates(locationCallback);
        if (shiftRef != null && shiftListener != null) shiftRef.removeEventListener(shiftListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates a notification channel for the foreground service.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Shift Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
