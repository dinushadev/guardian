package guardian.dns.com.guardian.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import guardian.dns.com.guardian.activity.MainActivity;
import guardian.dns.com.guardian.utill.AppUtill;

/**
 * Created by dinusha on 5/19/16.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    private static final String TAG = "DeviceBootReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        String mood = AppUtill.getRunningMood(context.getApplicationContext());
        if(mood!= null && !mood.equals(MainActivity.Constant.NOT_STARTED)){
            Intent locationServiceIntent = new Intent(context, LocationMonitorService.class);
            //   locationServiceIntent.putExtra("APP_MOOD", MainActivity.Constant.LEARNING_MOOD);
            ComponentName serviceName=  context.getApplicationContext().startService(locationServiceIntent);
            Log.d(TAG, serviceName.flattenToString());
            Toast.makeText(context, TAG + " SERVICE STARTD mood: "+mood, Toast.LENGTH_LONG).show();
        }

    }
}
