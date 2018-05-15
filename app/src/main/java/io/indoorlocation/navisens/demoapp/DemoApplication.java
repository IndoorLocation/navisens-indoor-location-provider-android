package io.indoorlocation.navisens.demoapp;

import android.app.Application;
import io.mapwize.mapwizeformapbox.AccountManager;


/**
 * This is a demo application for Navisens indoor location provider.
 * After asking for location permission, this app will run a NavisensIndoorLocationProvider with Mapwize.
 * Click on the map to set a user location then walk with your device to see your location moving based on Navisens data.
 */
public class DemoApplication extends Application {

    static final String MAPBOX_ACCESS_TOKEN = "pk.mapwize";
    // This is a demo key, giving you access to the demo building. It is not allowed to use it for production.
    // The key might change at any time without notice. Get your key by signin up at mapwize.io
    static final String MAPWIZE_API_KEY = "1f04d780dc30b774c0c10f53e3c7d4ea";
    static final String MAPWIZE_STYLE_URL_BASE = "http://outdoor.mapwize.io/styles/mapwize/style.json?key=";
    static final String NAVISENS_API_KEY = "YOUR_NAVISENS_API_KEY";


    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, MAPWIZE_API_KEY);
    }

}