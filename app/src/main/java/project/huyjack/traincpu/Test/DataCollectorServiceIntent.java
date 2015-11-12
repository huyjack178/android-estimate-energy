package project.huyjack.traincpu.test;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataCollectorServiceIntent extends IntentService {




    public DataCollectorServiceIntent() {
        super("DataCollectorServiceIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int timeOut = intent.getIntExtra("timeOut", 0);
        Log.e("TIMEOUT", timeOut + "");
        DataCollector dataCollector = new DataCollector();
        dataCollector.startCollectData(timeOut, getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
