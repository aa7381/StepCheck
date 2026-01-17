package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;
import static com.example.stepcheck.FBRef.refBase4;
import static com.example.stepcheck.FBRef.refStorage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.UUID;

public class Shoe_information extends MasterClass   {

    private TextView shoeTitle, fitType, priceText;
    private String qr_code_data;

    private ImageView shoeImage;

    private Spinner spinnerSizeType, spinnerSizeGender;

    GridLayout sizeGrid;
    ArrayList<Button> sizeButtons = new ArrayList<>();


    private String[] sizeType = {"adult", "adult_kids", "youngerkids","babies"};

    private String[] adult_sizes = { "usMen", "usWomen", "euSizes", "ukSizes",};
    private String[] adult_kids_sizes = {"usKidsY","ukKidsY","euKidsY"};
    private String[] youngerkids_sizes = {"usYoungerKids","ukYoungerKids","euYoungerKids"};
    private String[] babies_sizes = {"usBabies","ukBabies","euBabies"};


    String[] euSizes = {
            "35.5", "36", "36.5", "37.5", "38", "38.5", "39", "40", "40.5", "41",
            "42", "42.5", "43", "44", "44.5", "45", "45.5", "46"
    };

    String[] ukSizes = {
            "3", "3.5", "4", "4.5", "5", "5.5", "6", "6", "6.5", "7",
            "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11"
    };

    String[] usMenSizes = {
            "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8",
            "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12"
    };

    String[] usWomenSizes = {
            "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5",
            "10", "10.5", "11", "11.5", "12", "12.5", "13", "13.5"
    };

    // 🧒 Youth / Big Kids (US Y)
    String[] usKidsY = {
            "1Y","1.5Y","2Y","2.5Y","3Y","3.5Y","4Y","4.5Y","5Y","5.5Y","6Y","6.5Y","7Y"
    };

    String[] ukKidsY = {
            "1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6"
    };

    String[] euKidsY = {
            "32","33","33.5","34","35","35.5","36","36.5","37.5","38","38.5","39","40"
    };

    // 👦 Younger Kids –
    String[] usYoungerKids = {
            "8C","8.5C","9C","9.5C","10C","10.5C","11C","11.5C","12C","12.5C","13C","13.5C",
            "1Y","1.5Y","2Y","2.5Y","3Y"
    };

    String[] ukYoungerKids = {
            "1","1.5","2","2.5","7.5","8","8.5","9","9.5","10","10.5","11","11.5","12","12.5","13",
            "13.5"
    };

    String[] euYoungerKids = {
            "25","25.5","26","26.5","27","27.5","28","28.5","29.5","30","31","31.5",
            "32","33","33.5","34","35"
    };

    // 👶 Babies & Toddlers (C)
    String[] usBabies = {
            "1C","1.5C","2C","2.5C","3C","3.5C","4C","4.5C","5C","5.5C",
            "6C","6.5C","7C","7.5C","8C","8.5C","9C","9.5C"
    };

    String[] ukBabies = {
            "0.5","1","1.5","2","2.5","3","3.5","4","4.5","5",
            "5.5","6","6.5","7","7.5","8","8.5","9"
    };

    String[] euBabies = {
            "16","16.5","17","18","18.5","19","19.5","20","21","21.5",
            "22","22.5","23.5","24","25","25.5","26","26.5"
    };






    int selectedSizeTypePosition = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_information);

        shoeTitle = findViewById(R.id.shoeTitle);
        fitType = findViewById(R.id.fitType);
        priceText = findViewById(R.id.priceText);
        shoeImage = findViewById(R.id.shoeImage);
        spinnerSizeType = findViewById(R.id.SizeType);
        spinnerSizeGender = findViewById(R.id.spinnerGenderType);
        sizeGrid = findViewById(R.id.sizeGrid);

        for (int i = 0; i < sizeGrid.getChildCount(); i++) {
            View v = sizeGrid.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                sizeButtons.add(b);

                b.setEnabled(false);
                b.setAlpha(0.4f);
            }
        }







        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizeType);
        spinnerSizeGender.setAdapter(adp);


        Intent intent = getIntent();
        qr_code_data = intent.getStringExtra("qr_code_data");

        if (qr_code_data != null && !qr_code_data.isEmpty()) {
            give_all_inform();
        }


    spinnerSizeGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
            switch (position) {
                case 0:
                    selectedSizeTypePosition = position;
                    ArrayAdapter<String> adp2 = new ArrayAdapter<>(Shoe_information.this, android.R.layout.simple_spinner_dropdown_item, adult_sizes);
                    spinnerSizeType.setAdapter(adp2);
                    break;
                case 1:
                    selectedSizeTypePosition = position;
                    ArrayAdapter<String> adp1 = new ArrayAdapter<>(Shoe_information.this, android.R.layout.simple_spinner_dropdown_item, adult_kids_sizes);
                    spinnerSizeType.setAdapter(adp1);
                    break;
                case 2:
                    selectedSizeTypePosition = position;
                    ArrayAdapter<String> adp4 = new ArrayAdapter<>(Shoe_information.this, android.R.layout.simple_spinner_dropdown_item, youngerkids_sizes);
                    spinnerSizeType.setAdapter(adp4);
                    break;
                case 3:
                    selectedSizeTypePosition = position;
                    ArrayAdapter<String> adp3 = new ArrayAdapter<>(Shoe_information.this, android.R.layout.simple_spinner_dropdown_item, babies_sizes);
                    spinnerSizeType.setAdapter(adp3);
                    break;
                default:
                    break;
            }
        }
            @Override
            public void onNothingSelected (AdapterView < ? > parent)
            {

            }
    });

        spinnerSizeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sizeGender = "";
                String[] sizesArray = new String[0];
                String sizeType = "";

                switch (selectedSizeTypePosition) {
                    case 0: // adult
                        sizeGender = "adult_size";
                        switch (position) {
                            case 0:
                                sizesArray = usMenSizes;
                                sizeType = "usMen";
                                break;
                            case 1:
                                sizesArray = usWomenSizes;
                                sizeType = "usWomen";
                                break;
                            case 2:
                                sizesArray = euSizes;
                                sizeType = "euSizes";
                                break;
                            case 3:
                                sizesArray = ukSizes;
                                sizeType = "ukSizes";
                                break;
                        }
                        break;

                    case 1: // adult_kids
                        sizeGender = "kids_size";
                        switch (position) {
                            case 0:
                                sizesArray = usKidsY;
                                sizeType = "usKidsY";
                                break;
                            case 1:
                                sizesArray = ukKidsY;
                                sizeType = "ukKidsY";
                                break;
                            case 2:
                                sizesArray = euKidsY;
                                sizeType = "euKidsY";
                                break;
                        }
                        break;

                    case 2: // youngerkids
                        sizeGender = "youngerkids_size";
                        switch (position) {
                            case 0:
                                sizesArray = usYoungerKids;
                                sizeType = "usYoungerKids";
                                break;
                            case 1:
                                sizesArray = ukYoungerKids;
                                sizeType = "ukYoungerKids";
                                break;
                            case 2:
                                sizesArray = euYoungerKids;
                                sizeType = "euYoungerKids";
                                break;
                        }
                        break;

                    case 3: // babies
                        sizeGender = "babies_size";
                        switch (position) {
                            case 0:
                                sizesArray = usBabies;
                                sizeType = "usBabies";
                                break;
                            case 1:
                                sizesArray = ukBabies;
                                sizeType = "ukBabies";
                                break;
                            case 2:
                                sizesArray = euBabies;
                                sizeType = "euBabies";
                                break;
                        }
                        break;
                }

                showSizes(sizesArray, sizeGender, sizeType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



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

    private void showSizes(String[] sizesArray, String sizeGender, String sizeType) {

        for (int i = 0; i < sizeButtons.size(); i++) {
            Button b = sizeButtons.get(i);

            if (i < sizesArray.length) {
                String sizeText = sizesArray[i];
                b.setText(sizeText);

                String safeSize = sizeText.replace(".", "_");

                refBase4.child(qr_code_data)
                        .child(sizeGender)
                        .child(sizeType)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(safeSize)) {
                                    String qty = snapshot.child(safeSize).getValue(String.class);
                                    if (qty != null && !qty.equals("0")) {
                                        b.setEnabled(true);
                                        b.setAlpha(1f);
                                    } else {
                                        b.setEnabled(false);
                                        b.setAlpha(0.4f);
                                    }
                                } else {
                                    b.setEnabled(false);
                                    b.setAlpha(0.4f);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

            } else {
                b.setText("");
                b.setEnabled(false);
                b.setAlpha(0.0f);
            }
        }
    }
    public void Close_Scan(View view) {
        finish();
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
