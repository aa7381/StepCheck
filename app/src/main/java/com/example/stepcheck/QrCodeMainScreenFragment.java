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

public class QrCodeMainScreenFragment extends Fragment {

    private Button btn_start_scanning;
    private String qr_code_data = "";

    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code_main_screen, container, false);
    }

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


    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

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
