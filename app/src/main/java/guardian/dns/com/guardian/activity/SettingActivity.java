package guardian.dns.com.guardian.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import guardian.dns.com.guardian.R;
import guardian.dns.com.guardian.utill.Constant;

/**
 * Created by dinusha on 5/23/16.
 */
public class SettingActivity extends PreferenceActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new MainSettingsFragment()).commit();

    }




    public static class MainSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            EditTextPreference learningDaysPre = (EditTextPreference) findPreference(Constant.guardian_contact_no);
            learningDaysPre.getText();
            learningDaysPre.setSummary(learningDaysPre.getText());

        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);


            if(key.equals(Constant.learnig_days)){
                preference.setSummary(sharedPreferences.getString(key,"")+" Days");

            }else if(key.equals(Constant.guardian_contact_no)){

                preference.setSummary(sharedPreferences.getString(key,""));

            }else if(key.equals(Constant.max_deviation)){
                preference.setSummary(preference.getSummary()+"-" +sharedPreferences.getString(key,"")+" Meters");

            }
        }





    }

}
