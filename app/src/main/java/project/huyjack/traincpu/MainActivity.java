package project.huyjack.traincpu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.Manager.BatteryManager;
import project.huyjack.traincpu.Manager.Common;


public class MainActivity extends Activity {
    private TextView txtTimeout;
    private Button btnGenModel, btnTest;
    private  PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Control
        txtTimeout = (EditText)findViewById(R.id.txtTimeout);
        btnGenModel = (Button)findViewById(R.id.btnGenModel);
        btnTest = (Button)findViewById(R.id.btnTest);
       // Intent intent = new Intent(this, GenerateModelService.class);
        //startService(intent);
        handleBtnOnclick();
    }

    private void handleBtnOnclick(){
        btnGenModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                //Start Service
                Intent intent = new Intent(MainActivity.this, GenerateModelService.class);
                MainActivity.this.startService(intent);

                //Stop Service
                Timer timer = new Timer ();
                TimerTask hourlyTask = new TimerTask () {
                    @Override
                    public void run () {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //Generate Model
                                Training train = new Training(0);
                                TrainingData data = train.GetData("data.txt");
                                MultipleLinearRegression regression = new MultipleLinearRegression(data.component, data.power);
                                new Common().writeToFile(regression.beta(0) + "," + regression.beta(1) + ","
                                        + regression.beta(2) + "\n", "model.txt");
                                MainActivity.this.stopService(new Intent(MainActivity.this, GenerateModelService.class));
                            }
                        });
                    }
                };
                timer.schedule(hourlyTask, Long.parseLong(txtTimeout.getText().toString()) * 1000);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryManager batteryManager = new BatteryManager();
                final int  batteryLevelIn = batteryManager.getBatteryLevel();
                Training training = new Training(Integer.parseInt(txtTimeout.getText().toString()));
                Thread thread = new Thread(training);
                training.run();
                //Start Service
//                Intent intent = new Intent(MainActivity.this, TestService.class);
//                MainActivity.this.startService(intent);

                //Call Task
                callAsynchronousTask();

                //Stop Service
                Timer timer = new Timer ();
                TimerTask hourlyTask = new TimerTask () {
                    @Override
                    public void run () {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                Training train = new Training(0);
                                TrainingData data = train.GetData("data.txt");
                                Experiment experiment = new Experiment();
                                double totalPower = experiment.EstimateTotalPower(data.component);

                                BatteryManager batteryManager1 = new BatteryManager();
                                int batteryLevelOut = batteryManager1.getBatteryLevel();
                                int disBattery = batteryLevelIn - batteryLevelOut;

                                new Common().writeToFile(txtTimeout.getText().toString() + ": " + totalPower + ", " + disBattery + "\n","test.txt");

                                MainActivity.this.stopService(new Intent(MainActivity.this, TestService.class));
                            }
                        });
                    }
                };
                timer.schedule(hourlyTask, Long.parseLong(txtTimeout.getText().toString()) * 1000);
            }
        });
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this,GenerateModelService.class));
        stopService(new Intent(this,TestService.class));
        super.onStop();
    }

    private static final void doWork() {
        long limit = 2000000000;
        double sum = 0;
        for (long m = 0 ; m < limit ; m++) {
            sum = (double) (1 / m);
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            double i = 1.0;
                            double sum = 0.0;
                            while (i < 1000000) {
                                sum = sum + 1/i;
                                i = i + 1;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 2000 ms
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
}
