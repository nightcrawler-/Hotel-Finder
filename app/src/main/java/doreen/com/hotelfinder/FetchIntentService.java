package doreen.com.hotelfinder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchIntentService extends IntentService {


    // TODO: Rename parameters
    private static final String EXTRA_LOCATION = "doreen.com.hotelfinder.extra.LOCATION";
    private static final String EXTRA_NAME = "doreen.com.hotelfinder.extra.NAME";
    private static final String ACTION_FETCH = "doreen.com.ACTION_FETCH";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetch(Context context, String location, String name) {
        Intent intent = new Intent(context, FetchIntentService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_NAME, name);
        context.startService(intent);
    }


    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                final String location = intent.getStringExtra(EXTRA_LOCATION);
                final String name = intent.getStringExtra(EXTRA_NAME);
                try {
                    handleActionFetch(location, name);
                    Utils.setSyncDone(this, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }


    private void handleActionFetch(String location, String name) throws JSONException {
       Backbone.insertData(this, Backbone.getData());
    }


}
