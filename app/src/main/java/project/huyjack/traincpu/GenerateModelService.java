package project.huyjack.traincpu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import project.huyjack.traincpu.Manager.Common;
import project.huyjack.traincpu.Training;

public class GenerateModelService extends Service {
    public GenerateModelService() {
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Training train = new Training(0);
        new Common().deleteFile();
       // train.RunTask();
        train.RunAnalysis();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
