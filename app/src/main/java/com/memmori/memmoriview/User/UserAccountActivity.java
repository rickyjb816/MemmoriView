package com.memmori.memmoriview.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.memmori.memmoriview.Map.MapsActivity;
import com.memmori.memmoriview.R;

import static com.memmori.memmoriview.Constants.STORAGE_REF;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private User mUser;

    private ImageButton[] Buttons = new ImageButton[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Button btnBack = findViewById(R.id.btnLocationBack);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        mUser = getIntent().getParcelableExtra("UserInfo");
        Buttons[0] = findViewById(R.id.imageButton);
        Buttons[1] = findViewById(R.id.imageButton2);
        Buttons[2] = findViewById(R.id.imageButton3);
        //populateUserAccount();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnLocationBack: {
                startActivity(new Intent(this, MapsActivity.class));
                break;
            }
            case R.id.btnLogout: {
                logOut();
                break;
            }
        }
    }

    private void logOut()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void populateUserAccount() {
        int i = 0;
        for(ImageButton ib : Buttons)
        {
            if(mUser.getmUserLocations().get(i).isEmpty())
            {
                break;
            }
            else
            {
                StorageReference store = STORAGE_REF.getReference();
                DocumentReference ref = FirebaseFirestore.getInstance().collection("Locations").document(mUser.getmUserLocations().get(i));
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        StorageReference imgRef = store.child(documentSnapshot.get("picture").toString());
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext()).load(uri).into(ib);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ib.setImageResource(R.drawable.ic_add_black_24dp);
                            }
                        });
                    }
                });
                i++;
            }
        }
    }
}
