package com.example.stepcheck;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A utility class for holding static references to Firebase services.
 * This class provides easy access to Firebase Authentication and the Firebase Realtime Database.
 */
public class FBRef
{
    /**
     * Static reference to the Firebase Authentication instance.
     */
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    /**
     * Static reference to the Firebase Realtime Database instance.
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    /**
     * Static reference to the "Worker" node in the Firebase Realtime Database.
     */
    public static DatabaseReference refBase = FBDB.getReference("Worker");
    public static DatabaseReference refBase2 = FBDB.getReference("Shoe");



}
