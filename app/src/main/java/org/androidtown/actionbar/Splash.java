package org.androidtown.actionbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by J Bin on 2016-07-28.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
