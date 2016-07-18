package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * 직접 만들어보는 설정 화면
 * API에서 제공하는 설정 화면은 다른 방법으로 만들어야 함
 *
 * @author Mike
 */
public class SettingsActivity extends ActionBarActivity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setTitle("Settings");
    }

}
