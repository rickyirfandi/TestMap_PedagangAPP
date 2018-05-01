package com.ardasatata.testmap_pedagangapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = "";
    private static final float DEFAULT_ZOOM =  14.0f;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    private GoogleMap mMap;

    MapFragment mapFragment;

    Button show;
    Button hide;

    Button findLocation;

    Pedagang test1;

    private FirebaseAuth firebaseAuth;

    boolean mLocationPermissionGranted;

    FusedLocationProviderClient mFusedLocationProviderClient;

    Location mLastKnownLocation;
    Location mDefaultLocation;

    LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    GoogleApiClient mGoogleApiClient;

    MarkerOptions yourMarkerOptions;
    Marker yourMarker;

    DatabaseReference pedagangDatabase;
    String pedagangId;

    LatLng currentPos;

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        pedagangOnline();

    }

    @Override
    protected void onPause() {
        super.onPause();

        pedagangOffline();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentPos = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            // user is already logged in
            // open profile activity
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            //close this activity
            finish();

        }

        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDefaultLocation = new Location("");
        mDefaultLocation.setLatitude(38.609556);
        mDefaultLocation.setLongitude(1.139637);
        mLastKnownLocation = mDefaultLocation;

        show = findViewById(R.id.show);
        hide = findViewById(R.id.hide);
        Button buttonLogout = (Button) findViewById(R.id.logoutMaps);
        findLocation = findViewById(R.id.findLocation);


        pedagangDatabase = FirebaseDatabase.getInstance().getReference("pedagang");

        FirebaseUser FireUser = FirebaseAuth.getInstance().getCurrentUser();

        pedagangId = FireUser.getUid();


        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        test1 = new Pedagang(new LatLng(38.609556, -1.139637),true,"Tahu Campur Pak Sukir","hehe");

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getView().setVisibility(View.VISIBLE);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getView().setVisibility(View.INVISIBLE);
            }
        });

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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(38.609556, -1.139637))
//                .anchor(0.5f, 0.5f)
//                .title("Title1")
//                .snippet("Snippet1")
//                );

        mMap.addMarker(new MarkerOptions()
                .position(test1.getLatlng())
                .anchor(0.5f, 0.5f)
                .title(test1.getNamaDagang())
                .snippet("Snippet1")
                );

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Context context = getApplicationContext();
                CharSequence text = test1.getInfo();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return false;
            }
        });

    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            currentPos = new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentPos, DEFAULT_ZOOM));
                            // Add a marker in Sydney and move the camera

                            yourMarkerOptions = new MarkerOptions();
                            yourMarkerOptions.title("Title");
                            yourMarkerOptions.snippet("");
                            yourMarkerOptions.position(currentPos);
                            //Set your marker icon using this method.
                            //yourMarkerOptions.icon();

                            yourMarker = mMap.addMarker(yourMarkerOptions);

                            pedagangDatabase.child(pedagangId).child("latlng").setValue(currentPos);

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDefaultLocation.getLatitude(),mDefaultLocation.getLongitude()), DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });


            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }


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

    private void pedagangOnline(){
        pedagangDatabase.child(pedagangId).child("status").setValue(true);
    }

    private void pedagangOffline(){
        pedagangDatabase.child(pedagangId).child("status").setValue(false);
    }

}
