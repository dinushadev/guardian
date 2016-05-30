package guardian.dns.com.guardian.utill;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import guardian.dns.com.guardian.activity.MainActivity;

/**
 * Created by dinusha on 5/20/16.
 */
public class AppUtill {
    private static  int DEFAULT_MAX_DISTANCE_OF_TWO_POINT = 500;
    private static  int DEFAULT_ACCURACY = 3;

    public static void  setRunningMood(String mood,Context context){
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("MOOD", mood);
        editor.apply();
    }
    public static String getRunningMood(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String mood = settings.getString("MOOD", MainActivity.Constant.NOT_STARTED);
        return mood;
    }

    public static String getSharedPref(Context context, String pref) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String val = settings.getString(pref, "");
        return val;
    }

    public static int getMaxRadiusPref(Context context) {
        String val =getSharedPref(context, Constant.max_deviation);
        int radius = 0;
        if(!val.equals("")){
            radius = Integer.parseInt(val);
        }else {
            radius = DEFAULT_MAX_DISTANCE_OF_TWO_POINT;
        }
        return radius;
    }


 /*   public static int getMaxDeviationPref(Context context) {
        String val =getSharedPref(context, Constant.max_deviation);
        int devi = 0;
        if(!val.equals("")){
            devi = Integer.parseInt(val);
        }else {
            devi = DEFAULT_MAX_DISTANCE_OF_TWO_POINT;
        }
        return devi;
    }*/
    public static int getAccuracyPref(Context context) {
        String val =getSharedPref(context, Constant.accuracy);
        int accuracy = 0;
        if(!val.equals("")){
            accuracy = Integer.parseInt(val);
        }else {
            accuracy = DEFAULT_ACCURACY;
        }
        return accuracy;
    }


    public static String getGuardianPhoneNo(Context applicationContext) {
        String val =getSharedPref(applicationContext, Constant.guardian_contact_no);
        return val;
    }
}
