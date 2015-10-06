package project.huyjack.traincpu;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

import project.huyjack.traincpu.Listener.GenerateModelListener;
import project.huyjack.traincpu.Test.DataCollector;


public class MainActivity extends Activity implements GenerateModelListener {
    public static final String TAG = MainActivity.class.getName();
    private static final String FILE_NAME = "data";

    private TextView txtTimeout, txtPercent;
    private Button btnGenModel, btnCollect;
    private ArrayList<TrainData> trainingDatas = null;
    private MultipleLinearRegression regression = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Control
        txtTimeout = (EditText) findViewById(R.id.txtTimeout);
        btnGenModel = (Button) findViewById(R.id.btnGenModel);

        txtPercent = (EditText) findViewById(R.id.txtPercent);
        btnCollect = (Button) findViewById(R.id.btnCollect);
        trainingDatas = new ArrayList<TrainData>();
        handleBtnOnclick();
    }

    private void handleBtnOnclick() {

        btnGenModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Start generating data model!", Toast.LENGTH_LONG).show();
                generateModel();
            }
        });

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Start collecting data!", Toast.LENGTH_LONG).show();
                int percent = Integer.parseInt(txtPercent.getText().toString());
                DataCollector dataCollector = new DataCollector(v.getContext(), percent);
                int timeOut = Integer.parseInt(txtTimeout.getText().toString());
                dataCollector.startCollectDataByTimeout(timeOut);
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
