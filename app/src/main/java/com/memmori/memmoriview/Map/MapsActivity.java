package com.memmori.memmoriview.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.memmori.memmoriview.Location.LocationDialog;
import com.memmori.memmoriview.R;
import com.memmori.memmoriview.User.LoginActivity;
import com.memmori.memmoriview.User.User;
import com.memmori.memmoriview.User.UserAccountActivity;

import java.util.ArrayList;

import static com.memmori.memmoriview.Constants.STORAGE_REF;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionGranted = false;
    private int DEFAULT_ZOOM = 15;

    private FirebaseFirestore mdatabase;

    private ClusterManager mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers;

    private User mUser;

    private Button btnUser;
    private Button btnLogout;
    private Button btnRefresh;
    private Button btnFilterAll;
    private Button btnFilterApproved;
    private Button btnFilterUser;

    private String Filter = "All";

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mdatabase = FirebaseFirestore.getInstance();
        mClusterMarkers = new ArrayList<>();

        btnUser = findViewById(R.id.btnAdd);
        btnUser.setOnClickListener(this);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterAll.setOnClickListener(this);

        btnFilterApproved = findViewById(R.id.btnFilterApproved);
        btnFilterApproved.setOnClickListener(this);

        btnFilterUser = findViewById(R.id.btnFilterUser);
        btnFilterUser.setOnClickListener(this);

        initialUser();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                }
            }
        });
        AddMarkers();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void AddMarkers() {
        mdatabase.collection("Locations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (mMap != null) {
                        //resetMap();
                        if (mClusterManager == null) {
                            mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
                            handler.postDelayed(runnable, 10000);
                        }
                        if (mClusterManagerRenderer == null) {
                            mClusterManagerRenderer = new ClusterManagerRenderer(
                                    getApplicationContext(),
                                    mMap,
                                    mClusterManager
                            );
                            mClusterManager.setRenderer(mClusterManagerRenderer);
                        }

                        StorageReference store = STORAGE_REF.getReference();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {

                                StorageReference imgRef = store.child(document.get("picture").toString());
                                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext()).load(uri).into(mClusterManagerRenderer.imageview);
                                        GeoPoint latLng = (GeoPoint) document.get("location");
                                        com.memmori.memmoriview.Location.Location location = new com.memmori.memmoriview.Location.Location(document);
                                        ClusterMarker newClusterMarker = new ClusterMarker(
                                                new LatLng(latLng.getLatitude(), latLng.getLongitude()),
                                                document.get("building_name").toString(),
                                                "",
                                                document.get("picture").toString(),
                                                location
                                        );
                                        mClusterManager.addItem(newClusterMarker);
                                        mClusterMarkers.add(newClusterMarker);
                                        mClusterManager.cluster();
                                        //Toast.makeText(MapsActivity.this, document.get("Test").toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            } catch (NullPointerException e) {}
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = SphericalUtil.computeDistanceBetween(latLng, marker.getPosition());
                openDialog(mClusterManagerRenderer.getClusterItem(marker), distance);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:{
                Intent intent = new Intent(this, LocationPlacementActivity.class);
                //intent.putExtra("UserInfo", mUser);
                startActivity(intent);
                break;
            }
            case R.id.btnLogout:
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btnRefresh:
            {
                mClusterManager.clearItems();
                mClusterManager.cluster();
                FilterLocations(Filter);
                mClusterManager.cluster();
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnFilterAll:
            {
                ClearMap();
                FilterLocations("All");
                break;
            }
            case R.id.btnFilterApproved:
            {
                ClearMap();
                FilterLocations("Approved");
                break;
            }
            case R.id.btnFilterUser:
            {
                ClearMap();
                FilterLocations("User");
                break;
            }
        }
    }

    public void openDialog(ClusterMarker marker, double distance)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("LocationInfo", marker.getLocation());
        bundle.putDouble("Distance", distance);
        LocationDialog locationDialog = new LocationDialog();
        locationDialog.setArguments(bundle);
        locationDialog.show(getSupportFragmentManager(), "location dialog");
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mClusterManager.clearItems();
            mClusterManager.addItems(mClusterMarkers);
            mClusterManager.cluster();
            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private void initialUser() {
        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        if(String.valueOf(document.get("user_id")).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            mUser = new User(document);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void ClearMap()
    {
        mClusterManager.clearItems();
        mClusterManager.cluster();
    }

    private void FilterLocations(String Filter)
    {
        if(Filter.equals("All"))
        {
            mClusterManager.addItems(mClusterMarkers);
            mClusterManager.cluster();

        }
        else
        {
            for(ClusterMarker marker : mClusterMarkers)
            {
                if(marker.getLocation().getFilter().equals(Filter))
                {
                    mClusterManager.addItem(marker);
                }
            }
            mClusterManager.cluster();
        }
        this.Filter = Filter;
    }
}