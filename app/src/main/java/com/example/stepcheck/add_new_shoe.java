package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;
import static com.example.stepcheck.FBRef.refBase;
import static com.example.stepcheck.FBRef.refBase2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class add_new_shoe extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button ScanQR, UploadImage, btnSave, btnSaveShoe, btnBack;

    private String qr_code_data = "";


    EditText etShoeName, etShoeType, etPrice, etQuantity ,etmanufacturing_company, etColor;
    Spinner spinnerSizeType, spinnerQuantitySize;

    String[] sizeType = {"uk", "eu", "us"};



    int role = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shoe);

        ScanQR = findViewById(R.id.ScanQR);
        UploadImage = findViewById(R.id.UploadImage);
        etShoeName = findViewById(R.id.etShoeName);
        etShoeType = findViewById(R.id.etShoeType);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etColor = findViewById(R.id.etColor);
        etmanufacturing_company = findViewById(R.id.etmanufacturing_company);
        spinnerSizeType = findViewById(R.id.spinnerSizeType);



        ScanQR.setOnClickListener(view ->
        {
            scanCode();
        });

        spinnerSizeType.setOnItemSelectedListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sizeType);
        spinnerSizeType.setAdapter(adp);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        role = position;
    }

    /**
     * Callback method to be invoked when the selection disappears from the role spinner.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volunm up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result2 ->
    {
        if (result2.getContents() != null) {
            qr_code_data = result2.getContents();
        }
    });

    public void upload_image(View view) {

    }

    public void Save_shoe(View view) {
        String shoeName = etShoeName.getText().toString();
        String shoeType = etShoeType.getText().toString();
        String color = etColor.getText().toString();
        String manufacturing_company = etmanufacturing_company.getText().toString();
        String price = etPrice.getText().toString();
        if (shoeName.isEmpty() && shoeType.isEmpty() && price.isEmpty() || qr_code_data == null || manufacturing_company.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Information are missing", Toast.LENGTH_SHORT).show();
        }
        else
        {
            FirebaseUser user = refAuth.getCurrentUser();
            Shoes shoes = new Shoes(qr_code_data, etShoeName.getText().toString(),etColor.getText().toString(), etShoeType.getText().toString(), Double.parseDouble(etPrice.getText().toString()), etmanufacturing_company.getText().toString());
            refBase2.child(qr_code_data).setValue(shoes);

        }
    }

    public void back(View view) {
        finish();
    }
}


