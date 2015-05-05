package doreen.com.hotelfinder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Frederick on 4/27/2015.
 */
public class Utils {
    public static final String TAG = Utils.class.getSimpleName();

    private static SharedPreferences prefs = null;

    private static synchronized SharedPreferences getPrefs(Context c) {
        if (prefs == null)
            prefs = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return prefs;

    }

    public static synchronized void setSyncDone(Context c, boolean done) {
        SharedPreferences.Editor editor = getPrefs(c).edit();
        editor.putBoolean("done", done);
        editor.commit();
    }

    public static synchronized boolean isSyncDone(Context c) {
        return getPrefs(c).getBoolean("done", false);
    }
}
