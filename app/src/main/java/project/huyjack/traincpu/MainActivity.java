package project.huyjack.traincpu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import project.huyjack.traincpu.Common.CommonUtil;
import project.huyjack.traincpu.Listener.GenerateModelListener;


public class MainActivity extends Activity implements GenerateModelListener {
    public static final String TAG = MainActivity.class.getName();
    private static final String FILE_NAME = "data";
    private int COUNT_PERCENT = 30;
    private TextView txtTimeout;
    private Button btnGenModel, btnTest;
    private ArrayList<TrainData> trainingDatas = null;
    private MultipleLinearRegression regression = null;
    private int mLevelCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Control
        txtTimeout = (EditText) findViewById(R.id.txtTimeout);
        btnGenModel = (Button) findViewById(R.id.btnGenModel);
        btnTest = (Button) findViewById(R.id.btnTest);
        trainingDatas = new ArrayList<TrainData>();
        String fileStr = CommonUtil.readFromFile(FILE_NAME + 5 + ".txt");
        Log.e(TAG, fileStr);
        handleBtnOnclick();
    }


    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        final android.os.Handler handler = new android.os.Handler();
        final List<Double> watts = new LinkedList<Double>();
        final List<Double> ampes = new LinkedList<Double>();
        final List<Double> volts = new LinkedList<Double>();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            EnergyEstimator.readBatteryStatus();
                            Double watt = EnergyEstimator.getWattBattery();
                            Double ampe = EnergyEstimator.getAmpeBattery() * 1.0;
                            Double voltage = EnergyEstimator.getVoltBattery() * 1.0;
                            //Log.e(TAG, watt + " " + ampe / 1000000 + " " + voltage / 1000000);
                            watts.add(watt);
                            ampes.add(ampe);
                            volts.add(voltage);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            Log.e(TAG, "Battery level: " + String.valueOf(level));

            if (mLevelCount == 0) {
                String resultStr = String.valueOf(level) + "\n";
                Double wattTotal = 0d;
                Double ampeTotal = 0d;

                if (watts.size() != 0) {
                    for (int i = 0; i < watts.size(); i++) {
                        wattTotal += watts.get(i);
                        ampeTotal += ampes.get(i);
                        resultStr += watts.get(i).toString() + "W\t" + ampes.get(i).toString() + "mA\n";
                    }
                    Log.e(TAG, wattTotal + "W " + ampeTotal + "mA");
                    //resultStr += wattTotal + "W " + ampeTotal + "mA\n";
                    //CommonUtil.write(FILE_NAME + COUNT_PERCENT + ".txt", resultStr);
                    watts.clear();
                    ampes.clear();
                }

                timer.schedule(timerTask, 0, 1000);
            } else if (mLevelCount == COUNT_PERCENT) {
                timer.cancel();
                timer.purge();
                String resultStr = String.valueOf(level) + "\n";
                Double wattTotal = 0d;
                Double ampeTotal = 0d;

                if (watts.size() != 0) {
                    for (int i = 0; i < watts.size(); i++) {
                        wattTotal += watts.get(i);
                        ampeTotal += ampes.get(i);
                        resultStr += watts.get(i).toString() + "W\t" + ampes.get(i).toString() + "mA\n";
                    }
                    Log.e(TAG, wattTotal + "W " + ampeTotal + "mA");
                    resultStr += wattTotal + "W " + ampeTotal + "mA\n";
                    String fileStr = CommonUtil.readFromFile(FILE_NAME + COUNT_PERCENT + ".txt");
                    CommonUtil.write(FILE_NAME + COUNT_PERCENT + ".txt", fileStr + resultStr);
                    watts.clear();
                    ampes.clear();
                }
            } else {
                String resultStr = String.valueOf(level) + "\n";
                Double wattTotal = 0d;
                Double ampeTotal = 0d;
                if (watts.size() != 0) {
                    for (int i = 0; i < watts.size(); i++) {
                        wattTotal += watts.get(i);
                        ampeTotal += ampes.get(i);
                        resultStr += watts.get(i).toString() + "W\t" + ampes.get(i).toString() + "mA\n";
                    }
                    Log.e(TAG, wattTotal + "W " + ampeTotal + "mA");
                    resultStr += wattTotal + "W " + ampeTotal + "mA\n";
                    String fileStr = CommonUtil.readFromFile(FILE_NAME + COUNT_PERCENT + ".txt");
                    Log.e(TAG, fileStr);
                    CommonUtil.write(FILE_NAME + COUNT_PERCENT + ".txt", fileStr + resultStr);
                    watts.clear();
                    ampes.clear();
                }
            }
            mLevelCount++;
        }
    };

    private void handleBtnOnclick() {

        btnGenModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                COUNT_PERCENT = Integer.parseInt(txtTimeout.getText().toString());
                Toast.makeText(v.getContext(), "Start getting battery data!", Toast.LENGTH_LONG).show();

                Log.e(TAG, COUNT_PERCENT + "%");
                v.getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                //generateModel();
            }
        });
    }

    private void generateModel() {
        final Timer startTimer = new Timer();
        EnergyEstimator estimator = new EnergyEstimator(startTimer);
        estimator.runTrainingData(MainActivity.this);

        TestCPU testCPU = new TestCPU();
        testCPU.execute(0);

        final Timer endTimer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Generate Model
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
                        startTimer.cancel();
                        startTimer.purge();
                        endTimer.cancel();
                        endTimer.purge();
                    }
                });
            }

        };
        endTimer.schedule(hourlyTask, Long.parseLong(txtTimeout.getText().toString()) * 1000);
    }

    class TestCPU extends AsyncTask<Integer, String, String> {
        @Override
        protected String doInBackground(Integer... params) {
            long limit = 200000000;
            double sum = 0;
            for (long m = 1; m < limit; m++) {
                sum = (double) (1 / m);
            }
            return null;
        }
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
        Log.e(TAG, trainingData.getFrequency() + "");
        Log.e(TAG, trainingDatas.size() + "");

    }
}
