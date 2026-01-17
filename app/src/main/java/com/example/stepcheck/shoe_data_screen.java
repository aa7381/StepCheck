package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;
import static com.example.stepcheck.FBRef.refBase3;
import static com.example.stepcheck.FBRef.refBase4;
import static com.example.stepcheck.FBRef.refStorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class shoe_data_screen extends MasterClass implements AdapterView.OnItemSelectedListener {

        Spinner spinnerQuantitySize;
        Spinner spinnerSizeGender;
        Spinner spinnerSizeType;

        EditText ShoeType, shoeName , shoePrice ;

        ImageView imgShoe;

        EditText etQuantity;


        String qr_code_data;





        int i = 0 ;

    private static final int REQUEST_PICK_IMAGE = 300;
    private Uri imageUri;






    private String[] sizeType = {"adult", "adult_kids", "youngerkids","babies"};

    private String[] adult_sizes = { "usMen", "usWomen", "euSizes", "ukSizes",};
    private String[] adult_kids_sizes = {"usKidsY","ukKidsY","euKidsY"};
    private String[] youngerkids_sizes = {"usYoungerKids","ukYoungerKids","euYoungerKids"};
    private String[] babies_sizes = {"usBabies","ukBabies","euBabies"};


    int selectedSizeTypePosition = 0;

    int selectedSizeGenderPosition = 0;

    int selectedWomenus =0 ;


    boolean isexist = false;





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


    String[][] sizes ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_data_screen);

        spinnerSizeGender = findViewById(R.id.spinnerGenderType);
        spinnerSizeType = findViewById(R.id.spinnerSizeType);
        spinnerQuantitySize = findViewById(R.id.spinnerSelectSize);
        imgShoe = findViewById(R.id.imgShoe);
        ShoeType = findViewById(R.id.ShoeType);
        shoeName = findViewById(R.id.ShoeName);
        shoePrice = findViewById(R.id.ShoePrice);
        etQuantity = findViewById(R.id.etQuantity);







        give_count_shoe();

        Intent intent = getIntent();
        qr_code_data = intent.getStringExtra("qr_code_data");





        spinnerSizeType.setOnItemSelectedListener(this);
        spinnerSizeGender.setOnItemSelectedListener(this);
        spinnerQuantitySize.setOnItemSelectedListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizeType);
        spinnerSizeGender.setAdapter(adp);



        spinnerQuantitySize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (sizes == null) {
                    etQuantity.setText("0");
                    return;
                }

                String selectedSize = parent.getItemAtPosition(position).toString();
                String safeSelectedSize = selectedSize.replace(".", "_");

                String sizeGender = "";
                switch (selectedSizeTypePosition) {
                    case 0: sizeGender = "adult_size"; break;
                    case 1: sizeGender = "kids_size"; break;
                    case 2: sizeGender = "youngerkids_size"; break;
                    case 3: sizeGender = "babies_size"; break;
                }

                String sizeType = spinnerSizeType.getSelectedItem().toString();

                int row = -1;
                for (int i = 0; i < sizes.length; i++) {
                    if (sizes[i][0] != null &&
                            sizes[i][1] != null &&
                            sizes[i][2] != null &&
                            sizes[i][0].equals(qr_code_data) &&
                            sizes[i][1].equals(sizeGender) &&
                            sizes[i][2].equals(sizeType)) {

                        row = i;
                        break;
                    }
                }

                if (row == -1) {
                    etQuantity.setText("0");
                    return;
                }

                String qty = "0";
                for (int col = 3; col + 1 < sizes[row].length; col += 2) {
                    if (sizes[row][col] != null &&
                            sizes[row][col].equals(safeSelectedSize)) {

                        qty = sizes[row][col + 1];
                        break;
                    }
                }

                etQuantity.setText(qty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                etQuantity.setText("0");
            }
        });




        spinnerSizeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selectedSizeTypePosition == 0)
                {
                    switch (position){
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, usMenSizes);
                            spinnerQuantitySize.setAdapter(adp);

                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, usWomenSizes);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, euSizes);
                            spinnerQuantitySize.setAdapter(adp3);
                            break;
                        case 3:
                            ArrayAdapter<String> adp4 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, ukSizes);
                            spinnerQuantitySize.setAdapter(adp4);
                            break;
                        default:
                            break;
                    }
                }
                else if(selectedSizeTypePosition == 1) {
                    switch (position) {
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, usKidsY);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item,ukKidsY);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, euKidsY);
                            spinnerQuantitySize.setAdapter(adp3);
                            break;
                        default:
                            break;

                    }
                }
                else if(selectedSizeTypePosition == 2)
                {
                    switch(position)
                    {
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, usYoungerKids);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, ukYoungerKids);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, euYoungerKids);
                            spinnerQuantitySize.setAdapter(adp3);
                        default:
                            break;
                    }
                }
                else if(selectedSizeTypePosition == 3)
                {
                    switch(position)
                    {
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, usBabies);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, ukBabies);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(shoe_data_screen.this, android.R.layout.simple_spinner_dropdown_item, euBabies);
                            spinnerQuantitySize.setAdapter(adp3);
                        default:
                            break;
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                selectedSizeTypePosition = position;
                selectedSizeGenderPosition = position;
                ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, adult_sizes);
                spinnerSizeType.setAdapter(adp);
                break;
            case 1:
                selectedSizeTypePosition = position;
                selectedSizeGenderPosition = position;
                ArrayAdapter<String> adp1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, adult_kids_sizes);
                spinnerSizeType.setAdapter(adp1);
                break;
            case 2:
                selectedSizeTypePosition = position;
                selectedSizeGenderPosition = position;
                ArrayAdapter<String> adp2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, youngerkids_sizes);
                spinnerSizeType.setAdapter(adp2);
                break;
            case 3:
                selectedSizeTypePosition = position;
                selectedSizeGenderPosition = position;
                ArrayAdapter<String> adp3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, babies_sizes);
                spinnerSizeType.setAdapter(adp3);
                break;
            default:
                break;
        }
    }

    private void down() {
        StorageReference shoeFolderRef = refStorage.child("shoes").child(qr_code_data);

        shoeFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    if (!listResult.getItems().isEmpty()) {
                        StorageReference firstFileRef = listResult.getItems().get(0);

                        final long MAX_SIZE = 5 * 1024 * 1024;
                        firstFileRef.getBytes(MAX_SIZE)
                                .addOnSuccessListener(bytes -> {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imgShoe.setImageBitmap(bitmap);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(shoe_data_screen.this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(shoe_data_screen.this, "No images found for this shoe", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(shoe_data_screen.this, "Failed to list images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void give_all_inform() {
        refBase2.child(qr_code_data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shoeType = snapshot.child("type").getValue(String.class);
                    String shoe_Name = snapshot.child("shoe_name").getValue(String.class);
                    Double priceDouble = snapshot.child("price").getValue(Double.class);
                    if (priceDouble != null) {
                        shoePrice.setText(String.valueOf(priceDouble));
                    }

                    shoeName.setText(shoe_Name);
                    ShoeType.setText(shoeType);
                    down();
                    loadSizes(qr_code_data);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void give_count_shoe()
    {
        refBase3.child("count_shoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int how_much_shoes = snapshot.getValue(Integer.class);


                    sizes = new String[how_much_shoes][3+36];


                    if (qr_code_data != null && !qr_code_data.isEmpty()) {
                        give_all_inform();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }












    private void saveAllSizesToFirebase() {
        if (sizes == null) {
            Toast.makeText(this, "Sizes array is null", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < sizes.length; i++) {
            String qr = sizes[i][0];
            String sizeGender = sizes[i][1];
            String sizeType = sizes[i][2];

            if (qr != null && sizeGender != null && sizeType != null) {

                for (int j = 3; j + 1 < sizes[i].length; j += 2) {
                    String sizeValue = sizes[i][j];
                    String qtyValue = sizes[i][j + 1];

                    if (sizeValue != null) {
                        if (qtyValue == null || qtyValue.isEmpty()) {
                            qtyValue = "0";
                        }

                        String safeSizeValue = sizeValue.replace(".", "_");

                        refBase4.child(qr).child(sizeGender).child(sizeType).child(safeSizeValue).setValue(qtyValue);
                    }
                }
            }
        }

        Toast.makeText(this, "All sizes saved to Firebase", Toast.LENGTH_SHORT).show();
    }


    public void save_size(View view) {
        if (sizes == null) return;

        String selectedSize = spinnerQuantitySize.getSelectedItem().toString();
        String quantity = etQuantity.getText().toString();
        if (quantity.isEmpty()) return;

        String sizeGender = "";
        String sizeType = "";

        switch (selectedSizeTypePosition) {
            case 0: sizeGender = "adult_size"; break;
            case 1: sizeGender = "kids_size"; break;
            case 2: sizeGender = "youngerkids_size"; break;
            case 3: sizeGender = "babies_size"; break;
        }
        sizeType = spinnerSizeType.getSelectedItem().toString();

        int row = -1;

        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i][0] != null &&
                    sizes[i][0].equals(qr_code_data) &&
                    sizes[i][1].equals(sizeGender) &&
                    sizes[i][2].equals(sizeType)) {
                if (row == -1) {
                    row = i;
                }
            }
        }

        if (row == -1) {
            for (int i = 0; i < sizes.length; i++) {
                if (sizes[i][0] == null) {
                    sizes[i][0] = qr_code_data;
                    sizes[i][1] = sizeGender;
                    sizes[i][2] = sizeType;
                    row = i;
                    break;
                }
            }
        }

        if (row == -1) {
            Toast.makeText(this, "No space for new shoe", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean added = false;
        for (int k = 3; k + 1 < sizes[row].length; k += 2) {
            if (sizes[row][k] == null) {
                sizes[row][k] = selectedSize;
                sizes[row][k + 1] = quantity;
                added = true;
                break;
            } else if (sizes[row][k].equals(selectedSize)) {
                sizes[row][k + 1] = quantity;
                added = true;
                break;
            }
        }

        if (added) {
            Toast.makeText(this, "Size saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No space in this row for more sizes", Toast.LENGTH_SHORT).show();
        }
    }




    private void loadSizes(String qrCode) {
        if (sizes == null) return;

        refBase4.child(qrCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                for (DataSnapshot genderSnap : snapshot.getChildren()) {
                    String sizeGender = genderSnap.getKey();

                    for (DataSnapshot typeSnap : genderSnap.getChildren()) {
                        String sizeType = typeSnap.getKey();

                        int row = -1;

                        for (int i = 0; i < sizes.length; i++) {
                            if (sizes[i][0] != null &&
                                    sizes[i][0].equals(qrCode) &&
                                    sizes[i][1].equals(sizeGender) &&
                                    sizes[i][2].equals(sizeType)) {
                                if (row == -1) {
                                    row = i;
                                }
                            }
                        }

                        if (row == -1) {
                            for (int i = 0; i < sizes.length; i++) {
                                if (sizes[i][0] == null) {
                                    sizes[i][0] = qrCode;
                                    sizes[i][1] = sizeGender;
                                    sizes[i][2] = sizeType;
                                    row = i;
                                    break;
                                }
                            }
                        }

                        if (row == -1) {
                            Toast.makeText(shoe_data_screen.this, "No space in sizes array", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int col = 3;
                        for (DataSnapshot sizeSnap : typeSnap.getChildren()) {
                            String sizeValue = sizeSnap.getKey();
                            Object qtyObj = sizeSnap.getValue();
                            String qtyValue;

                            if (qtyObj == null) {
                                qtyValue = "0";
                            } else {
                                qtyValue = qtyObj.toString();
                            }

                            sizes[row][col] = sizeValue;
                            sizes[row][col + 1] = qtyValue;

                            if (col == 3) {
                                etQuantity.setText(qtyValue);
                            }

                            col += 2;

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(shoe_data_screen.this, "Failed to load sizes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void save_update(View view)
    {
        saveAllSizesToFirebase();

        if(shoeName.getText().toString().isEmpty() || ShoeType.getText().toString().isEmpty() || shoePrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
        else {
            refBase2.child(qr_code_data).child("shoe_name").setValue(shoeName.getText().toString());
            refBase2.child(qr_code_data).child("type").setValue(ShoeType.getText().toString());
            refBase2.child(qr_code_data).child("price").setValue(shoePrice.getText().toString());
            finish();
        }
    }

    public void backScreen(View view)
    {
        finish();

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void replaceShoeImage(Uri newImageUri) {

        if (newImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference shoeFolderRef = refStorage.child("shoes").child(qr_code_data);

        shoeFolderRef.listAll().addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.delete()
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete: " + fileRef.getName(), e));
                    }

                    String fileName = "main.jpg"; // שם קבוע
                    StorageReference newImageRef = shoeFolderRef.child(fileName);

                    ProgressDialog pd = new ProgressDialog(this);
                    pd.setTitle("Uploading new image...");
                    pd.show();

                    newImageRef.putFile(newImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                pd.dismiss();
                                Toast.makeText(shoe_data_screen.this, "Image replaced successfully", Toast.LENGTH_SHORT).show();
                                Bitmap bitmap = null;
                                try {
                                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(newImageUri));
                                    imgShoe.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .addOnFailureListener(e -> {
                                pd.dismiss();
                                Toast.makeText(shoe_data_screen.this, "Failed to upload new image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to list existing images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                replaceShoeImage(imageUri);
            }
        }
    }

    public void change_photo(View view)
    {

        Intent si = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(si, REQUEST_PICK_IMAGE);
    }
}