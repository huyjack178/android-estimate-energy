package project.huyjack.traincpu.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DataCollectorService extends Service {

    public DataCollectorService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        int timeOut = intent.getIntExtra("timeOut", 0);
        DataCollector dataCollector = new DataCollector();
        dataCollector.startCollectData(timeOut, getApplicationContext());
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
