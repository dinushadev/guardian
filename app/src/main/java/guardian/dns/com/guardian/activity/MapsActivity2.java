package guardian.dns.com.guardian.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import guardian.dns.com.guardian.R;
import guardian.dns.com.guardian.data.LocationDataContract;
import guardian.dns.com.guardian.data.LocationDbHelper;
import guardian.dns.com.guardian.utill.AppUtill;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {



    private static final String TAG = "MapsActivity";
    private static  int MAX_DEVIATION = 500;

    private GoogleMap mMap;

    private static int MAX_DISTANCE_OF_TWO_POINT = 500;
    LocationDbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MAX_DISTANCE_OF_TWO_POINT = AppUtill.getMaxRadiusPref(getApplicationContext());

        MAX_DEVIATION = AppUtill.getMaxRadiusPref(getApplicationContext());
        dbHelper = new LocationDbHelper(getApplicationContext());

        db = dbHelper.getReadableDatabase();

        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        ArrayList<LatLng> pointList = getRouteData();

        if (pointList != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList.get(0), 13));
/*
            PolygonOptions polygonOptions = new PolygonOptions().addAll(pointList).strokeColor(Color.RED)
                    .fillColor(Color.BLUE);
            mMap.addPolygon(polygonOptions);*/

            for (LatLng latLon : pointList) {
                CircleOptions circleOptions = new CircleOptions()
                        .center(latLon)
                        .radius(MAX_DISTANCE_OF_TWO_POINT)
                        .strokeWidth(5)
                        .strokeColor(Color.argb(60, 51, 51, 224))
                        .fillColor(Color.argb(50, 51, 51, 204)); // In meters

                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

            }

        }

    }

    private ArrayList<LatLng> getRouteData() {
        Cursor cursor = db.rawQuery("select * from track", null);
        ArrayList<LatLng> pointList = null;

        Log.i(TAG, "Pin count: " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            pointList = new ArrayList<>();
            LatLng latLng = null;
            int latCoumnIndex = cursor.getColumnIndex(LocationDataContract.TrackEntry.COLUMN_NAME_LAT);
            int lonCoumnIndex = cursor.getColumnIndex(LocationDataContract.TrackEntry.COLUMN_NAME_LON);
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(latCoumnIndex);
                double lon = cursor.getDouble(lonCoumnIndex);
                Log.i(TAG, "LAT:" + lat + " LON:" + lon);
                latLng = new LatLng(lat, lon);
                pointList.add(latLng);
                cursor.moveToNext();

            }
        } else {
            Toast.makeText(getBaseContext(), "No Learned Geo data Found", Toast.LENGTH_SHORT).show();
        }

        return pointList;

    }

}
