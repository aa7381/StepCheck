package com.example.stepcheck.activities;

import static com.example.stepcheck.utils.FBRef.refBase2;
import static com.example.stepcheck.utils.FBRef.refBase3;
import static com.example.stepcheck.utils.FBRef.refStorage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.stepcheck.utils.GeminiCallback;
import com.example.stepcheck.utils.GeminiManager;
import com.example.stepcheck.utils.Prompts;
import com.example.stepcheck.R;
import com.example.stepcheck.models.Shoes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.RGBLuminanceSource;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity that uses Gemini AI to automatically identify shoe details and decode QR codes from photos.
 * The user can take multiple photos or pick them from the gallery. The AI then processes these images 
 * to extract information like shoe name, brand, color, price, and the QR code ID.
 */
public class add_shoe_ai extends AppCompatActivity {

    /**
     * Buttons for adding photos, starting analysis, saving the shoe, and exiting.
     */
    private Button btnAddPhoto, btnAnalyze, btnAddShoe, btnExit;
    
    /**
     * ImageView to display the most recently added photo.
     */
    private ImageView imageView;
    
    /**
     * TextViews to display the AI analysis result and the count of added photos.
     */
    private TextView tvResult, tvCounter;

    /**
     * List of Bitmaps currently selected for analysis.
     */
    private ArrayList<Bitmap> selectedBitmaps = new ArrayList<>();
    
    /**
     * Request codes for image picking and camera capture.
     */
    private static final int REQUEST_PICK_IMAGE = 300;
    private static final int REQUEST_CAMERA_IMAGE = 301;
    
    /**
     * Permission code for camera access.
     */
    private static final int PERMISSION_CODE = 100;

    /**
     * Path to the photo currently being captured by the camera.
     */
    private String currentPhotoPath;
    
    /**
     * Base64 encoded safe key derived from the decoded QR code.
     */
    private String safeKey;
    
    /**
     * JSONObject containing the parsed details of the shoe returned by the AI.
     */
    private JSONObject shoeData;
    
    /**
     * Tag for logging purposes.
     */
    private final String TAG = "add_shoe_ai";
    
    /**
     * Local counter for the total number of shoes in the database.
     */
    private int count_shoes = 0;

    /**
     * Initializes the activity, sets up view references and click listeners.
     * Also fetches the current shoe count from Firebase.
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shoe_ai);

        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        btnAnalyze = (Button) findViewById(R.id.btnAnalyze);
        btnAddShoe = (Button) findViewById(R.id.btnAddShoe);
        btnExit = (Button) findViewById(R.id.btnExit);
        imageView = (ImageView) findViewById(R.id.imageView);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvCounter = (TextView) findViewById(R.id.tvCounter);

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeImage();
            }
        });

        btnAddShoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_shoe(v);
            }
        });

        refBase3.child("count_shoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    count_shoes = snapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Handles the click event for the 'Add Photo' button.
     * Shows a dialog to choose between the camera and gallery.
     * @param view The clicked view.
     */
    public void add_photo(View view) {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            checkPermissionsAndCapture();
                        } else {
                            openGallery();
                        }
                    }
                }).show();
    }

    /**
     * Opens the device's image gallery.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    /**
     * Checks if camera permissions are granted. If so, starts the camera; otherwise, requests permissions.
     */
    private void checkPermissionsAndCapture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            captureFromCamera();
        }
    }

    /**
     * Launches the camera activity to capture a new photo.
     * Creates a temporary file to store the image.
     */
    private void captureFromCamera() {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imgFile = File.createTempFile("shoe_" + System.currentTimeMillis(), ".jpg", storageDir);
            currentPhotoPath = imgFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.stepcheck.fileprovider", imgFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback for activity results (picking an image or capturing a photo).
     * Adds the selected image to the list of bitmaps.
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent containing the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (Exception e) { e.printStackTrace(); }
            } else if (requestCode == REQUEST_CAMERA_IMAGE) {
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            }
            if (bitmap != null) {
                selectedBitmaps.add(bitmap);
                imageView.setImageBitmap(bitmap);
                tvCounter.setText("Photos: " + selectedBitmaps.size());
            }
        }
    }

    /**
     * Iterates through all selected bitmaps and attempts to decode a QR code from each.
     * @return The decoded QR code text, or null if none was found.
     */
    private String decodeQRCodeFromAllImages() {
        for (Bitmap bitmap : selectedBitmaps) {
            String qr = decodeQRCode(bitmap);
            if (qr != null && !qr.isEmpty()) {
                return qr;
            }
        }
        return null;
    }

    /**
     * Attempts to decode a QR code from a single Bitmap using the ZXing library.
     * @param bitmap The Bitmap to scan.
     * @return The decoded text, or null if decoding failed.
     */
    private String decodeQRCode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(bBitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * Starts the AI analysis process.
     * Decodes the QR code first, then sends the photos to Gemini AI for shoe identification.
     */
    private void analyzeImage() {
        if (selectedBitmaps.isEmpty()) {
            Toast.makeText(this, "Please add a photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        String qrRawContent = decodeQRCodeFromAllImages();
        if (qrRawContent == null) {
            Toast.makeText(this, "QR code not found in any image", Toast.LENGTH_SHORT).show();
            return;
        }

        safeKey = Base64.encodeToString(qrRawContent.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("AI Analysis");
        pd.setMessage("Identifying shoe details...");
        pd.setCancelable(false);
        pd.show();

        GeminiManager.getInstance().sendTextWithPhotosPrompt(Prompts.SHOE_PROMPT, selectedBitmaps, new GeminiCallback() {
            @Override
            public void onSuccess(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        parseAndProcessResult(result, qrRawContent);
                    }
                });
            }

            @Override
            public void onFailure(final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        tvResult.setText("Error: " + t.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Parses the JSON response from the AI and displays the shoe details in the UI.
     * @param result The raw string response from the AI.
     * @param qrRawContent The previously decoded QR code content.
     */
    private void parseAndProcessResult(String result, String qrRawContent) {
        try {
            String cleanJson = result.replace("```json", "").replace("```", "").trim();
            Log.d(TAG, "AI raw result: " + cleanJson);

            shoeData = new JSONObject(cleanJson);
            shoeData.put("id", qrRawContent);

            StringBuilder sb = new StringBuilder("Shoe Details:\n\n");
            sb.append("Name: ").append(shoeData.optString("shoe_name")).append("\n");
            sb.append("Brand: ").append(shoeData.optString("manufacturing_company")).append("\n");
            sb.append("Color: ").append(shoeData.optString("color")).append("\n");
            sb.append("Shoe Type: ").append(shoeData.optString("type")).append("\n");
            sb.append("Price: $").append(shoeData.optDouble("price"));

            tvResult.setText(sb.toString());
        } catch (JSONException e) {
            tvResult.setText("Failed to parse AI response.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the click event for the 'Save' button.
     * Validates that analysis has been performed, then checks Firebase for existing records before saving.
     * @param view The clicked view.
     */
    public void save_shoe(View view) {
        if (shoeData == null || safeKey == null) {
            Toast.makeText(this, "Please analyze first", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            final String name = shoeData.getString("shoe_name");
            final String color = shoeData.getString("color");
            final String type = shoeData.getString("type");
            final double price = shoeData.getDouble("price");
            final String company = shoeData.getString("manufacturing_company");
            final String qrId = shoeData.getString("id");

            refBase2.child(safeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(add_shoe_ai.this, "Shoe already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadAndSave(qrId, name, color, type, price, company);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * Uploads the representative shoe image to Firebase Storage and saves the shoe metadata to the Realtime Database.
     * @param id Decoded QR ID.
     * @param name Shoe name.
     * @param color Shoe color.
     * @param type Shoe type.
     * @param price Shoe price.
     * @param company Manufacturing company.
     */
    private void uploadAndSave(final String id, final String name, final String color, final String type, final double price, final String company) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Saving...");
        pd.show();

        Bitmap bitmap = getShoeImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        refStorage.child("shoes").child(safeKey).child(fileName).putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot ts) {
                        count_shoes += 13;
                        refBase3.child("count_shoes").setValue(count_shoes);
                        final Shoes shoe = new Shoes(id, name, color, type, price, company);
                        refBase2.child(safeKey).setValue(shoe).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Toast.makeText(add_shoe_ai.this, "Saved!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });
    }

    /**
     * Closes the activity.
     * @param view The clicked view.
     */
    public void back(View view) { finish(); }
    
    /**
     * Finds a representative image of the shoe from the list of bitmaps (the one that DOESN'T contain a QR code, if possible).
     * @return A Bitmap image of the shoe.
     */
    private Bitmap getShoeImage() {
        for (Bitmap bitmap : selectedBitmaps) {
            String qr = decodeQRCode(bitmap);
            if (qr == null || qr.isEmpty()) {
                return bitmap;
            }
        }
        return selectedBitmaps.get(0);
    }
}
