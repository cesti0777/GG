package org.androidtown.actionbar;

/**
 * Created by J Bin on 2016-07-13.
 */
public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "Typo_SsangmunDongB.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Typo_SsangmunDongB.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Typo_SsangmunDongB.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Typo_SsangmunDongB.ttf");
    }
}
