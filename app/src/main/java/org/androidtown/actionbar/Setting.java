package org.androidtown.actionbar;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }


//    @Override
//    public boolean onPreferenceClick(Preference preference) {
//        // 도움말 선택시
//        if(preference.getKey().equals("help"))
//        {
//            startActivity(new Intent(this, Help.class));
//            Toast.makeText(getApplicationContext(), "--------", Toast.LENGTH_LONG).show();
//        }
//        return false;
//    }

    public static class MyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private SeekBarPreference _seekBarPref_t;
        private SeekBarPreference _seekBarPref_p;


        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);



        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            int radius_t = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_T", 50);
            _seekBarPref_t.setSummary(this.getString(R.string.settings_summary_t).replace("$1", ""+radius_t));

            int radius_p = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_P", 50);
            _seekBarPref_p.setSummary(this.getString(R.string.settings_summary_p).replace("$1", ""+radius_p));

        }


    }



    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.commit();
    }
}