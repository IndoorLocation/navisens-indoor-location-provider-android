package io.indoorlocation.navisens.demoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;

import org.w3c.dom.Text;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;
import io.indoorlocation.manual.ManualIndoorLocationProvider;
import io.indoorlocation.navisens.LocationListener;
import io.indoorlocation.navisens.NavisensIndoorLocationProvider;
import io.mapwize.mapwizeformapbox.AccountManager;
import io.mapwize.mapwizeformapbox.MapOptions;
import io.mapwize.mapwizeformapbox.MapwizePlugin;
import io.mapwize.mapwizeformapbox.model.LatLngFloor;

import static com.navisens.motiondnaapi.MotionDna.VerticalMotionStatus.VERTICAL_STATUS_LEVEL_GROUND;

public class MapActivity extends AppCompatActivity implements LocationListener { // LocationListener to retrieve each location updates

    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private NavisensIndoorLocationProvider navisensIndoorLocationProvider;
    private static final int REQUEST_MDNA_PERMISSIONS = 1;

    private MotionDnaApplication motionDna;

    private double lastLocationFloor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.mapwize");
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.setStyleUrl("http://outdoor.mapwize.io/styles/mapwize/style.json?key=" + AccountManager.getInstance().getApiKey());

        final IndoorLocationProvider manualIndoorLocationProvider = new ManualIndoorLocationProvider();

        navisensIndoorLocationProvider = new NavisensIndoorLocationProvider(getApplicationContext(), manualIndoorLocationProvider,"uu6oF6dDdNsIBWBez4pw2GuMwNWGJlLpRjVjsa4c23XrT8wqT7BKnXS7WuWSyPfc");


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
        motionDna = new MotionDnaApplication(navisensIndoorLocationProvider);
        navisensIndoorLocationProvider.locationListener = this;
        navisensIndoorLocationProvider.start(motionDna);
        navisensIndoorLocationProvider.start();
        motionDna.setAverageFloorHeight(4.8);

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
    public void onLocationChange(MotionDna.Location location) {
        Log.d("MapActivity", "coucou");
        TextView logLocation = findViewById(R.id.logLocation);
        String vMotion;

        switch (location.verticalMotionStatus) {
            case VERTICAL_STATUS_LEVEL_GROUND:
                vMotion = "VERTICAL_STATUS_LEVEL_GROUND";
                break;
            case VERTICAL_STATUS_ESCALATOR_UP:
                vMotion = "VERTICAL_STATUS_ESCALATOR_UP";
                break;
            case VERTICAL_STATUS_ESCALATOR_DOWN:
                vMotion = "VERTICAL_STATUS_ESCALATOR_DOWN";
                break;
            case VERTICAL_STATUS_ELEVATOR_UP:
                vMotion = "VERTICAL_STATUS_ELEVATOR_UP";
                break;
            case VERTICAL_STATUS_ELEVATOR_DOWN:
                vMotion = "VERTICAL_STATUS_ELEVATOR_DOWN";
                break;
            case VERTICAL_STATUS_STAIRS_UP:
                vMotion = "VERTICAL_STATUS_STAIRS_UP";
                break;
            case VERTICAL_STATUS_STAIRS_DOWN:
                vMotion = "VERTICAL_STATUS_STAIRS_DOWN";
                break;
            default:
                vMotion = "NO";
        }

        String txt = "floor " + location.floor + "\nmotion: " + vMotion + "\naltitude: " + location.absoluteAltitude;

        logLocation.setText(txt);


        if (lastLocationFloor != location.floor) {
            mapwizePlugin.setFloor((double)location.floor);
        }
        lastLocationFloor = location.floor;
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
