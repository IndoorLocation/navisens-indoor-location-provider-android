package io.indoorlocation.navisens.demoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.navisens.motiondnaapi.MotionDnaApplication;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;
import io.indoorlocation.manual.ManualIndoorLocationProvider;
import io.indoorlocation.navisens.NavisensIndoorLocationProvider;
import io.mapwize.mapwizeformapbox.MapOptions;
import io.mapwize.mapwizeformapbox.MapwizePlugin;
import io.mapwize.mapwizeformapbox.model.LatLngFloor;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private NavisensIndoorLocationProvider navisensIndoorLocationProvider;
    private static final int REQUEST_MDNA_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.mapwize");
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        final IndoorLocationProvider manualIndoorLocationProvider = new ManualIndoorLocationProvider();

        navisensIndoorLocationProvider = new NavisensIndoorLocationProvider(getApplicationContext(), manualIndoorLocationProvider,"<YOUR NAVISENS KEY>");


        MapOptions opts = new MapOptions.Builder().build();
        mapwizePlugin = new MapwizePlugin(mapView, opts);
        mapwizePlugin.setOnDidLoadListener(new MapwizePlugin.OnDidLoadListener() {
            @Override
            public void didLoad(MapwizePlugin plugin) {

                startLocationService();

                mapwizePlugin.setOnMapClickListener(new MapwizePlugin.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLngFloor latLngFloor) {

                        Location location = new Location(navisensIndoorLocationProvider.getName());
                        location.setLatitude(latLngFloor.getLatitude());
                        location.setLongitude(latLngFloor.getLongitude());
                        IndoorLocation indoorLocation = new IndoorLocation(navisensIndoorLocationProvider.getName(), latLngFloor.getLatitude(), latLngFloor.getLongitude(), latLngFloor.getFloor(), System.currentTimeMillis());
                        manualIndoorLocationProvider.dispatchIndoorLocationChange(indoorLocation);
                    }
                });

            }
        });
    }

    private void setupLocationProvider() {
        navisensIndoorLocationProvider.start();
        mapwizePlugin.setLocationProvider(navisensIndoorLocationProvider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (MotionDnaApplication.checkMotionDnaPermissions(this) == true) {
            setupLocationProvider();
        }
    }

    private void startLocationService() {
        if  (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS);
        }
        else {
            setupLocationProvider();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
