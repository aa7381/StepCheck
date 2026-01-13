package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;
import static com.example.stepcheck.FBRef.refBase2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Activity for adding a new shoe to the inventory.
 */
public class add_new_shoe extends MasterClass implements AdapterView.OnItemSelectedListener {

    private Button ScanQR, UploadImage;
    private String qr_code_data = "";

    private EditText etShoeName, etShoeType, etPrice, etmanufacturing_company, etColor;
    private Spinner spinnerSizeType, spinnerQuantitySize,spinnerSizeGender;


    private int selectedSizeTypePosition = 0;

    private String[] sizeType = {"uk", "eu", "us"};

    private String[] us_sizes = { "usMen", "usWomen", "usKidsY","usYoungerKids", "usBabies"};
    private String[] uk_sizes = {"ukSizes","ukKidsY","ukYoungerKids","ukBabies"};

    private String[] eu_sizes = {"euSizes","euKidsY","euYoungerKids","euBabies"};


    String[] euSizes = {
            "35.5", "36", "36.5", "37.5", "38", "38.5", "39", "40", "40.5", "41",
            "42", "42.5", "43", "44", "44.5", "45", "45.5", "46", "47", "47.5",
            "48", "48.5", "49", "49.5", "50", "50.5", "51", "51.5", "52",
            "52.5", "53", "53.5", "54", "54.5", "55", "55.5", "56", "56.5"
    };

    String[] ukSizes = {
            "3", "3.5", "4", "4.5", "5", "5.5", "6", "6", "6.5", "7",
            "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12",
            "12.5", "13", "13.5", "14", "14.5", "15", "15.5", "16", "16.5",
            "17", "17.5", "18", "18.5", "19", "19.5", "20", "20.5", "21"
    };

    String[] usMenSizes = {
            "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8",
            "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12", "12.5",
            "13", "13.5", "14", "14.5", "15", "15.5", "16", "16.5", "17",
            "17.5", "18", "18.5", "19", "19.5", "20", "20.5", "21", "21.5", "22"
    };

    String[] usWomenSizes = {
            "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5",
            "10", "10.5", "11", "11.5", "12", "12.5", "13", "13.5", "14",
            "14.5", "15", "15.5", "16", "16.5", "17", "17.5", "18", "18.5",
            "19", "19.5", "20", "20.5", "21", "21.5", "22", "22.5", "23", "23.5"
    };

    //🧒 Youth / Big Kids (US Y)

    String[] usKidsY = {
            "1Y","1.5Y","2Y","2.5Y","3Y","3.5Y","4Y","4.5Y","5Y","5.5Y","6Y","6.5Y","7Y"
    };

    String[] ukKidsY = {
            "13.5","1","1.5","2","2.5","3","3.5","4","4.5","5","5.5","6","6"
    };

    String[] euKidsY = {
            "32","33","33.5","34","35","35.5","36","36.5","37.5","38","38.5","39","40"
    };

    //----------------------------------------------------------------------------------------------------------

    //👦 Younger Kids (C + Y)

    String[] usYoungerKids = {
            "8C","8.5C","9C","9.5C","10C","10.5C","11C","11.5C","12C","12.5C","13C","13.5C",
            "1Y","1.5Y","2Y","2.5Y","3Y"
    };

    String[] ukYoungerKids = {
            "7.5","8","8.5","9","9.5","10","10.5","11","11.5","12","12.5","13",
            "13.5","1","1.5","2","2.5"
    };

    String[] euYoungerKids = {
            "25","25.5","26","26.5","27","27.5","28","28.5","29.5","30","31","31.5",
            "32","33","33.5","34","35"
    };

    //--------------------------------------------------------------------

    //👶 Babies & Toddlers (C)

    String[] usBabies = {
            "1C","1.5C","2C","2.5C","3C","3.5C","4C","4.5C","5C","5.5C",
            "6C","6.5C","7C","7.5C","8C","8.5C","9C","9.5C","10C"
    };

    String[] ukBabies = {
            "0.5","1","1.5","2","2.5","3","3.5","4","4.5","5",
            "5.5","6","6.5","7","7.5","8","8.5","9","9.5"
    };

    String[] euBabies = {
            "16","16.5","17","18","18.5","19","19.5","20","21","21.5",
            "22","22.5","23.5","24","25","25.5","26","26.5","27"
    };


    private int selectedSizePosition = 0;

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            qr_code_data = result.getContents();
            Toast.makeText(this, "Scanned: " + qr_code_data, Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shoe);

        ScanQR = findViewById(R.id.ScanQR);
        UploadImage = findViewById(R.id.UploadImage);
        etShoeName = findViewById(R.id.etShoeName);
        etShoeType = findViewById(R.id.etShoeType);
        etPrice = findViewById(R.id.etPrice);
        etColor = findViewById(R.id.etColor);
        etmanufacturing_company = findViewById(R.id.etmanufacturing_company);
        spinnerSizeType = findViewById(R.id.spinnerSizeType);
        spinnerQuantitySize = findViewById(R.id.spinnerQuantitySize);
        spinnerSizeGender = findViewById(R.id.spinnerSizeGender);


        FirebaseUser currentUser = refAuth.getCurrentUser();

        ScanQR.setOnClickListener(view -> scanCode());

        //spinnerQuantitySize.setOnItemSelectedListener(this);
        spinnerSizeType.setOnItemSelectedListener(this);
        spinnerSizeGender.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sizeType);
        spinnerSizeType.setAdapter(adp);


        spinnerSizeGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selectedSizeTypePosition == 0)
                {
                    switch (position){
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, ukSizes);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, ukKidsY);
                            spinnerSizeGender.setAdapter(adp2);
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, ukYoungerKids);
                            spinnerSizeGender.setAdapter(adp3);
                            break;
                        case 3:
                            ArrayAdapter<String> adp4 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, ukBabies);
                            spinnerQuantitySize.setAdapter(adp4);
                            break;
                        default:
                            break;
                    }
                }
                else if(selectedSizeTypePosition == 1) {
                    switch (position) {
                        case 0:
                            ArrayAdapter<String> adp = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, euSizes);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, euKidsY);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, euYoungerKids);
                            spinnerQuantitySize.setAdapter(adp3);
                            break;
                        case 3:
                            ArrayAdapter<String> adp4 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, euBabies);
                            spinnerQuantitySize.setAdapter(adp4);
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
                            ArrayAdapter<String> adp = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, usMenSizes);
                            spinnerQuantitySize.setAdapter(adp);
                            break;
                        case 1:
                            ArrayAdapter<String> adp2 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, usWomenSizes);
                            spinnerQuantitySize.setAdapter(adp2);
                            break;
                        case 2:
                            ArrayAdapter<String> adp3 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, usKidsY);
                            spinnerQuantitySize.setAdapter(adp3);
                        case 3:
                            ArrayAdapter<String> adp4 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, usYoungerKids);
                            spinnerQuantitySize.setAdapter(adp4);
                            break;
                        case 4:
                            ArrayAdapter<String> adp5 = new ArrayAdapter<>(add_new_shoe.this, android.R.layout.simple_spinner_dropdown_item, usBabies);
                            spinnerQuantitySize.setAdapter(adp5);
                            break;
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

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                selectedSizeTypePosition = position;
                ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, uk_sizes);
                spinnerSizeGender.setAdapter(adp);
                break;
            case 1:
                selectedSizeTypePosition = position;
                ArrayAdapter<String> adp1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eu_sizes);
                spinnerSizeGender.setAdapter(adp1);
                break;
            case 2:
                selectedSizeTypePosition = position;
                ArrayAdapter<String> adp2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,us_sizes);
                spinnerSizeGender.setAdapter(adp2);
                break;
            default:
                break;
        }
    }







    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void upload_image(View view) {
    }

    public void Save_shoe(View view) {
        String shoeName = etShoeName.getText().toString();
        String shoeType = etShoeType.getText().toString();
        String color = etColor.getText().toString();
        String manufacturing_company = etmanufacturing_company.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (shoeName.isEmpty() || shoeType.isEmpty() || priceStr.isEmpty() || qr_code_data.isEmpty() || manufacturing_company.isEmpty() || color.isEmpty()) {
            Toast.makeText(this, "Information is missing", Toast.LENGTH_SHORT).show();
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                Shoes shoes = new Shoes(qr_code_data, shoeName, color, shoeType, price, manufacturing_company);
                refBase2.child(qr_code_data).setValue(shoes);
                Toast.makeText(this, "Shoe saved successfully", Toast.LENGTH_SHORT).show();

                // After saving, go back to inventory fragment
                back(view);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Returns to the main screen and specifically to the inventory fragment.
     */
    public void back(View view) {
        Intent intent = new Intent(this, Qr_Code_main_Screen.class);
        intent.putExtra("TARGET_FRAGMENT", "INVENTORY");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
