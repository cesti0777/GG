package org.androidtown.actionbar;

/**
 * Created by J Bin on 2016-07-13.
 */
public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "HoonWhitecatR.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "HoonWhitecatR.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "HoonWhitecatR.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "HoonWhitecatR.ttf");
    }
}
