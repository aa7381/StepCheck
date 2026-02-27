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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass that displays the settings screen.
 * This fragment provides the user with an option to sign out of the application.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemLongClickListener,View.OnCreateContextMenuListener
{

    AlertDialog.Builder adb;

    private Button Sign_out ;
    private ListView lvSettings;

    int position = 0 ;


    private String[] list_information = {"change name", "change email", "change password"};



    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
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
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Sign_out = view.findViewById(R.id.sign_out);
        lvSettings = view.findViewById(R.id.lvSettings);


        if (Sign_out != null) {
            Sign_out.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        }
        lvSettings.setOnItemLongClickListener(this);

        lvSettings.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list_information);
        lvSettings.setAdapter(adapter);
        lvSettings.setOnCreateContextMenuListener(this);


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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add("change ");

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowid)
    {
        position = pos ;

        return false;
    }


    public boolean onContextItemSelected (MenuItem item)
    {
        FirebaseUser user = FBRef.refAuth.getCurrentUser();
        final String workerId = user.getUid();

        if(position == 0)
        {
            DatabaseReference inShiftRef = FBRef.refBase.child(workerId).child("username");
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
        }

        return super.onContextItemSelected(item);
    }

}
