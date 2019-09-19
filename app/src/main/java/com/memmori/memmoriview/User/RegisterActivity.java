package com.memmori.memmoriview.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.memmori.memmoriview.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etxtUsername;
    private EditText etxtPassword;
    private EditText etxtEmail;
    private Spinner spUserType;

    Map<String, String> UserLocations = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPassword = findViewById(R.id.etxtPassword);
        etxtUsername = findViewById(R.id.etxtUsername);
        spUserType = findViewById(R.id.spUserType);
        Button btnCreateAcctount = findViewById(R.id.btnCreateUser);

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
        UserLocations.put("location1", "BeD3feSwSoTJ00yQehXt");
        UserLocations.put("location2", "GTZkgeGgQQfSdRnqci7g");
        UserLocations.put("location3", "HcAjFreK3qqVzA3pBuiq");
    }
}
