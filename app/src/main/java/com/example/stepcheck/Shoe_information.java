package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;
import static com.example.stepcheck.FBRef.refStorage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class Shoe_information extends MasterClass {

    private TextView shoeTitle, fitType, priceText;
    private String qr_code_data;

    private ImageView shoeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_information);

        shoeTitle = findViewById(R.id.shoeTitle);
        fitType = findViewById(R.id.fitType);
        priceText = findViewById(R.id.priceText);
        shoeImage = findViewById(R.id.shoeImage);


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
                    String shoeType = snapshot.child("type").getValue(String.class);
                    Double priceDouble = snapshot.child("price").getValue(Double.class);
                    if (priceDouble != null) {
                        priceText.setText(String.valueOf(priceDouble));
                    }
                    down();
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

    private void down() {
        StorageReference shoeFolderRef = refStorage.child("shoes").child(qr_code_data);

        shoeFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    if (!listResult.getItems().isEmpty()) {
                        StorageReference firstFileRef = listResult.getItems().get(0);

                        final long MAX_SIZE = 5 * 1024 * 1024; // עד 5MB
                        firstFileRef.getBytes(MAX_SIZE)
                                .addOnSuccessListener(bytes -> {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    shoeImage.setImageBitmap(bitmap);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Shoe_information.this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(Shoe_information.this, "No images found for this shoe", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Shoe_information.this, "Failed to list images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
