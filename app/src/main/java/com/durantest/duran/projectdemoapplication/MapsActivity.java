package com.durantest.duran.projectdemoapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager lms;
    private Marker currentMarker;
    private LatLng prevLatLng;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String url = "http://www.pink-fun.com.tw/edufor4g/?school=fcu&app=test";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
        } catch (Exception e) {

        }

    }

    private void setMarker(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(current).
                    title("Lat: " + location.getLatitude() + " Long:" + location.getLongitude()));
        } else {
            currentMarker.setPosition(current);
            currentMarker.setTitle("Lat: " + location.getLatitude() + " Long:" + location.getLongitude());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    private void setPolyLine(Location location) {
        if (prevLatLng == null) {
            prevLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addPolyline(new PolylineOptions()
                    .add(prevLatLng, currentLatLng).width(5).color(Color.BLUE));
        }
    }

    private void setCamera(Location location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lms = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_PHONE_STATE);
                }
                return;
            }
            lms.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                setCamera(location);
                setMarker(location);
                setPolyLine(location);
            }

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    lms.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(location!=null){
                        setCamera(location);
                        setMarker(location);
                        setPolyLine(location);
                    }
                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap != null){
            setCamera(location);
            setMarker(location);
            setPolyLine(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
