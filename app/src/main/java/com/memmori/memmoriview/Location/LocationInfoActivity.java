package com.memmori.memmoriview.Location;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.memmori.memmoriview.Map.MapsActivity;
import com.memmori.memmoriview.R;

public class LocationInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Location location;

    private TextView txtStreetName;
    private TextView txtBuildingName;
    private TextView txtDateTaken;
    private TextView txtDateAdded;
    private TextView txtPhotographer;
    private TextView txtOwner;
    private TextView txtDescription;

    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        location = getIntent().getParcelableExtra("LocationInfo");

        setUpControls();
        applyInformation();

        Toast.makeText(this, location.getDateTakenString(), Toast.LENGTH_SHORT).show();
    }

    private void setUpControls()
    {
        txtStreetName = findViewById(R.id.txtStreetName);
        txtBuildingName = findViewById(R.id.txtBuildingName);
        txtDateTaken = findViewById(R.id.txtDateTaken);
        txtDateAdded = findViewById(R.id.txtDateAdded);
        txtPhotographer = findViewById(R.id.txtPhotographer);
        txtOwner = findViewById(R.id.txtOwner);
        txtDescription = findViewById(R.id.txtDescription);
        btnBack = findViewById(R.id.btnARBack);
        btnBack.setOnClickListener(this);
    }

    private void applyInformation()
    {
        txtStreetName.setText(location.getStreetName());
        txtBuildingName.setText(location.getBuildingName());
        txtDateTaken.setText(location.getDateTakenString());
        txtDateAdded.setText(location.getDateAddedString());
        txtPhotographer.setText(location.getPhotographer());
        txtOwner.setText(location.getOwner());
        txtDescription.setText(location.getDescription());
        btnBack = findViewById(R.id.btnARBack);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
