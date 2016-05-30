package guardian.dns.com.guardian.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;


import guardian.dns.com.guardian.R;
import guardian.dns.com.guardian.service.LocationMonitorService;
import guardian.dns.com.guardian.utill.AppUtill;
import guardian.dns.com.guardian.utill.Constant;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    ToggleButton startLearningBtn ;
    Button showMapBtn ;
    Button hideButn;
    ToggleButton guardBtn;
    Button prefBtn;

    LocationMonitorService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(getBaseContext(), "Max Radius:"+maxRadius, Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton prefBtn = (FloatingActionButton) findViewById(R.id.prefBtn);
        prefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Sorry, This feature under development", Snackbar.LENGTH_LONG)   .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);

            }
        });

        showMapBtn = (Button)findViewById(R.id.showMapBtn);
        showMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity2.class);
                startActivity(intent);
            }
        });

        startLearningBtn = (ToggleButton)findViewById(R.id.learnBtn);
        startLearningBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    startLearning();
                } else {
                    // The toggle is disabled
                    stopLearning();
                }
            }
        });

        hideButn = (Button) findViewById(R.id.hideBtn);
        hideButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

        guardBtn = (ToggleButton) findViewById(R.id.gardBtn);
        guardBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    startGarding();
                } else {
                    // The toggle is disabled
                    stopGarding();
                }
            }
        });




    }



    private void stopGarding() {
        AppUtill.setRunningMood(Constant.NOT_STARTED, getApplicationContext());
        if(mConnection!= null && mBound){
            try{
                getApplicationContext().unbindService(mConnection);
                Intent locationServiceIntent = new Intent(this, LocationMonitorService.class);
                stopService(locationServiceIntent);
                Log.i(TAG, "Unbind and stop the service");
            }catch (Exception e){

            }


        }

    }
    private void startGarding() {

        stopLearning();

        startLearningBtn.setChecked(false);

        AppUtill.setRunningMood(Constant.GARD_MOOD,getApplicationContext());

        Intent locationServiceIntent = new Intent(MainActivity.this, LocationMonitorService.class);
        //locationServiceIntent.putExtra("APP_MOOD", Constant.LEARNING_MOOD);

        ComponentName serviceName= startService(locationServiceIntent);
        Log.d(TAG, serviceName.flattenToString());
        boolean bindStatus = bindService(locationServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(getApplicationContext(), LocationMonitorService.class);
        //intent.putExtra("APP_MOOD", Constant.LEARNING_MOOD);

        startService(intent);
        if(AppUtill.getRunningMood(getApplicationContext()) != null && AppUtill.getRunningMood(getApplicationContext()).equals(Constant.LEARNING_MOOD)){
            startLearningBtn.setChecked(true);
            guardBtn.setChecked(false);
        }else if(AppUtill.getRunningMood(getApplicationContext()) != null && AppUtill.getRunningMood(getApplicationContext()).equals(Constant.GARD_MOOD)){
            startLearningBtn.setChecked(false);
            guardBtn.setChecked(true);
        }

            getApplicationContext().bindService(intent, mConnection, 0);

      //  Log.d(TAG, "mConnection: " + mConnection + " Service:" + mService);
       // if(mService!=null && mService.getRunningMood()!=null ){
           /* if(mService.getRunningMood().equals(Constant.LEARNING_MOOD)){
                startLearningBtn.setChecked(true);
            }else{
                startLearningBtn.setChecked(false);
            }
*/
    //    }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void startLearning(){

       stopGarding();
        guardBtn.setChecked(false);

        AppUtill.setRunningMood(Constant.LEARNING_MOOD,getApplicationContext());

      Intent locationServiceIntent = new Intent(getApplicationContext(), LocationMonitorService.class);
        //locationServiceIntent.putExtra("APP_MOOD", Constant.LEARNING_MOOD);

       ComponentName serviceName= startService(locationServiceIntent);
        Log.d(TAG, serviceName.flattenToString());
        boolean bindStatus = bindService(locationServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
       /* Intent intent = new Intent(this, LocationMonitorService.class);
        intent.putExtra("APP_MOOD", Constant.LEARNING_MOOD);
       // ComponentName serviceName= startService(intent);
        boolean bindStatus = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG,"Bind status : "+bindStatus);
*/
    }

    private void stopLearning(){
        //stopService()
        AppUtill.setRunningMood(Constant.NOT_STARTED, getApplicationContext());
        Log.i(TAG, "M connection:"+mConnection+" mBound:"+mBound);
        if(mConnection!= null  && mBound){
            try{
                getApplicationContext().unbindService(mConnection);
                Intent locationServiceIntent = new Intent(this, LocationMonitorService.class);
                stopService(locationServiceIntent);

                Log.i(TAG, "Unbind and stop the service");
            }catch (Exception e){

            }

        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        if(mConnection!= null && mBound ){
            try {
                getApplicationContext().unbindService(mConnection);
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }


        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationMonitorService.LocalBinder binder = (LocationMonitorService.LocalBinder) service;
            mService = binder.getService();
            //Toast.makeText(MainActivity.this, "Connected"+ mService, Toast.LENGTH_SHORT) .show();
            Log.d(TAG, "mConnection: " + mConnection + " Service:" + mService + " mService.getRunningMood():" + AppUtill.getRunningMood(getApplicationContext()));
            mBound =true;
            if(AppUtill.getRunningMood(getApplicationContext()).equals(Constant.LEARNING_MOOD)){
                startLearningBtn.setChecked(true);
            }else{
                startLearningBtn.setChecked(false);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public class Constant{
        public static final String LEARNING_MOOD="LEANING";
        public static final String GARD_MOOD="GARD";
        public static final String NOT_STARTED = "NOT_STARTED";
    }
}
