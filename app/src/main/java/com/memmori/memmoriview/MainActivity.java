package com.memmori.memmoriview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.memmori.memmoriview.Controls.JoyStick;
import com.memmori.memmoriview.User.LoginActivity;


public class MainActivity extends AppCompatActivity implements JoyStick.JoystickListener {


    JoyStick jsTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        jsTest = new JoyStick(this, R.drawable.ic_rotationtop);
        jsTest.VectorDrawable = R.drawable.ic_rotationtop;
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int source) {
        jsTest.VectorDrawable = R.drawable.ic_rotationtop;
    }
}
