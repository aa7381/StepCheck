package com.example.stepcheck;

import static android.content.Context.MODE_PRIVATE;
import static com.example.stepcheck.FBRef.refAuth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass that displays the settings screen.
 * This fragment provides the user with an option to sign out of the application.
 */
public class SettingsFragment extends Fragment {

    private Button Sign_out ;

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

        Sign_out = view.findViewById(R.id.Sign_out);

        if (Sign_out != null) {
            Sign_out.setOnClickListener(v -> {
                logout();
            });
        }

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
    
}
