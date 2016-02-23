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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import guardian.dns.com.guardian.R;
import guardian.dns.com.guardian.data.LocationDataContract;
import guardian.dns.com.guardian.data.LocationDbHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;


    LocationDbHelper dbHelper;
    SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dbHelper = new LocationDbHelper(getApplicationContext());

        db = dbHelper.getReadableDatabase();

        setContentView(R.layout.activity_maps);
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
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/





        // Polylines are useful for marking paths and routes on the map.
        ArrayList<LatLng> pointList = getRouteData();

        if(pointList != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList.get(0), 14));
/*
            PolygonOptions polygonOptions = new PolygonOptions().addAll(pointList).strokeColor(Color.RED)
                    .fillColor(Color.BLUE);
            mMap.addPolygon(polygonOptions);*/

            for(LatLng latLon :pointList){
                CircleOptions circleOptions = new CircleOptions()
                        .center(latLon)
                        .radius(500)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.argb(50,51,204,51)); // In meters

                // Get back the mutable Circle
                Circle circle = mMap.addCircle(circleOptions);

            }

        }

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
}
