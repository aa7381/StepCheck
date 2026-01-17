package com.example.stepcheck;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A utility class for holding static references to Firebase services.
 * This class provides easy access to Firebase Authentication, Firebase Realtime Database, and Firebase Storage.
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
    /**
     * Static reference to the "Shoe" node in the Firebase Realtime Database.
     */
    public static DatabaseReference refBase2 = FBDB.getReference("Shoe");

    /**
     * Static reference to the "stock" node in the Firebase Realtime Database.
     */
    public static DatabaseReference refBase4 = FBDB.getReference("stock");


    /**
     * Static reference to the "Shoe_count" node in the Firebase Realtime Database.
     */
    public static DatabaseReference refBase3 = FBDB.getReference("Shoe_count");


    /**
     * Static reference to the Firebase Storage instance.
     */
    public static FirebaseStorage Storage = FirebaseStorage.getInstance();
    /**
     * Static reference to the root of the Firebase Storage.
     */
    public static StorageReference refStorage = Storage.getReference();




}
