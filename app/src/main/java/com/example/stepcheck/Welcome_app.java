package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class Welcome_app extends AppCompatActivity {

    Button register_button;
    Button login_button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_app);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);


    }

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

    public void Login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    public void Sign_up(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }


}