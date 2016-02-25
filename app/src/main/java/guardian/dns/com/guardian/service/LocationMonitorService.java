package guardian.dns.com.guardian.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import guardian.dns.com.guardian.data.LocationDataContract;
import guardian.dns.com.guardian.data.LocationDbHelper;

public class LocationMonitorService extends Service {

    private static final String TAG = "LocationMonitorService";
    private static final float MAX_DISTANCE_OF_TWO_POINT = 500;

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

    ArrayList<LatLng> learnedPoints ;


    public LocationMonitorService() {

    }

    @Override
    public void onCreate() {

         dbHelper = new LocationDbHelper(getApplicationContext());

         db = dbHelper.getWritableDatabase();

         learnedPoints = getRouteData();
        Log.d(TAG,"Point List Loaded"+ learnedPoints);

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
       if(intent != null){
           String mood = intent.getStringExtra("APP_MOOD");
           RUNNING_MOOD = mood;

       }

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

          //  mLastLocation.set(location);
            if(!isCoverThisPoint(location)){

                updateData(location);
                if(learnedPoints == null){
                    learnedPoints = new ArrayList<>();
                }
                LatLng tmpLatLog = new LatLng(location.getLatitude(),location.getLongitude());
                learnedPoints.add(tmpLatLog);
            }
            //onLocationChanged(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private boolean isCoverThisPoint(Location location) {
        boolean status =false;
        if(learnedPoints != null){
            for(LatLng loc :learnedPoints){
                Location location1 = new Location("");
                location1.setLatitude(loc.latitude);
                location1.setLongitude(loc.longitude);
                float distance = location.distanceTo(location1);
                Log.d(TAG,"Distance:"+distance+" Acuracy:"+location.getAccuracy());
                if(Math.abs(distance) < MAX_DISTANCE_OF_TWO_POINT){
                    status =true;
                    break;
                }
            }
        }
        return status;
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

    private ArrayList<LatLng> getRouteData(){
        Cursor cursor = db.rawQuery("select * from track", null);
        ArrayList<LatLng> pointList =null;

        Log.i(TAG, "Pin count: " + cursor.getCount());
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            pointList = new ArrayList<>();
            LatLng latLng = null;
            int latCoumnIndex = cursor.getColumnIndex(LocationDataContract.TrackEntry.COLUMN_NAME_LAT);
            int lonCoumnIndex = cursor.getColumnIndex(LocationDataContract.TrackEntry.COLUMN_NAME_LON);
            while (!cursor.isAfterLast()){
                double lat = cursor.getDouble(latCoumnIndex);
                double lon = cursor.getDouble(lonCoumnIndex);
                Log.i(TAG, "LAT:"+lat+ " LON:"+lon);
                latLng = new LatLng(lat,lon);
                pointList.add(latLng);
                cursor.moveToNext();

            }
        }else{
            Toast.makeText(getBaseContext(), "No Learned Geo data Found", Toast.LENGTH_SHORT).show();
        }

        return pointList;

    }

  /*  LocationListener[] mLocationListeners = new LocationListener[] {
            new MyLocationListener(LocationManager.GPS_PROVIDER),
            new MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };*/
}
