package com.example.stepcheck.activities;

import static com.example.stepcheck.utils.FBRef.refBase;
import static com.example.stepcheck.utils.FBRef.refBase5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.stepcheck.R;
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
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String user;
    TextView etName, etRank, etWorkTime;
    Button btnBack;
    private GoogleMap mMap;
    private Marker workerMarker;
    private ValueEventListener locationListener;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        give_all_inform();
    }

    private void give_all_inform() {
        refBase.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Name = snapshot.child("username").getValue(String.class);
                    String rank = snapshot.child("job_rank").getValue(String.class);
                    etName.setText(Name);
                    etRank.setText(rank);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        refBase5.child(user).child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String startShift = snapshot.child("start_your_Shift").getValue(String.class);
                    if (startShift != null) {
                        etWorkTime.setText(startShift);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        startListeningForLocation();
    }

    /**
     * Checks for location permissions and enables the "My Location" layer on the map.
     * Requests permissions if they are not already granted.
     */
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission denied. Map might not show your position.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startListeningForLocation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        refBase5.child(user).child(currentDate).addValueEventListener(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationListener != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(calendar.getTime());
            refBase5.child(user).child(currentDate).removeEventListener(locationListener);
        }
    }

    public void back(View view) {
        finish();
    }
}
