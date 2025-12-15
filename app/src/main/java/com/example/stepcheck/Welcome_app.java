package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

/**
 * The initial screen of the application.
 * This activity serves as the entry point, providing users with options to either log in
 * or register. It also checks if a user is already logged in and, if so, redirects them
 * directly to the main screen.
 */
public class Welcome_app extends AppCompatActivity {

    Button register_button;
    Button login_button;

    /**
     * Called when the activity is first created. Initializes the UI components.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_app);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
    }

    /**
     * Called when the activity is becoming visible to the user.
     * Checks if the user is already logged in and if the "Remember Me" option was checked.
     * If so, it skips the welcome screen and navigates directly to the main application screen.
     */
    @Override
    protected void onStart() {
        super. onStart();
        Boolean isChecked = getSharedPreferences("RemeberMe", MODE_PRIVATE).getBoolean("stayConnect", false);
        if (refAuth.getCurrentUser() != null && isChecked) {
            FirebaseUser user = refAuth.getCurrentUser();
            Intent si = new Intent(this, Qr_Code_main_Screen. class);
            startActivity(si);
            finish();

        }
    }

    /**
     * Navigates the user to the Login screen.
     *
     * @param view The view that was clicked.
     */
    public void Login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Navigates the user to the Register screen.
     *
     * @param view The view that was clicked.
     */
    public void Sign_up(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
