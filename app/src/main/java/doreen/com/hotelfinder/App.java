package doreen.com.hotelfinder;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Doreen on 3/22/2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initUIL();

    }

    private void initUIL() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
