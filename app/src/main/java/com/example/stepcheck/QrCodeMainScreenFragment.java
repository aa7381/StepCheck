package com.example.stepcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private void open_inform_shoe()
    {
        Intent intent = new Intent(requireActivity(), Shoe_information.class);
        intent.putExtra("qr_code_data", qr_code_data);
        startActivity(intent);
        requireActivity().finish();
    }

}
