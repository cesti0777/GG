package org.androidtown.actionbar;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

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

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
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