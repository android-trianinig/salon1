package com.training.apps.makeup.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.training.apps.makeup.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "SelectLocationActivity";
    private GoogleMap mMap;
    private String userAddress;
    private Geocoder geocoder;
    private Marker userMarker;
    private GoogleMap.OnMarkerDragListener onMarkerDragListener;
    @BindView(R.id.location_text)
    TextView locationTextView;
    @BindView(R.id.confirm_location)
    Button confirmLocation;
    private SupportMapFragment mapFragment;
    private View mMyLocationButtonView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng userLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        ButterKnife.bind(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationTextView.setSelected(true);
    }


    private void setUserSelectedLocation(LatLng latLng) {
        userLatLong = latLng;
        geocoder = new Geocoder(SelectLocationActivity.this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                userAddress = addressList.get(0).getAddressLine(0).trim();
            }
        } catch (IOException e) {
            Toast.makeText(SelectLocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                getCurrentUserLocation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(SelectLocationActivity.this, "Permission needed to get your current location", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
        mMyLocationButtonView = mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton");
        mMyLocationButtonView.setBackgroundColor(Color.parseColor("#D81B60"));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (userMarker != null) {
                    userMarker.remove();
                }
                userMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("drag to select your location")
                        .draggable(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 16f));
                setUserSelectedLocation(latLng);
                locationTextView.setText(userAddress);
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (userMarker != null) {
                    userMarker.remove();
                }
                getCurrentUserLocation();
                return true;
            }
        });

        onMarkerDragListener = new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                setUserSelectedLocation(latLng);
                locationTextView.setText(userAddress);
                Log.e(TAG, "onMarkerDragEnd: " + userAddress);
            }
        };

        mMap.setOnMarkerDragListener(onMarkerDragListener);
        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("location-confirmed");
                intent.putExtra("userAddress", userAddress);
                intent.putExtra("userLat", userLatLong.latitude);
                intent.putExtra("userLong", userLatLong.longitude);
                LocalBroadcastManager.getInstance(SelectLocationActivity.this).sendBroadcast(intent);
                finish();
            }
        });
    }

    private void getCurrentUserLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng latLng = new LatLng(latitude, longitude);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                                setUserSelectedLocation(latLng);
                                locationTextView.setText(userAddress);
                            }
                        }
                    }
            );
        } else {
            Toast.makeText(SelectLocationActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                setUserSelectedLocation(latLng);
                locationTextView.setText(userAddress);
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
}
