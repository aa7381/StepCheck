package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * An activity that provides the user with a login interface.
 * This screen allows users to log in to the application using their email and password.
 * It also provides a "Remember Me" option to keep the user logged in.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class Login extends MasterClass {

    Button login_button;
    EditText email_input,password_input;
    CheckBox remember_checkbox;
    Boolean remember_me = false;

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
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.login_button);
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        remember_checkbox = findViewById(R.id.remember_checkbox);
    }

    /**
     * Handles the click event for the Login button.
     * It validates user input, attempts to sign in the user with Firebase Authentication,
     * and navigates to the main screen on success.
     *
     * @param view The view that was clicked.
     */
    public void Login_click(View view)
    {
        String email = email_input.getText().toString();
        String pass = password_input.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else{
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.show();
            pd.setCancelable(false);

            refAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Auth", "Login success");

                                remember_me = remember_checkbox.isChecked();
                                if (remember_me) {
                                    Intent intent = new Intent(Login.this, Qr_Code_main_Screen.class);
                                    startActivity(intent);
                                    SharedPreferences settings = getSharedPreferences("RemeberMe", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect", true);
                                    editor.commit();
                                }
                                else
                                {
                                    Intent intent = new Intent(Login.this, Qr_Code_main_Screen.class);
                                    startActivity(intent);
                                    SharedPreferences settings = getSharedPreferences("RemeberMe", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("stayConnect", false);
                                    editor.commit();
                                }
                                finish();

                            }   else {
                                Exception e = task.getException();
                                if (e instanceof FirebaseAuthInvalidUserException)
                                    Toast.makeText(Login.this, "Invalid info", Toast.LENGTH_SHORT).show();
                                else if (e instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(Login.this, "Invalid info", Toast.LENGTH_SHORT).show();
                                else if (e instanceof FirebaseNetworkException)
                                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Login.this, "Login failed try again later", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                        }
                    });
        }
    }

    /**
     * Handles the click event for the Sign-up button.
     * Starts the {@link Register} activity.
     *
     * @param view The view that was clicked.
     */
    public void Sign_up(View view)
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
