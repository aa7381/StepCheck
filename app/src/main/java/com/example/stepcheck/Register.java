package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;
import static com.example.stepcheck.FBRef.refBase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

/**
 * An activity that provides a user registration interface.
 * It allows new users to sign up by providing their email, full name, password, and selecting a role.
 * The user's information is then stored in Firebase Authentication and Realtime Database.
 */
public class Register extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    EditText email;
    EditText fullName;
    EditText password;
    Spinner roleSpinner;

    String [] roles={"Worker","ShiftManager","charge_of_merchandise"};
    int role;

    /**
     * Called when the activity is first created. Initializes UI components and the role spinner.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        fullName = findViewById(R.id.fullName);
        password = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.roleSpinner);

        roleSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        roleSpinner.setAdapter(adp);

    }

    /**
     * Navigates the user back to the Login screen.
     *
     * @param view The view that was clicked.
     */
    public void go_Login(View view)
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Callback method to be invoked when an item in the role spinner has been selected.
     *
     * @param parent The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that is selected.
     */
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

    /**
     * Handles the register button click event. Validates user input, creates a new user
     * in Firebase Authentication, and saves the user's details to the Realtime Database.
     *
     * @param view The view that was clicked.
     */
    public void Register_click(View view) {
        String Email = email.getText().toString();
        String FullName = fullName.getText().toString();
        String Password = password.getText().toString();

        if (Email.isEmpty() || FullName.isEmpty() || Password.isEmpty() ) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Creating user...");
            pd.show();
            pd.setCancelable(false);


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = refAuth.getCurrentUser();

                        if(role == 0 )
                        {
                            Worker worker2 = new Worker(user.getUid(),fullName.getText().toString(),roles[role],false,false,false);
                            refBase.child(user.getUid()).setValue(worker2);
                        }
                        else if(role == 1)
                        {
                            Worker worker2 = new Worker(user.getUid(),fullName.getText().toString(),roles[role],false,false,true) ;
                            refBase.child(user.getUid()).setValue(worker2);
                        }
                        else
                        {
                            Worker worker2 = new Worker(user.getUid(),fullName.getText().toString(),roles[role],false,true,false) ;
                            refBase.child(user.getUid()).setValue(worker2);
                        }


                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Exception exp = task.getException();
                        if (exp instanceof FirebaseAuthInvalidUserException){
                            Toast.makeText(Register.this, "Invalid info", Toast.LENGTH_SHORT).show();
                        } else if (exp instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(Register.this, "Password too weak.", Toast.LENGTH_SHORT).show();
                        } else if (exp instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(Register.this, "User already exists.", Toast.LENGTH_SHORT).show();
                        } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(Register.this, "Invalid email.", Toast.LENGTH_SHORT).show();
                        } else if (exp instanceof FirebaseNetworkException) {
                            Toast.makeText(Register.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}