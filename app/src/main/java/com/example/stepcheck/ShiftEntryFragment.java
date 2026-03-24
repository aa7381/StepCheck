package com.example.stepcheck;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A fragment that allows workers to manage their shift attendance.
 * It provides buttons to start a shift, take a break, end a break, and end the shift.
 * The fragment also tracks the user's location during the shift via a background service.
 */
public class ShiftEntryFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Button button_start_shift, button_pause_shift, button_end_shift, button_pause_shift_end;


    private String currentDate = "";
    private Presences presences = new Presences();


    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shiftentryfragment, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned.
     * Initializes UI components, sets up click listeners, and fetches initial shift status.
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_end_shift = view.findViewById(R.id.button_end_shift);
        button_start_shift = view.findViewById(R.id.button_start_shift);
        button_pause_shift = view.findViewById(R.id.button_pause_shift);
        button_pause_shift_end = view.findViewById(R.id.button_pause_shift_end);

        // Initially disable buttons until data is loaded
        if (button_start_shift != null) button_start_shift.setEnabled(false);
        if (button_pause_shift != null) button_pause_shift.setEnabled(false);
        if (button_pause_shift_end != null) button_pause_shift_end.setEnabled(false);
        if (button_end_shift != null) button_end_shift.setEnabled(false);

        infrom();

        checkLocationPermissions();
        if (button_start_shift != null) {
            button_start_shift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startShift();

                }
            });
        }
        if (button_pause_shift != null) {
            button_pause_shift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseShift();
                }
            });
        }
        if (button_pause_shift_end != null) {
            button_pause_shift_end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseShiftEnd();

                }
            });
        }
        if (button_end_shift != null) {
            button_end_shift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endShift();
                }

            });
        }
    }
    /**
     * Checks if the application has the necessary location permissions.
     * If not, it requests them from the user.
     */
    private void checkLocationPermissions() {
        if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    /**
     * Callback for the result from requesting permissions.
     * @param requestCode The request code passed in requestPermissions.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
            } else {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Location permission is required to track your shift", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     * Fetches current day's shift data for the logged-in worker from Firebase.
     * Updates the UI buttons' enabled state based on the retrieved shift progress.
     */
    private void infrom() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String workerId = currentUser.getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDate = sdf.format(calendar.getTime());

        DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

        shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    presences = snapshot.getValue(Presences.class);
                } else {
                    presences = new Presences();
                }

                if (presences == null) presences = new Presences();

                if (button_start_shift != null) {
                    button_start_shift.setEnabled(presences.getStart_your_Shift() == null || presences.getStart_your_Shift().isEmpty());
                }
                if (button_pause_shift != null) {
                    button_pause_shift.setEnabled(presences.getStart_your_Shift() != null && !presences.getStart_your_Shift().isEmpty() && (presences.getPause_time() == null || presences.getPause_time().isEmpty()));
                }
                if (button_pause_shift_end != null) {
                    button_pause_shift_end.setEnabled(presences.getPause_time() != null && !presences.getPause_time().isEmpty() && (presences.getPause_end_time() == null || presences.getPause_end_time().isEmpty()));
                }
                if (button_end_shift != null) {
                    button_end_shift.setEnabled(presences.getStart_your_Shift() != null && !presences.getStart_your_Shift().isEmpty() && (presences.getEnd_your_Shift() == null || presences.getEnd_your_Shift().isEmpty()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading shift data: " + error.getMessage());
            }
        });
    }
    /**
     * Starts the worker's shift.
     * Records the start time in Firebase, updates the worker's status to 'in shift',
     * and starts the {@link ShiftService} for background location tracking.
     */
    private void startShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();
            final DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);
            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        presences = snapshot.getValue(Presences.class);
                    }
                    if (presences == null) presences = new Presences();

                    if (presences.getIs_startShift() != null && presences.getIs_startShift()) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "You have already started a shift", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    } else {

                        if (getActivity() != null) {
                            Intent serviceIntent = new Intent(getActivity(), ShiftService.class);
                            getActivity().startService(serviceIntent);
                        }

                        DatabaseReference inShiftRef = FBRef.refBase.child(workerId).child("inShift");
                        inShiftRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "inShift updated successfully!");
                                } else {
                                    Log.e("Firebase", "Failed to update inShift", task.getException());
                                }
                            }
                        });


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String currentTime = sdf.format(calendar.getTime());

                        presences.setStart_your_Shift(currentTime);

                        Log.d("Presences", "Shift started at: " + presences.getStart_your_Shift());
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "Shift started at: " + presences.getStart_your_Shift(), Toast.LENGTH_SHORT).show();
                        }

                        if (button_start_shift != null) button_start_shift.setEnabled(false);
                        if (button_pause_shift != null) button_pause_shift.setEnabled(true);
                        if (button_end_shift != null) button_end_shift.setEnabled(true);

                        presences.setPause_time("");
                        presences.setPause_end_time("");
                        presences.setEnd_your_Shift("");
                        presences.setWorker_id(workerId);
                        presences.setIs_startShift(true);
                        presences.setTime(currentDate);

                        shiftRef.setValue(presences)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Firebase", "Presences updated successfully!");
                                        } else {
                                            Log.e("Firebase", "Failed to update Presences", task.getException());
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error reading shift data: " + error.getMessage());
                }
            });
        }
    }
    /**
     * Records the start of a break for the current shift.
     * Updates the pause time in Firebase and toggles button states.
     */
    private void pauseShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();
            final DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        presences = snapshot.getValue(Presences.class);
                    }
                    if (presences == null) presences = new Presences();

                    if (presences.getButtonPauseEnabled() != null && presences.getButtonPauseEnabled()) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "You have already started a break", Toast.LENGTH_SHORT).show();
                        }
                        if (button_pause_shift != null) button_pause_shift.setEnabled(false);
                        return;
                    } else {

                        DatabaseReference inShiftRef = FBRef.refBase.child(workerId).child("inShift");
                        inShiftRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "inShift updated successfully!");
                                } else {
                                    Log.e("Firebase", "Failed to update inShift", task.getException());
                                }
                            }
                        });


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String currentTime = sdf.format(calendar.getTime());

                        presences.setPause_time(currentTime);
                        if (button_pause_shift != null) button_pause_shift.setEnabled(false);
                        if (button_pause_shift_end != null) button_pause_shift_end.setEnabled(true);

                        presences.setPause_end_time("");
                        presences.setEnd_your_Shift("");
                        presences.setButtonPauseEnabled(true);

                        shiftRef.setValue(presences)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Firebase", "Presences updated successfully!");
                                        } else {
                                            Log.e("Firebase", "Failed to update Presences", task.getException());
                                        }
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled (@NonNull DatabaseError error){
                    Log.e("Firebase", "Error reading shift data: " + error.getMessage());
                }
            });
        }
    }
    /**
     * Records the end of a break for the current shift.
     * Updates the pause end time in Firebase and toggles button states.
     */
    private void pauseShiftEnd() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();
            final DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        presences = snapshot.getValue(Presences.class);
                    }
                    if (presences == null) presences = new Presences();

                    if (presences.getButtonPauseEndEnabled() != null && presences.getButtonPauseEndEnabled()) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "You have already ended the break", Toast.LENGTH_SHORT).show();
                        }
                        if (button_pause_shift_end != null) button_pause_shift_end.setEnabled(false);
                        return;
                    } else {

                        DatabaseReference inShiftRef = FBRef.refBase.child(workerId).child("inShift");
                        inShiftRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "inShift updated successfully!");
                                } else {
                                    Log.e("Firebase", "Failed to update inShift", task.getException());
                                }
                            }
                        });


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String currentTime = sdf.format(calendar.getTime());

                        presences.setPause_end_time(currentTime);
                        if (button_pause_shift_end != null) button_pause_shift_end.setEnabled(false);
                        if (button_end_shift != null) button_end_shift.setEnabled(true);

                        presences.setEnd_your_Shift("");
                        presences.setButtonPauseEndEnabled(true);

                        shiftRef.setValue(presences)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Firebase", "Presences updated successfully!");
                                        } else {
                                            Log.e("Firebase", "Failed to update Presences", task.getException());
                                        }
                                    }
                                });

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error reading shift data: " + error.getMessage());
                }
            });
        }
    }


    /**
     * Ends the worker's shift.
     * Records the end time in Firebase, updates the worker's status to 'not in shift',
     * and stops the {@link ShiftService}.
     */
    private void endShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();
            final DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

            shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        presences = snapshot.getValue(Presences.class);
                    }
                    if (presences == null) presences = new Presences();

                    if (presences.getButtonEndEnabled() != null && presences.getButtonEndEnabled()) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "You have already ended the shift", Toast.LENGTH_SHORT).show();
                        }
                        if (button_end_shift != null) button_end_shift.setEnabled(false);
                        return;
                    } else {

                        if (getActivity() != null) {
                            Intent serviceIntent = new Intent(getActivity(), ShiftService.class);
                            getActivity().stopService(serviceIntent);
                        }

                        DatabaseReference inShiftRef = FBRef.refBase.child(workerId).child("inShift");
                        inShiftRef.setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "inShift updated successfully!");
                                } else {
                                    Log.e("Firebase", "Failed to update inShift", task.getException());
                                }
                            }
                        });


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        final String currentTime = sdf.format(calendar.getTime());

                        presences.setEnd_your_Shift(currentTime);
                        if (button_end_shift != null) button_end_shift.setEnabled(false);

                        presences.setButtonEndEnabled(true);

                        shiftRef.setValue(presences)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Firebase", "Presences updated successfully!");
                                        } else {
                                            Log.e("Firebase", "Failed to update Presences", task.getException());
                                        }
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error reading shift data: " + error.getMessage());
                }
            });
        }
    }

}
