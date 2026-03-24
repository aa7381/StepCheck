package com.example.stepcheck;

import static android.content.Context.MODE_PRIVATE;
import static com.example.stepcheck.FBRef.refAuth;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass that displays the settings screen.
 * This fragment provides the user with an option to sign out of the application.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemLongClickListener,View.OnCreateContextMenuListener {

    AlertDialog.Builder adb;

    private Button Sign_out;
    private ListView lvSettings;

    int position = 0;


    private String[] list_information = {"change name", "change email", "change password","change rank"};


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
        lvSettings.setOnItemLongClickListener(this);

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
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowid) {
        position = pos;
        final FirebaseUser user = FBRef.refAuth.getCurrentUser();

        if (user == null)
        {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return true;
        }

        final String workerId = user.getUid();
        AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
        final EditText et = new EditText(requireContext());
        adb.setView(et);

        if (position == 0) {
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

        adb.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        adb.show();

        return true;
    }
}





