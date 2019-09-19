package com.memmori.memmoriview.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.memmori.memmoriview.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.memmori.memmoriview.Constants.FIREBASE_STORAGE;

public class LocationPlacementActivity extends AppCompatActivity implements View.OnClickListener {


    EditText etxtBuildingName;
    EditText etxtStreetName;
    EditText etxtDateTaken;
    EditText etxtOwner;
    EditText etxtPhotographer;
    EditText etxtDescription;
    EditText etxtLongatude;
    EditText etxtLatitude;

    Button btnPickDate;
    Button btnGetCurrentLocation;
    Button btnAddLocation;
    Button btnBack;
    Button btnUploadImage;

    ImageView imgImagePrview;

    Uri imageUri;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private Calendar date = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_placement);

        initiseControls();
        getCurrentLocation();
    }

    private void initiseControls() {
        etxtBuildingName = findViewById(R.id.etxtBuildingName);
        etxtStreetName = findViewById(R.id.etxtStreetName);
        etxtDateTaken = findViewById(R.id.etxtDateTaken);
        etxtOwner = findViewById(R.id.etxtOwner);
        etxtPhotographer = findViewById(R.id.etxtPhotographer);
        etxtDescription = findViewById(R.id.etxtDescription);
        etxtLongatude = findViewById(R.id.etxtLongatude);
        etxtLatitude = findViewById(R.id.etxtLatitude);

        btnPickDate = findViewById(R.id.btnPickDate);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnBack = findViewById(R.id.btnLocationBack);

        imgImagePrview = findViewById(R.id.imgPreview);

        btnPickDate.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);
        btnGetCurrentLocation.setOnClickListener(this);
        btnAddLocation.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPickDate:
            {
                pickDate();
                break;
            }
            case R.id.btnUploadImage: {
                //chooseImage();
                cropImage();
                break;
            }
            case R.id.btnGetCurrentLocation: {
                getCurrentLocation();
                break;
            }
            case R.id.btnAddLocation: {
                if (checkFields()) {
                    addLocation();
                    returnToMaps();
                }
                break;
            }

            case R.id.btnLocationBack: {
                returnToMaps();
                break;
            }
        }
    }

    private boolean checkFields() {
        boolean check = true;

        ArrayList<EditText> fields = new ArrayList<>();
        fields.add(etxtBuildingName);
        fields.add(etxtStreetName);
        fields.add(etxtDateTaken);
        fields.add(etxtOwner);
        fields.add(etxtPhotographer);
        fields.add(etxtDescription);
        fields.add(etxtLongatude);
        fields.add(etxtLatitude);

        for (EditText et : fields) {
            if (et.getText().toString().isEmpty()) {
                check = false;
                et.getBackground().setColorFilter(getResources().getColor(R.color.errorColor), PorterDuff.Mode.SRC_ATOP);
            }
        }
        if (imageUri.toString().isEmpty()) {
            check = false;
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.errorColor));
        }
        return check;
    }

    private void addLocation() {

        String imageName = UUID.randomUUID().toString();

        Map<String, Object> docData = new HashMap<>();
        docData.put("building_name", etxtBuildingName.getText().toString());
        docData.put("street_name", etxtStreetName.getText().toString());
        docData.put("date_taken", new Timestamp(date.getTime()));
        docData.put("date_added", new Timestamp(Calendar.getInstance().getTime()));
        docData.put("owner", etxtOwner.getText().toString());
        docData.put("photographer", etxtPhotographer.getText().toString());
        docData.put("description", etxtDescription.getText().toString());
        docData.put("picture", imageName);
        docData.put("location", new GeoPoint(Double.parseDouble(etxtLatitude.getText().toString()), Double.parseDouble(etxtLongatude.getText().toString())));
        docData.put("filter", "User");

        uploadImage(imageName);

        FirebaseFirestore.getInstance().collection("Locations").document().set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    private void returnToMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgImagePrview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imgImagePrview.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(String imageName) {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance(FIREBASE_STORAGE);
            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child(imageName);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            etxtLatitude.setText(String.valueOf(location.getLatitude()));
                            etxtLongatude.setText(String.valueOf(location.getLongitude()));
                        }
                    }
                });
    }

    private void pickDate() {
        DatePickerDialog dpd;
        Calendar c;

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        dpd = new DatePickerDialog(LocationPlacementActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                etxtDateTaken.setText(mDay + "/" + mMonth + "/" + mYear);
                date.set(mYear, mMonth, mDay);
            }
        }, year, month, day);
        dpd.show();
    }

    private void cropImage()
    {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMaxCropResultSize(1000,1000)
                .start(this);
    }
}


