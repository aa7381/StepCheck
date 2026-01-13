package com.example.stepcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * The main activity of the application after the user logs in.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class Qr_Code_main_Screen extends MasterClass implements BottomNavigationView.OnItemSelectedListener {

    private Button ScanQR;
    private String qr_code_data = "";
    private BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_main_screen);

        ScanQR = findViewById(R.id.ScanQR);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result != null && result.getContents() != null) {
                qr_code_data = result.getContents();
                Toast.makeText(this, "QR: " + qr_code_data, Toast.LENGTH_SHORT).show();
            }
        });

        if (ScanQR != null) {
            ScanQR.setOnClickListener(view -> scanCode());
        }

        setupPermissions();

        handleFragmentNavigation(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleFragmentNavigation(intent);
    }

    /**
     * Checks if a target fragment was requested via Intent extras.
     */
    private void handleFragmentNavigation(Intent intent) {
        if (intent != null && intent.hasExtra("TARGET_FRAGMENT")) {
            String target = intent.getStringExtra("TARGET_FRAGMENT");
            if ("INVENTORY".equals(target)) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_inventory);
            }
        } else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            // Default fragment
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void setupPermissions() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FBRef.refBase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Worker worker = snapshot.getValue(Worker.class);
                    if (worker != null) {
                        Menu navMenu = bottomNavigationView.getMenu();
                        if (worker.getCanEditInventory()) {
                            navMenu.findItem(R.id.navigation_inventory).setVisible(true);
                        }
                        if (worker.getCan_manage_shift()) {
                            navMenu.findItem(R.id.navigation_manage_shift).setVisible(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            fragment = new QrCodeMainScreenFragment();
        } else if (itemId == R.id.navigation_inventory) {
            fragment = new InventoryFragment();
        } else if (itemId == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        }
        // Add other fragments as needed
        return loadFragment(fragment);
    }
}
