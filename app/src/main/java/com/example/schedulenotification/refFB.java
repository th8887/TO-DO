package com.example.schedulenotification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * shortcuts for Firebase
 */

public class refFB{
    public static FirebaseAuth reAuth= FirebaseAuth.getInstance();

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference refDB = database.getReference("Users");


    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference refStorage =storage.getReference();
}
