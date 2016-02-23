package guardian.dns.com.guardian.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import guardian.dns.com.guardian.data.LocationDataContract;
import guardian.dns.com.guardian.data.LocationDbHelper;

public class LocationMonitorService extends Service {

    private static final String TAG = "LocationMonitorService";

    private final IBinder mBinder = new LocalBinder();
    MyLocationListener listener;


    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    private static final float LOCATION_DISTANCE = 500;
    private static String RUNNING_MOOD ;

    LocationManager locationManager ;
    String provider;

    LocationDbHelper dbHelper;

    SQLiteDatabase db ;

    public LocationMonitorService() {

    }

    @Override
    public void onCreate() {

         dbHelper = new LocationDbHelper(getApplicationContext());

         db = dbHelper.getWritableDatabase();

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


    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
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
            Log.e(TAG, "LocationListener  " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            updateData(location);
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

    private void updateData(Location location) {


// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationDataContract.TrackEntry.COLUMN_NAME_LAT, location.getLatitude());
        values.put(LocationDataContract.TrackEntry.COLUMN_NAME_LON, location.getLongitude());
        values.put(LocationDataContract.TrackEntry.COLUMN_NAME_TIME, location.getTime());

// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                LocationDataContract.TrackEntry.TABLE_NAME,
               null,
                values);
        Log.i(TAG,"New row inserted.."+newRowId);
    }

  /*  LocationListener[] mLocationListeners = new LocationListener[] {
            new MyLocationListener(LocationManager.GPS_PROVIDER),
            new MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };*/
}
