package com.memmori.memmoriview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.memmori.memmoriview.Map.MapsActivity;
import com.memmori.memmoriview.User.LoginActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnLoginRegister = findViewById(R.id.btnLoginRegistor);
        Button btnViewer = findViewById(R.id.btnViewer);

        btnLoginRegister.setOnClickListener(this);
        btnViewer.setOnClickListener(this);

        setupFirebaseAuth();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnLoginRegistor:
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnViewer:
            {
                guestSignIn();
                break;
            }
        }
    }

    private void guestSignIn()
    {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInAnonymously:success");
                    //FirebaseUser user = FirebaseAuth.getCurrentUser();
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInAnonymously:failure", task.getException());
                    //Toast.makeText(this, "Authentication failed.",
                    //Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setupFirebaseAuth()
    {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(), "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }
}
