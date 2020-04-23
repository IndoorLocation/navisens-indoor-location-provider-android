package io.indoorlocation.demoapp;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

import io.mapwize.mapwizesdk.core.MapwizeConfiguration;

/**
 * This is a demo application for Navisens indoor location provider.
 * After asking for location permission, this app will run a NavisensIndoorLocationProvider with Mapwize.
 * Click on the map to set a user location then walk with your device to see your location moving based on Navisens data.
 */
public class DemoApplication extends Application {

    // This is a demo key, giving you access to the demo building. It is not allowed to use it for production.
    // The key might change at any time without notice. Get your key by signin up at mapwize.io
    static final String MAPWIZE_API_KEY = "YOUR_MAPWIZE_API_KEY";
    static final String NAVISENS_API_KEY = "YOUR_NAVISENS_API_KEY";


    @Override
    public void onCreate() {
        super.onCreate();
        Mapbox.getInstance(this, "pk.mapwize");
        // Mapwize globale initialization
        MapwizeConfiguration config = new MapwizeConfiguration.Builder(this, MAPWIZE_API_KEY).build();
        MapwizeConfiguration.start(config);
    }

}