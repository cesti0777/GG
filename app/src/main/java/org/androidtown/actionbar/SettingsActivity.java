package org.androidtown.actionbar;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * 직접 만들어보는 설정 화면
 * API에서 제공하는 설정 화면은 다른 방법으로 만들어야 함
 *
 * @author Mike
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private SeekBarPreference _seekBarPref;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);
            _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", ""+radius));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        boolean alarmOnOff = pref.getBoolean("alarmOnOff", false);
        boolean talarm = pref.getBoolean("talarm", false);
        boolean palarm = pref.getBoolean("palarm", false);
        boolean malarm = pref.getBoolean("malarm", false);
        boolean aalarm = pref.getBoolean("aalarm", false);


        editor.putBoolean("alarmOnOff", alarmOnOff);
        editor.putBoolean("talarm", talarm);
        editor.putBoolean("palarm", palarm);
        editor.putBoolean("malarm", malarm);
        editor.putBoolean("aalarm", aalarm);


        editor.commit();
    }
}