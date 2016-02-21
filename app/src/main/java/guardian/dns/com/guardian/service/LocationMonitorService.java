package guardian.dns.com.guardian.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationMonitorService extends Service {

    private final IBinder mBinder = new LocalBinder();
    MyLocationListener listener;

    private static final String TAG = "LocationMonitorService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    private static final float LOCATION_DISTANCE = 0;
    private static String RUNNING_MOOD ;

    LocationManager locationManager ;
    String provider;

    public LocationMonitorService() {

    }

    @Override
    public void onCreate() {

        // Getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if(provider!=null && !provider.equals("")){

            // Get the location from the given provider
          //  Location location = locationManager.getLastKnownLocation(provider);
            listener =new MyLocationListener(provider);

            locationManager.requestLocationUpdates(provider, LOCATION_INTERVAL, LOCATION_DISTANCE,listener);

          /*  if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
*/
        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }


    }

    private void onLocationChanged(Location location) {

    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(listener);
        }
    }





    @Override
    public IBinder onBind(Intent intent) {
       return  mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocationMonitorService", "Received start id " + startId + ": " + intent);
        String mood = intent.getStringExtra("APP_MOOD");
        RUNNING_MOOD = mood;


        return START_STICKY;
    }

    public String getRunningMood(){
        return  RUNNING_MOOD;
    }


    public class LocalBinder extends Binder {
        public  LocationMonitorService getService() {
            return LocationMonitorService.this;
        }
    }



    private class MyLocationListener implements LocationListener
    {
        Location mLastLocation;

        public MyLocationListener(String provider)
        {
            Log.e(TAG, "LocationListener  oooooooo" + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            //onLocationChanged(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

  /*  LocationListener[] mLocationListeners = new LocationListener[] {
            new MyLocationListener(LocationManager.GPS_PROVIDER),
            new MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };*/
}
