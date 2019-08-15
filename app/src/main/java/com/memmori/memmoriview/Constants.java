package com.memmori.memmoriview;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Constants {

    public static final String FIREBASE_STORAGE = "gs://memmori-view";
    public static final FirebaseStorage STORAGE_REF = FirebaseStorage.getInstance(FIREBASE_STORAGE);
    public static final StorageReference STORE_REF = STORAGE_REF.getReference();
}
