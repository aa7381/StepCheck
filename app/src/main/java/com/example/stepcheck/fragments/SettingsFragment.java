package com.example.stepcheck.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.stepcheck.utils.FBRef.refAuth;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stepcheck.activities.CreditsActivity;
import com.example.stepcheck.utils.FBRef;
import com.example.stepcheck.R;
import com.example.stepcheck.activities.Welcome_app;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass that displays the settings screen.
 * This fragment provides the user with an option to sign out of the application.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnCreateContextMenuListener {

    AlertDialog.Builder adb;
    private Button Sign_out;
    private ListView lvSettings;
    int position = 0;
    private String[] list_information = {"Change Name", "Change Email", "Change Password", "Change Rank", "Credits"};
    
    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Sign_out = view.findViewById(R.id.sign_out);
        lvSettings = view.findViewById(R.id.lvSettings);


        if (Sign_out != null) {
            Sign_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        }
        lvSettings.setOnItemClickListener(this);
        lvSettings.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list_information);
        lvSettings.setAdapter(adapter);

    }

    /**
     * Logs the user out of the application.
     * This method signs the user out of Firebase Authentication, clears the "Remember Me" preference,
     * and navigates the user back to the Welcome screen.
     */
    private void logout() {
        refAuth.signOut();
        SharedPreferences settings = requireActivity().getSharedPreferences("RemeberMe", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", false);
        editor.commit();

        Intent intent = new Intent(requireActivity(), Welcome_app.class);
        startActivity(intent);
        requireActivity().finish();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowid) {
        position = pos;
        
        if (position == 4) { // Credits
            Intent intent = new Intent(requireContext(), CreditsActivity.class);
            startActivity(intent);
            return;
        }

        final FirebaseUser user = FBRef.refAuth.getCurrentUser();

        if (user == null)
        {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        final String workerId = user.getUid();
        AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());

        if (position == 0) {
            final EditText et = new EditText(requireContext());
            adb.setView(et);
            adb.setTitle("Change Name");
            et.setHint("Enter new name");

            adb.setPositiveButton("Save", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {

                    final String newName = et.getText().toString().trim();

                    DatabaseReference ref = FBRef.refBase.child(workerId).child("username");

                    ref.setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Name updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

        else if (position == 1) {
            final EditText et = new EditText(requireContext());
            adb.setView(et);
            adb.setTitle("Change Email");
            et.setHint("Enter new email");

            adb.setPositiveButton("Save", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {

                    final String newEmail = et.getText().toString().trim();

                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Email update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

        else if (position == 2) { // change password
            final EditText et = new EditText(requireContext());
            adb.setView(et);
            adb.setTitle("Change Password");
            et.setHint("Enter new password");

            adb.setPositiveButton("Save", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {

                    final String newPass = et.getText().toString().trim();

                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Password update failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        else if(position == 3)
        {
            adb.setTitle("Change Rank");
            final String[] roles = {"Worker", "ShiftManager", "SupplyManager"};
            final android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
            spinner.setPadding(64, 32, 64, 32);
            ArrayAdapter<String> adapterRank = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles);
            spinner.setAdapter(adapterRank);
            adb.setView(spinner);

            adb.setPositiveButton("Request", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    final String selectedRank = roles[spinner.getSelectedItemPosition()];
                    final int roleIndex = spinner.getSelectedItemPosition();
                    // Generate a random code each time
                    final String verificationCode = String.valueOf((int)(Math.random() * 9000) + 1000);

                    // Search for a Shift Manager who is CURRENTLY IN SHIFT
                    FBRef.refBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String currentRank = snapshot.child(workerId).child("job_rank").getValue(String.class);
                            if (selectedRank.equals(currentRank)) {
                                Toast.makeText(getContext(), "You are already " + currentRank, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String tempManagerName = "";
                            String tempManagerId = "";
                            String requesterName = snapshot.child(workerId).child("username").getValue(String.class);
                            if (requesterName == null) requesterName = "Worker";

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String rank = ds.child("job_rank").getValue(String.class);
                                Boolean inShift = ds.child("inShift").getValue(Boolean.class);
                                
                                if ("ShiftManager".equals(rank) && Boolean.TRUE.equals(inShift)) {
                                    tempManagerName = ds.child("username").getValue(String.class);
                                    tempManagerId = ds.getKey();
                                    break;
                                }
                            }

                            final String managerId = tempManagerId;
                            final String managerName = tempManagerName;
                            final String finalRequesterName = requesterName;

                            if (!managerName.isEmpty()) {
                                // SEND the code to the manager via Firebase
                                final DatabaseReference requestRef = FBRef.FBDB.getReference("RankRequests").child(managerId);
                                Map<String, Object> requestData = new HashMap<>();
                                requestData.put("workerId", workerId);
                                requestData.put("workerName", finalRequesterName);
                                requestData.put("requestedRank", selectedRank);
                                requestData.put("code", verificationCode);
                                requestData.put("timestamp", System.currentTimeMillis());
                                requestData.put("seenByManager", false); // Flag to show dialog only once
                                
                                requestRef.setValue(requestData);

                                Toast.makeText(getContext(), "Code sent to Manager " + managerName, Toast.LENGTH_LONG).show();

                                AlertDialog.Builder codeAdb = new AlertDialog.Builder(requireContext());
                                codeAdb.setTitle("Verification");
                                codeAdb.setMessage("Ask " + managerName + " for the code (Valid for 3 minutes):");
                                final EditText codeEt = new EditText(requireContext());
                                codeAdb.setView(codeEt);
                                codeAdb.setCancelable(false);
                                
                                final AlertDialog codeDialog = codeAdb.create();

                                codeDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Verify", new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                        if (codeEt.getText().toString().trim().equals(verificationCode)) {
                                            if (timeoutRunnable != null) timeoutHandler.removeCallbacks(timeoutRunnable);
                                            
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("job_rank", selectedRank);
                                            updates.put("canEditInventory", roleIndex == 2);
                                            updates.put("can_manage_shift", roleIndex == 1);

                                            FBRef.refBase.child(workerId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Rank updated successfully", Toast.LENGTH_SHORT).show();
                                                        requestRef.removeValue();
                                                    } else {
                                                        Toast.makeText(getContext(), "Error updating rank", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Wrong code", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                codeDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                        if (timeoutRunnable != null) timeoutHandler.removeCallbacks(timeoutRunnable);
                                        requestRef.removeValue();
                                        dialogInterface.dismiss();
                                    }
                                });

                                timeoutRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (codeDialog.isShowing()) {
                                            codeDialog.dismiss();
                                            requestRef.removeValue();
                                            if (getContext() != null) {
                                                Toast.makeText(getContext(), "Request timed out", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                };
                                timeoutHandler.postDelayed(timeoutRunnable, 180000);

                                codeDialog.show();
                            } else {
                                Toast.makeText(getContext(), "No Shift Manager currently in shift to approve change", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        adb.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        adb.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }
}
