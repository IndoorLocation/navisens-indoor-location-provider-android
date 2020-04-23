package io.indoorlocation.navisens;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;

import java.util.Map;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;
import io.indoorlocation.core.IndoorLocationProviderListener;

public class NavisensIndoorLocationProvider extends IndoorLocationProvider implements MotionDnaInterface, IndoorLocationProviderListener {

    private Context mContext;

    private MotionDnaApplication mMotionDna;
    private String mNavisensKey;
    private IndoorLocationProvider mSourceProvider;

    private boolean mStarted = false;
    private Double mCurrentFloor = null;

    private int mUpdateRate = 1000;
    private MotionDna.PowerConsumptionMode mPowerMode = MotionDna.PowerConsumptionMode.PERFORMANCE;

    /**
     * Create a new instance of Navisens location provider
     * @param context
     * @param navisensKey
     */
    public NavisensIndoorLocationProvider(Context context, String navisensKey) {
        super();
        mContext = context;
        mNavisensKey = navisensKey;
        mMotionDna = new MotionDnaApplication(this);
    }

    /**
     * Create a new instance of Navisens location provider
     * @param context
     * @param navisensKey
     * @param sourceProvider will be use to provide location to navisens
     */
    public NavisensIndoorLocationProvider(Context context, String navisensKey, IndoorLocationProvider sourceProvider) {
        super();
        mContext = context;
        mNavisensKey = navisensKey;
        mSourceProvider = sourceProvider;
        mSourceProvider.addListener(this);
        mMotionDna = new MotionDnaApplication(this);
    }

    /**
     * Get the instance of motion dna used by this provider.
     * It allows you to configure navisens as you want.
     * @return MotionDnaApplication
     */
    public MotionDnaApplication getMotionDna() {
        return mMotionDna;
    }

    /**
     * Manually provide location to navisens
     * @param indoorLocation
     */
    public void setIndoorLocation(IndoorLocation indoorLocation) {
        dispatchIndoorLocationChange(indoorLocation);
        mCurrentFloor = indoorLocation.getFloor();
        mMotionDna.setLocationLatitudeLongitude(indoorLocation.getLatitude(), indoorLocation.getLongitude());
        mMotionDna.setHeadingMagInDegrees();
        if (mCurrentFloor != null) {
            mMotionDna.setFloorNumber(mCurrentFloor.intValue());
        }
    }

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        if (!mStarted) {
            mStarted = true;
            mMotionDna.runMotionDna(mNavisensKey);
            mMotionDna.setCallbackUpdateRateInMs(mUpdateRate);
            mMotionDna.setPowerMode(mPowerMode);
            Log.i("Debug", "Start");
        }
    }

    @Override
    public void stop() {
        if (mStarted) {
            mStarted = false;
            mMotionDna.stop();
        }
    }

    @Override
    public boolean isStarted() {
        return mStarted;
    }

    @Override
    public void receiveMotionDna(MotionDna motionDna) {
        MotionDna.Location location = motionDna.getLocation();
        mCurrentFloor = (double)location.floor;
        IndoorLocation indoorLocation = new IndoorLocation(getName(), location.globalLocation.latitude, location.globalLocation.longitude, mCurrentFloor, System.currentTimeMillis());
        dispatchIndoorLocationChange(indoorLocation);
        Log.i("Debug", "Receive motion dna + dispatch");
    }

    @Override
    public void receiveNetworkData(MotionDna motionDna) {

    }

    @Override
    public void receiveNetworkData(MotionDna.NetworkCode networkCode, Map<String, ?> map) {

    }

    @Override
    public void reportError(MotionDna.ErrorCode errorCode, String s) {
        Log.i("Debug", "Error " + s);
        this.dispatchOnProviderError(new Error(errorCode.toString() + " " + s));
    }

    @Override
    public Context getAppContext() {
        return mContext.getApplicationContext();
    }

    @Override
    public PackageManager getPkgManager() {
        return mContext.getPackageManager();
    }

    @Override
    public void onProviderStarted() {
        this.dispatchOnProviderStarted();
    }

    @Override
    public void onProviderStopped() {
        this.dispatchOnProviderStopped();
    }

    @Override
    public void onProviderError(Error error) {
        dispatchOnProviderError(error);
    }

    @Override
    public void onIndoorLocationChange(IndoorLocation indoorLocation) {
        setIndoorLocation(indoorLocation);
    }
}
