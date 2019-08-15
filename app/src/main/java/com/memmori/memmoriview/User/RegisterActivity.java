package com.memmori.memmoriview.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.memmori.memmoriview.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etxtUsername;
    private EditText etxtPassword;
    private EditText etxtEmail;
    private Spinner spUserType;

    private Button btnCreateAcctount;

    ArrayList<Map<String, Object>> UserLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPassword = findViewById(R.id.etxtPassword);
        etxtUsername = findViewById(R.id.etxtUsername);
        spUserType = findViewById(R.id.spUserType);
        btnCreateAcctount = findViewById(R.id.btnCreateUser);

        btnCreateAcctount.setOnClickListener(this);

        initiseLocations();
    }



    private void createNewAccount()
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(etxtEmail.getText().toString(), etxtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                Map<String, Object> docData = new HashMap<>();
                docData.put("username", etxtUsername.getText().toString());
                docData.put("email", etxtEmail.getText().toString());
                docData.put("user_id", FirebaseAuth.getInstance().getUid());
                docData.put("user_type", spUserType.getSelectedItem().toString());


                docData.put("locations", UserLocations);
                
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
            }
        });
        
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnCreateUser:
            {
                createNewAccount();
                break;
            }
        }
    }

    private void initiseLocations()
    {
        FirebaseFirestore.getInstance().collection("Locations").document("BeD3feSwSoTJ00yQehXt").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> Location = new HashMap<>();
                Location.put("building_name", documentSnapshot.get("date_added"));
                Location.put("date_added", documentSnapshot.get("building_name"));
                Location.put("date_taken", documentSnapshot.get("date_taken"));
                Location.put("index", documentSnapshot.get("index"));
                Location.put("location", documentSnapshot.get("location"));
                Location.put("name", documentSnapshot.get("name"));
                Location.put("picture", documentSnapshot.get("picture"));
                Location.put("street_name", documentSnapshot.get("street_name"));
                UserLocations.add(Location);
            }
        });
    }
}
