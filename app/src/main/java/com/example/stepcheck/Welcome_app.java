package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;

/**
 * The initial screen of the application.
 * This activity checks if the user is already logged in with "Remember Me" checked,
 * and if so, navigates directly to the main screen. Otherwise, it presents
 * the user with options to log in or sign up.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class Welcome_app extends MasterClass {

    Button register_button;
    Button login_button;

    /**
     * Called when the activity is first created. This is where you should do all
     * of your normal static set up: create views, bind data to lists, etc.
     * This method also sets up the user interface from the layout resource.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_app);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
    }

    /**
     * Called after {@link #onCreate} or {@link #onRestart} followed by
     * {@link #onStart}. This is where the activity begins to interact with the user.
     * It checks for a persisted login and redirects to the main screen if applicable.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Boolean isChecked = getSharedPreferences("RemeberMe", MODE_PRIVATE).getBoolean("stayConnect", false);
        if (refAuth.getCurrentUser() != null && isChecked) {
            FirebaseUser user = refAuth.getCurrentUser();
            Intent si = new Intent(this, Qr_Code_main_Screen.class);
            startActivity(si);
            finish();
        }
    }

    /**
     * Handles the click event for the Login button.
     * Starts the {@link Login} activity.
     *
     * @param view The view that was clicked.
     */
    public void Login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Handles the click event for the Sign-up button.
     * Starts the {@link Register} activity.
     *
     * @param view The view that was clicked.
     */
    public void Sign_up(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
