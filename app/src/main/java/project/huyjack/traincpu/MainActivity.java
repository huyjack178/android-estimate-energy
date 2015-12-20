package project.huyjack.traincpu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.listener.GenerateModelListener;
import project.huyjack.traincpu.test.DataCollector;
import project.huyjack.traincpu.test.DataCollectorService;
import project.huyjack.traincpu.test.DataCollectorServiceIntent;


public class MainActivity extends Activity implements GenerateModelListener {
    public static final String TAG = MainActivity.class.getName();
    private static final String FILE_NAME = "data";

    private TextView txtTimeout, txtPercent, txtStartPercent;
    private Button btnGenModel, btnCollect, btnStop;
    private ArrayList<TrainData> trainingDatas = null;
    private MultipleLinearRegression regression = null;
    private DataCollectorService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((DataCollectorService.LocalBinder) service).getService();
            Toast.makeText(MainActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Toast.makeText(MainActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Control
        txtTimeout = (EditText) findViewById(R.id.txtTimeout);
        btnGenModel = (Button) findViewById(R.id.btnGenModel);

        txtPercent = (EditText) findViewById(R.id.txtPercent);
        txtStartPercent = (EditText) findViewById(R.id.txtStartPercent);
        btnCollect = (Button) findViewById(R.id.btnCollect);
        trainingDatas = new ArrayList<TrainData>();
        btnStop = (Button) findViewById(R.id.btnStop);
        handleBtnOnclick();
    }

    private void handleBtnOnclick() {
        //Init for btnCollect service

        btnGenModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Start generating data model!", Toast.LENGTH_LONG).show();
//                generateModel();
                Toast.makeText(v.getContext(), "Start collecting data!", Toast.LENGTH_LONG).show();
//                int percent = Integer.parseInt(txtPercent.getText().toString());
//                int startPercent = Integer.parseInt(txtStartPercent.getText().toString());
//                DataCollector dataCollector = new DataCollector();
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, DataCollectorService.class);
                int timeOut = Integer.parseInt(txtTimeout.getText().toString());
                intent.putExtra("timeOut", timeOut);
                startService(intent);
                PendingIntent collectDataService = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(collectDataService);
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (timeOut + 5) * 1000, collectDataService);


            }
        });

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Start collecting data!", Toast.LENGTH_LONG).show();
//                int percent = Integer.parseInt(txtPercent.getText().toString());
//                int startPercent = Integer.parseInt(txtStartPercent.getText().toString());
//                DataCollector dataCollector = new DataCollector();
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, DataCollectorService.class);
                int timeOut = Integer.parseInt(txtTimeout.getText().toString());
                intent.putExtra("timeOut", timeOut);
                startService(intent);
                PendingIntent collectDataService = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(collectDataService);
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (timeOut + 5) * 1000, collectDataService);
                this.RunCodeTest();
            }

            private void RunCodeTestWithoutHandler() {
                for (int i = 0; i < 100000000; i++) {
                    double a = 1000;
                    double d = 230;
                    a = a / d + d / a;
                }
            }

            private void RunCodeTest() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 100000000; i++) {
                            double a = 1000;
                            double d = 230;
                            a = a / d + d / a;
                        }
                    }
                }, 6000);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Stop collecting data!", Toast.LENGTH_LONG).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnalysisListener(TrainData trainingData) {
        trainingDatas.add(trainingData);
        if (trainingDatas.size() == Integer.parseInt(txtTimeout.getText().toString())) {
            TrainData data = new TrainData(trainingDatas.size(), 3);
            for (int i = 0; i < trainingDatas.size(); i++) {
                data.component[i][0] = 1;
                data.component[i][1] = trainingDatas.get(i).getFrequency();
                data.component[i][2] = trainingDatas.get(i).getCPUUsage();
                data.power[i] = trainingDatas.get(i).getPower();
            }
            regression = new MultipleLinearRegression(data.component, data.power);
            Log.e(TAG, regression.beta(0) + "," + regression.beta(1) + ","
                    + regression.beta(2));

            Log.e(TAG, trainingData.getFrequency() + "");
            Log.e(TAG, trainingDatas.size() + "");
        }
    }

//        private void generateModel() {
//            final Timer startTimer = new Timer();
//            EnergyEstimator estimator = new EnergyEstimator(startTimer);
//            estimator.runTrainingData(MainActivity.this, getApplicationContext());
//
////        final Timer endTimer = new Timer();
////        TimerTask hourlyTask = new TimerTask() {
////            @Override
////            public void run() {
////                runOnUiThread(new Runnable() {
////                    public void run() {
//            //Generate Model
//            TrainData data = new TrainData(trainingDatas.size(), 3);
//            for (int i = 0; i < trainingDatas.size(); i++) {
//                data.component[i][0] = 1;
//                data.component[i][1] = trainingDatas.get(i).getFrequency();
//                data.component[i][2] = trainingDatas.get(i).getCPUUsage();
//                data.power[i] = trainingDatas.get(i).getPower();
//            }
//            regression = new MultipleLinearRegression(data.component, data.power);
//            Log.e(TAG, regression.beta(0) + "," + regression.beta(1) + ","
//                    + regression.beta(2));
////                        startTimer.cancel();
////                        startTimer.purge();
////                        endTimer.cancel();
////                        endTimer.purge();
////                    }
////                });
////            }
////
////        };
//
////        endTimer.schedule(hourlyTask, Long.parseLong(txtTimeout.getText().toString()) * 1000);
//        }

}
