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
import android.view. View;
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


public class ShiftEntryFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Button button_start_shift,button_pause_shift ,button_end_shift,button_pause_shift_end;


    Boolean inShift = false;

    String currentDate = "";


    Presences presences ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shiftentryfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_end_shift = view.findViewById(R.id.button_end_shift);
        button_start_shift = view.findViewById(R.id.button_start_shift);
        button_pause_shift = view.findViewById(R.id.button_pause_shift);
        button_pause_shift_end = view.findViewById(R.id.button_pause_shift_end);


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

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
            } else {
                Toast.makeText(getActivity(), "Location permission is required to track your shift", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void infrom() {
        String workerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDate = sdf.format(calendar.getTime());

        DatabaseReference shiftRef = FBRef.refBase5.child(workerId).child(currentDate);

        shiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // טען את הנתונים הקיימים
                    presences = snapshot.getValue(Presences.class);
                } else {
                    // יצירת אובייקט חדש אם אין נתונים
                    presences = new Presences();
                }

                // עדכון הכפתורים לפי השדות הקיימים
                button_start_shift.setEnabled(presences.getStart_your_Shift() == null || presences.getStart_your_Shift().isEmpty());
                button_pause_shift.setEnabled(presences.getStart_your_Shift() != null && (presences.getPause_time() == null || presences.getPause_time().isEmpty()));
                button_pause_shift_end.setEnabled(presences.getPause_time() != null && (presences.getPause_end_time() == null || presences.getPause_end_time().isEmpty()));
                button_end_shift.setEnabled(presences.getStart_your_Shift() != null && (presences.getEnd_your_Shift() == null || presences.getEnd_your_Shift().isEmpty()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading shift data: " + error.getMessage());
            }
        });
    }




    private void startShift() {

        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();

            final DatabaseReference isStartShiftRef = FBRef.refBase5.child(workerId).child(currentDate).child("is_startShift");

            isStartShiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isStartShift = snapshot.getValue(Boolean.class);

                    if (isStartShift != null && isStartShift) {
                        Toast.makeText(getActivity(), "You have already started a shift", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        Intent serviceIntent = new Intent(getActivity(), ShiftService.class);
                        getActivity().startService(serviceIntent);

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
                        Toast.makeText(getActivity(), "Shift started at: " + presences.getStart_your_Shift(), Toast.LENGTH_SHORT).show();

                        button_start_shift.setEnabled(false);
                        button_pause_shift.setEnabled(true);
                        button_end_shift.setEnabled(true);

                        presences.setPause_time("");
                        presences.setPause_end_time("");
                        presences.setEnd_your_Shift("");
                        presences.setWorker_id(workerId);
                        presences.setIs_startShift(true);
                        presences.setTime(currentDate);

                        FBRef.refBase5.child(workerId).child(currentDate).setValue(presences)
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
                    Log.e("Firebase", "Error reading is_start_shift: " + error.getMessage());
                }
            });
        }
    }

    private void pauseShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();

            final DatabaseReference isStartShiftRef2 = FBRef.refBase5.child(workerId).child(currentDate).child("buttonPauseEnabled");

            isStartShiftRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isStartShift = snapshot.getValue(Boolean.class);

                    if (isStartShift != null && isStartShift) {
                        Toast.makeText(getActivity(), "You have already started a shift", Toast.LENGTH_SHORT).show();
                        button_pause_shift.setEnabled(false);
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
                        button_pause_shift_end.setEnabled(true);

                        presences.setPause_end_time("");
                        presences.setEnd_your_Shift("");
                        presences.setButtonPauseEnabled(true);

                        FBRef.refBase5.child(workerId).child(currentDate).setValue(presences)
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
                    Log.e("Firebase", "Error reading is_start_shift: " + error.getMessage());
                }
            });
        }
    }
    private void pauseShiftEnd() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();

            final DatabaseReference isStartShiftRef2 = FBRef.refBase5.child(workerId).child(currentDate).child("buttonPauseEndEnabled");

            isStartShiftRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isStartShift = snapshot.getValue(Boolean.class);

                    if (isStartShift != null && isStartShift) {
                        Toast.makeText(getActivity(), "You have already started a shift", Toast.LENGTH_SHORT).show();
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
                        button_pause_shift_end.setEnabled(false);
                        button_end_shift.setEnabled(true);

                        presences.setEnd_your_Shift("");
                        presences.setButtonPauseEndEnabled(true);

                        FBRef.refBase5.child(workerId).child(currentDate).setValue(presences)
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
                    Log.e("Firebase", "Error reading is_start_shift: " + error.getMessage());
                }
            });
        }
    }


    private void endShift() {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        if (user != null) {
            final String workerId = user.getUid();

            final DatabaseReference isStartShiftRef2 = FBRef.refBase5.child(workerId).child(currentDate).child("buttonendEnabled");

            isStartShiftRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isStartShift = snapshot.getValue(Boolean.class);

                    if (isStartShift != null && isStartShift) {
                        Toast.makeText(getActivity(), "You have already started a shift", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        Intent serviceIntent = new Intent(getActivity(), ShiftService.class);
                        getActivity().stopService(serviceIntent);

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
                        button_end_shift.setEnabled(false);

                        presences.setButtonEndEnabled(true);

                        FBRef.refBase5.child(workerId).child(currentDate).setValue(presences)
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
                    Log.e("Firebase", "Error reading is_start_shift: " + error.getMessage());
                }
            });
        }
    }

}
