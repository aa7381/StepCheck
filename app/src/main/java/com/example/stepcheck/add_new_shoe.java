package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;
import static com.example.stepcheck.FBRef.refBase2;
import static com.example.stepcheck.FBRef.refBase3;
import static com.example.stepcheck.FBRef.refStorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.UUID;

/**
 * Activity for adding a new shoe to the inventory.
 * This screen allows the user to scan a QR code, enter shoe details, select an image, and save the new shoe to the database.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class add_new_shoe extends MasterClass  {

    private Button ScanQR ;
    private String qr_code_data = "";

    private EditText etShoeName, etShoeType, etPrice, etmanufacturing_company, etColor;
    private static final int REQUEST_PICK_IMAGE = 300;

    private String fileName ;
    String safeKey ;
    Uri imageUri ;

    int count_shoes =0 ;


    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            qr_code_data = result.getContents();
            safeKey = Base64.encodeToString(qr_code_data.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

            Toast.makeText(this, "Scanned: " + qr_code_data, Toast.LENGTH_SHORT).show();
        }
    });

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shoe);

        ScanQR = findViewById(R.id.ScanQR);
        etShoeName = findViewById(R.id.etShoeName);
        etShoeType = findViewById(R.id.etShoeType);
        etPrice = findViewById(R.id.etPrice);
        etColor = findViewById(R.id.etColor);
        etmanufacturing_company = findViewById(R.id.etmanufacturing_company);

        ScanQR.setOnClickListener(view -> scanCode());
    }
    /**
     * Initiates the QR code scanning process.
     * Configures and launches the barcode scanner.
     */
    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * Handles the click event for the 'upload image' button.
     * It checks if a QR code has been scanned first and then opens the image gallery for the user to pick an image.
     * @param view The view that was clicked.
     */
    public void upload_image(View view)
    {
        if(safeKey ==null || safeKey.isEmpty())
        {
            Toast.makeText(this, "Please scan the QR code first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent si = new Intent(Intent.ACTION_PICK, android. provider.MediaStore. Images.Media. EXTERNAL_CONTENT_URI);
            startActivityForResult(si, REQUEST_PICK_IMAGE);
        }
    }

    /**
     * Callback for the result from launching the Intent for picking an image.
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
        }
    }

    /**
     * Uploads the selected image to Firebase Storage.
     * @param imageUri The Uri of the image to be uploaded.
     */
    private void uploadImage(Uri imageUri) {
        if (imageUri == null)
        {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            fileName = UUID.randomUUID().toString() + ".jpg";

            StorageReference reFfile = refStorage.child("shoes").child(safeKey).child(fileName);

            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading...");
            pd.show();

            UploadTask uploadTask = reFfile. putFile(imageUri);
            uploadTask. addOnSuccessListener(new OnSuccessListener<UploadTask. TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask. TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(add_new_shoe.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
                }).addOnFailureListener(new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e){
                        pd.dismiss();
                        Toast.makeText(add_new_shoe.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });

        } catch (Exception e) {
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Saves the new shoe's information to the Firebase Realtime Database.
     * It validates the input fields, checks if a shoe with the same QR code already exists,
     * and then uploads the image and saves the shoe data.
     * @param view The view that was clicked.
     */
    public void Save_shoe(View view) {
        String shoeName = etShoeName.getText().toString();
        String shoeType = etShoeType.getText().toString();
        String color = etColor.getText().toString();
        String manufacturing_company = etmanufacturing_company.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (shoeName.isEmpty() || shoeType.isEmpty() || priceStr.isEmpty() || safeKey.isEmpty() || manufacturing_company.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Information is missing", Toast.LENGTH_SHORT).show();
        } else {
            try {
                double price = Double.parseDouble(priceStr);





                refBase2.child(safeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {



                            Toast.makeText(getApplicationContext(), "Shoe with this QR already exists", Toast.LENGTH_SHORT).show();
                        } else {


                            count_shoes = count_shoes + 13 ;


                            refBase3.child("count_shoes").setValue(count_shoes);

                            uploadImage(imageUri);
                            Shoes shoes = new Shoes(safeKey, shoeName, color, shoeType, price, manufacturing_company);
                            refBase2.child(safeKey).setValue(shoes).addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Shoe saved successfully", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getApplicationContext(), "Failed to save shoe", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
        }

    /**
     * Finishes the current activity and returns to the previous screen.
     * @param view The view that was clicked.
     */
    public void back(View view) {

        finish();
    }
}
