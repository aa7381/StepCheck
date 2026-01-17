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
 * This activity hosts the main navigation of the app using a BottomNavigationView,
 * and displays different fragments based on user selection.
 * It also handles user permissions to show or hide certain navigation items.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class Qr_Code_main_Screen extends MasterClass implements BottomNavigationView.OnItemSelectedListener {

    private Button ScanQR;
    private String qr_code_data = "";
    private BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<ScanOptions> barLauncher;

    /**
     * Called when the activity is first created.
     * Initializes the UI components, sets up permissions, and handles initial fragment navigation.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_main_screen);

        ScanQR = findViewById(R.id.ScanQR);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);



        setupPermissions();

        handleFragmentNavigation(getIntent());
    }

    /**
     * This is called for activities that set launchMode to "singleTop" in their package,
     * or if a client used the {@link Intent#FLAG_ACTIVITY_SINGLE_TOP} flag when calling
     * startActivity().
     * @param intent The new intent that was started for the activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleFragmentNavigation(intent);
    }

    /**
     * Handles navigation to a specific fragment based on the intent extras.
     * This is used to deep-link into a specific part of the app.
     * @param intent The intent that may contain a "TARGET_FRAGMENT" extra.
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


    /**
     * Sets up the visibility of navigation items based on the current user's permissions.
     * It fetches the user's data from Firebase and adjusts the BottomNavigationView menu items accordingly.
     */
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

    /**
     * Loads a given fragment into the fragment container view.
     * @param fragment The fragment to load.
     * @return true if the fragment was successfully loaded, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            return true;
        }
        return false;
    }

    /**
     * Called when an item in the bottom navigation menu is selected.
     * @param item The selected item
     * @return true to display the item as the selected item and false if the item is not to be selected.
     */
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
