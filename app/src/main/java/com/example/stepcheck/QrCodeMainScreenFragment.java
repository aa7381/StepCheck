package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * A simple {@link Fragment} subclass that displays the main screen for QR code scanning.
 * This fragment provides a button to start the QR code scanner and handles the result.
 */
public class QrCodeMainScreenFragment extends Fragment {

    private Button btn_start_scanning;
    private String qr_code_data = "";

    private ActivityResultLauncher<ScanOptions> barLauncher;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is the place to inflate the layout for the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code_main_screen, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any saved state has been restored in to the view.
     * This is where you should initialize your UI and set up event listeners.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_start_scanning= view.findViewById(R.id.btn_start_scanning);


        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result != null && result.getContents() != null) {
                qr_code_data = result.getContents();
                open_inform_shoe();

            }
        });

        if (btn_start_scanning != null) {
            btn_start_scanning.setOnClickListener(v -> {
                scanCode();
            });
        }
    }


    /**
     * Initiates the QR code scanning process using the zxing-android-embedded library.
     * Configures and launches the barcode scanner.
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * After a QR code is successfully scanned, this method checks if the shoe exists in the database.
     * If the shoe exists, it opens the {@link Shoe_information} activity to display its details.
     * Otherwise, it shows a toast message indicating that the shoe was not found.
     */
    private void open_inform_shoe() {
        if (qr_code_data != null && !qr_code_data.isEmpty()) {

            String safeKey = Base64.encodeToString(qr_code_data.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

            refBase2.child(safeKey).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Intent intent = new Intent(requireActivity(), Shoe_information.class);
                        intent.putExtra("qr_code_data", safeKey);
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "נעל לא קיימת במערכת", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "שגיאה בגישה לשרת", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(requireContext(), "לא נמצא QR Code תקין", Toast.LENGTH_SHORT).show();
        }
    }


}
