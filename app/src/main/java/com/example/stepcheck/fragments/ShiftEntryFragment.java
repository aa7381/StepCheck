package com.example.stepcheck.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.stepcheck.utils.FBRef;
import com.example.stepcheck.models.Presences;
import com.example.stepcheck.R;
import com.example.stepcheck.utils.ShiftService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShiftEntryFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    Button button_start_shift, button_pause_shift, button_end_shift;

    private String currentDate = "";
    private Presences presences = new Presences();

    private boolean inBreak = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shiftentryfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_end_shift = view.findViewById(R.id.button_end_shift);
        button_start_shift = view.findViewById(R.id.button_start_shift);
        button_pause_shift = view.findViewById(R.id.button_pause_shift);

        disableAllButtons();

        infrom();
        checkLocationPermissions();

        button_start_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift();
            }
        });

        button_pause_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inBreak) {
                    pauseShift();
                } else {
                    pauseShiftEnd();
                }
            }
        });

        button_end_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift();
            }
        });
    }

    private void disableAllButtons() {
        button_start_shift.setEnabled(false);
        button_pause_shift.setEnabled(false);
        button_end_shift.setEnabled(false);
    }

    private void checkLocationPermissions() {
        if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void infrom() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String workerId = currentUser.getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = sdf.format(calendar.getTime());

        DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

        shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                presences = snapshot.getValue(Presences.class);
                if (presences == null) presences = new Presences();

                boolean started = Boolean.TRUE.equals(presences.getIs_startShift());
                boolean ended = Boolean.TRUE.equals(presences.getButtonEndEnabled());

                inBreak = Boolean.TRUE.equals(presences.getButtonPauseEnabled()) && !Boolean.TRUE.equals(presences.getButtonPauseEndEnabled());

                button_start_shift.setEnabled(!started && !ended);
                button_pause_shift.setEnabled(started && !ended);
                button_end_shift.setEnabled(started && !ended);

                updatePauseButtonText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });
    }

    private void updatePauseButtonText() {
        if (inBreak) {
            button_pause_shift.setText("End Break");
        } else {
            button_pause_shift.setText("Start Break");
        }
    }

    private void startShift() {
        // בדיקת הרשאות מיקום לפני התחלת משמרת
        if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "חובה לאשר הרשאת מיקום כדי להתחיל משמרת", Toast.LENGTH_LONG).show();
            checkLocationPermissions();
            return;
        }

        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user == null) return;

        String workerId = user.getUid();
        DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

        Calendar calendar = Calendar.getInstance();
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.getTime());

        presences.setStart_your_Shift(time);
        presences.setIs_startShift(true);
        presences.setButtonEndEnabled(false);
        presences.setButtonPauseEnabled(false);
        presences.setButtonPauseEndEnabled(false);
        presences.setWorker_id(workerId);
        presences.setTime(currentDate);

        shiftRef.setValue(presences);
        
        // עדכון סטטוס כללי של העובד ל-inShift = true
        FBRef.refBase.child(workerId).child("inShift").setValue(true);

        startService();

        button_start_shift.setEnabled(false);
        button_pause_shift.setEnabled(true);
        button_end_shift.setEnabled(true);

        Toast.makeText(getActivity(), "Shift started", Toast.LENGTH_SHORT).show();
    }

    private void pauseShift() {
        inBreak = true;
        updatePauseButtonText();

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        presences.setPause_time(time);
        presences.setButtonPauseEnabled(true);
        presences.setButtonPauseEndEnabled(false);

        save();
    }

    private void pauseShiftEnd() {
        inBreak = false;
        updatePauseButtonText();

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        presences.setPause_end_time(time);
        presences.setButtonPauseEndEnabled(true);

        save();
    }

    private void endShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user == null) return;

        String workerId = user.getUid();
        DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        presences.setEnd_your_Shift(time);
        presences.setIs_startShift(false);
        presences.setButtonEndEnabled(true);

        shiftRef.setValue(presences);
        
        // עדכון סטטוס כללי של העובד ל-inShift = false
        FBRef.refBase.child(workerId).child("inShift").setValue(false);

        stopService();

        button_end_shift.setEnabled(false);
        button_pause_shift.setEnabled(false);

        Toast.makeText(getActivity(), "Shift ended", Toast.LENGTH_SHORT).show();
    }

    private void save() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user == null) return;

        FBRef.refBase5.child(user.getUid()).child(currentDate).setValue(presences);
    }

    private void startService() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ShiftService.class);
            getActivity().startService(intent);
        }
    }

    private void stopService() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ShiftService.class);
            getActivity().stopService(intent);
        }
    }
}
