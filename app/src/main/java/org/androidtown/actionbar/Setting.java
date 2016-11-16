package org.androidtown.actionbar;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;


public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
//        addPreferencesFromResource(R.xml.pref);
    }

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
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference pref) {
            if(pref.equals(findPreference("help_temp"))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(" ※ 개인차가 있을 수 있으며,  \n" +
                        "복부 측정을 기준으로 합니다. \n\n" +
                        "[정상 체온 범위] \n" +
                        "- 0~2세 : 36.3~37.9˚C \n" +
                        "- 3세 : 36.0~37.7˚C");
                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
            else if(pref.equals(findPreference("help_heart"))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("※ 개인차가 있을 수 있으니 \n" +
                        "참고용으로만 사용하세요. \n\n" +
                        "[남성 정상 심박 수 범위] \n" +
                        "- 1세 미만 : 115~137 bpm \n" +
                        "- 1세 : 107~122 bpm \n" +
                        "- 2-3세 : 96~112 bpm \n\n" +
                        "-----------------------------\n\n"+
                        "[여성 정상 심박 수 범위] \n" +
                        "- 1세 미만 : 118~137 bpm \n" +
                        "- 1세 : 110~125 bpm \n" +
                        "- 2-3세 : 98~114 bpm ");
                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
                return super.onPreferenceTreeClick(preferenceScreen, pref);
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