package guardian.dns.com.guardian.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


import guardian.dns.com.guardian.R;
import guardian.dns.com.guardian.service.LocationMonitorService;

public class MainActivity extends AppCompatActivity {

    ToggleButton startLearningBtn ;

    LocationMonitorService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    }



    @Override
    protected void onStart() {
        super.onStart();

      //  Intent intent = new Intent(this, LocationMonitorService.class);
     //   bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if(mService!=null && mService.getRunningMood()!=null ){
            if(mService.getRunningMood().equals(Constant.LEARNING_MOOD)){
                startLearningBtn.setChecked(true);
            }else{
                startLearningBtn.setChecked(false);
            }

        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLearning(){
       /* Intent locationServiceIntent = new Intent(this, LocationMonitorService.class);
        locationServiceIntent.putExtra("APP_MOOD", Constant.LEARNING_MOOD);
        startService(locationServiceIntent);*/

        Intent intent = new Intent(this, LocationMonitorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private void stopLearning(){
        //stopService()
       /* Intent locationServiceIntent = new Intent(this, LocationMonitorService.class);
        stopService(locationServiceIntent);*/
        unbindService(mConnection);
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationMonitorService.LocalBinder binder = (LocationMonitorService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public class Constant{
        public static final String LEARNING_MOOD="LEANING";
        public static final String GARD_MOOD="GARD";
    }
}
