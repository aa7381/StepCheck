package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Shoe_information extends MasterClass {

    private TextView shoeTitle, fitType, priceText;
    private String qr_code_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_information);

        shoeTitle = findViewById(R.id.shoeTitle);
        fitType = findViewById(R.id.fitType);
        priceText = findViewById(R.id.priceText);

        Intent intent = getIntent();
        qr_code_data = intent.getStringExtra("qr_code_data");

        if (qr_code_data != null && !qr_code_data.isEmpty()) {
            give_all_inform();
        }
    }

    private void give_all_inform() {
        refBase2.child(qr_code_data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shoeName = snapshot.child("shoe_name").getValue(String.class);
                    String shoeType = snapshot.child("shoe_type").getValue(String.class);
                    Double priceDouble = snapshot.child("price").getValue(Double.class);
                    if (priceDouble != null) {
                        priceText.setText(String.valueOf(priceDouble));
                    }
                    shoeTitle.setText(shoeName);
                    fitType.setText(shoeType);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void Close_Scan(View view) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new QrCodeMainScreenFragment())
                .addToBackStack(null)
                .commit();
    }
}
