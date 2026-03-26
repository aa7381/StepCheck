package com.example.stepcheck.activities;

import static com.example.stepcheck.utils.FBRef.refBase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stepcheck.utils.FBRef;
import com.example.stepcheck.R;
import com.example.stepcheck.models.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that allows managers to see which employees are currently in a shift.
 * It displays a list of active workers and allows viewing detailed information for each worker.
 * Includes logic to automatically clear 'inShift' status for workers whose shift date is not today.
 */
public class employee_management_acticity extends Fragment implements AdapterView.OnItemLongClickListener {

    private ListView lvEmployees;
    private Button btnRefresh;
    private ArrayAdapter<String> adapter;
    private List<String> employeeNames;
    private List<String> employeeIds;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_management_acticity, container, false);
        lvEmployees = view.findViewById(R.id.lvEmployees);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        lvEmployees.setOnItemLongClickListener(this);
        employeeNames = new ArrayList<>();
        employeeIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, employeeNames);
        lvEmployees.setAdapter(adapter);


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEmployeesInShift();
            }
        });
        loadEmployeesInShift();
        return view;
    }

    /**
     * Fetches the list of employees who are currently marked as being 'in shift' from Firebase.
     * It also checks the date of their last presence. If the presence date is not today,
     * it automatically sets their 'inShift' status to false in the database.
     * Updates the UI list with the retrieved names of workers currently in shift today.
     */
    private void loadEmployeesInShift() {
        employeeNames.clear();
        employeeIds.clear();
        adapter.notifyDataSetChanged();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final String todayDate = sdf.format(calendar.getTime());
        
        refBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot workerSnapshot : snapshot.getChildren()) {
                    final Worker worker = workerSnapshot.getValue(Worker.class);
                    final String workerId = workerSnapshot.getKey();
                    
                    if (worker != null && Boolean.TRUE.equals(worker.getInShift())) {
                        FBRef.refBase5.child(workerId).child(todayDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot presenceSnapshot) {
                                if (presenceSnapshot.exists()) {
                                    if (worker.getUsername() != null) {
                                        employeeNames.add(worker.getUsername());
                                        employeeIds.add(workerId);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {

                                    refBase.child(workerId).child("inShift").setValue(false);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * Opens the detailed information screen for a specific worker.
     * @param workerId The unique ID of the worker to display information for.
     */
    private void open_inform_worker(final String workerId) {
        if (workerId == null) return;
        refBase.child(workerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Intent intent = new Intent(requireActivity(), Worker_information.class);
                        intent.putExtra("USER_ID", workerId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "עובד לא קיים במערכת", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "שגיאה בגישה לשרת", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * Callback method to be invoked when an item in this AdapterView has been clicked and held.
     * @param adapterView The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked.
     * @param pos The position of the view in the adapter.
     * @param rowid The row id of the item that was clicked.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowid) {
        if (pos >= 0 && pos < employeeIds.size()) {
            String selectedWorkerId = employeeIds.get(pos);
            open_inform_worker(selectedWorkerId);
        }
        return true;
    }
}
