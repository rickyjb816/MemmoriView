package com.memmori.memmoriview.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.memmori.memmoriview.Map.MapsActivity;
import com.memmori.memmoriview.R;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        mUser = getIntent().getParcelableExtra("UserInfo");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnBack: {
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
}
