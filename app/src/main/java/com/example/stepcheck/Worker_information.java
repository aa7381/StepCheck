package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase;
import static com.example.stepcheck.FBRef.refBase5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * An activity that displays detailed information about a specific worker.
 * It shows the worker's name, job rank, shift start time, and current real-time location on a map.
 */
public class Worker_information extends AppCompatActivity implements OnMapReadyCallback {
    String user;

    TextView etName, etRank,etWorkTime;
    Button btnBack;

    private GoogleMap mMap;
    private Marker workerMarker;
    private ValueEventListener locationListener;

    /**
     * Called when the activity is first created.
     * Initializes the UI components, retrieves the worker ID from the intent,
     * and sets up the Google Map.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_information);
        etName = findViewById(R.id.etName);
        etRank = findViewById(R.id.etRank);
        btnBack = findViewById(R.id.btnBack);
        etWorkTime = findViewById(R.id.etWorkTime);



        Intent intent = getIntent();
        user = intent.getStringExtra("USER_ID");

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        give_all_inform();
    }

    /**
     * Fetches and displays the worker's basic information (name, rank)
     * and today's shift start time from Firebase.
     */
    private void give_all_inform() {
        refBase.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Name = snapshot.child("username").getValue(String.class);
                    String rank = snapshot.child("job_rank").getValue(String.class);

                    etName.setText("Worker name: " + Name);
                    etRank.setText("Worker_rank: " + rank);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Calendar calendar = Calendar.getInstance();

        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        refBase5.child(user)
                .child(day)
                .child(month)
                .child(year)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String startShift = snapshot.child("start_your_Shift").getValue(String.class);

                            if (startShift != null) {
                                etWorkTime.setText( "\nStart shift: " + startShift);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     *
     * @param googleMap A non-null instance of a GoogleMap.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startListeningForLocation();
    }

    /**
     * Starts listening for real-time location updates of the worker from Firebase.
     * Updates the marker position on the map accordingly.
     */
    private void startListeningForLocation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        locationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double lat = snapshot.child("latitude").getValue(Double.class);
                    Double lng = snapshot.child("longitude").getValue(Double.class);

                    if (lat != null && lng != null && lat != 0 && lng != 0) {
                        LatLng workerPos = new LatLng(lat, lng);
                        if (workerMarker == null) {
                            workerMarker = mMap.addMarker(new MarkerOptions().position(workerPos).title("Worker Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(workerPos, 15));
                        } else {
                            workerMarker.setPosition(workerPos);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        refBase5.child(user).child(currentDate).addValueEventListener(locationListener);
    }

    /**
     * Removes the location listener when the activity is destroyed to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationListener != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(calendar.getTime());
            refBase5.child(user).child(currentDate).removeEventListener(locationListener);
        }
    }

    /**
     * Closes the current activity and returns to the previous screen.
     *
     * @param view The view that was clicked.
     */
    public void back(View view) {
        finish();
    }
}
