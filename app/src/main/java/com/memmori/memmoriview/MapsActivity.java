package com.memmori.memmoriview;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements 
        OnMapReadyCallback, 
        View.OnClickListener, 
        GoogleMap.OnInfoWindowClickListener
        {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionGranted = false;
    private int DEFAULT_ZOOM = 15;

    private FirebaseFirestore mdatabase;

    private ClusterManager mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers;

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
            Toast.makeText(this, "Not Got Permission", Toast.LENGTH_SHORT).show();
            getLocationPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
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

    private void AddMarkers()
    {
        mdatabase.collection("Locations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if (mMap != null) {
                            //resetMap();
                            if (mClusterManager == null) {
                                mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
                            }
                            if (mClusterManagerRenderer == null) {
                                mClusterManagerRenderer = new ClusterManagerRenderer(
                                        getApplicationContext(),
                                        mMap,
                                        mClusterManager
                                );
                                mClusterManager.setRenderer(mClusterManagerRenderer);
                            }
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                                try{
                                    GeoPoint latLng = (GeoPoint) document.get("location");
                                    int drawable = Integer.valueOf((String) document.get("picture"));
                                    com.memmori.memmoriview.Location location = new com.memmori.memmoriview.Location(document);
                                    ClusterMarker newClusterMarker = new ClusterMarker(
                                            new LatLng(latLng.getLatitude(), latLng.getLongitude()),
                                            document.get("name").toString(),
                                            document.get("name").toString(),
                                            drawable,
                                            location
                                    );
                                    mClusterManager.addItem(newClusterMarker);
                                    mClusterMarkers.add(newClusterMarker);

                                }catch (NullPointerException e){
                                    //Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                                }
                        }
                        mClusterManager.cluster();
                    }
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        openDialog(findMarker(marker.getTitle()));
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }

    public void openDialog(ClusterMarker a)
    {
        Toast.makeText(this, a.getTitle(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putParcelable("LocationInfo", a.getLocation());
        LocationDialog locationDialog = new LocationDialog();
        locationDialog.setArguments(bundle);
        locationDialog.show(getSupportFragmentManager(), "location dialog");
    }

    private ClusterMarker findMarker(String name)
    {
        for(ClusterMarker marker : mClusterMarkers)
        {
            if(marker.getTitle().equals(name))
            {
                return marker;
            }
        }
        return null;
    }
}