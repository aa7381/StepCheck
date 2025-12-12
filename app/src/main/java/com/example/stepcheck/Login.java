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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    Button login_button;
    EditText email_input,password_input;
    CheckBox remember_checkbox;
    Boolean remember_me = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.login_button);
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        remember_checkbox = findViewById(R.id.remember_checkbox);
    }

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

    public void Sign_up(View view)
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void logout() {
        refAuth.signOut();
        SharedPreferences settings = getSharedPreferences("RemeberMe", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", false);
        editor.commit();

        Intent intent = new Intent(this, Welcome_app.class);
        startActivity(intent);
        finish();
    }
}

