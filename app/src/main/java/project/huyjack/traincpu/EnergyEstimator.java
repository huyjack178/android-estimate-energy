package project.huyjack.traincpu;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.Common.CommonUtil;
import project.huyjack.traincpu.Listener.GenerateModelListener;
import project.huyjack.traincpu.Manager.BatteryManager;
import project.huyjack.traincpu.Manager.CPUManager;
import project.huyjack.traincpu.Manager.ScreenManager;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class EnergyEstimator {
    public static final String TAG = EnergyEstimator.class.getName();
    private Timer timer = null;
    private CPUManager cpuManager = null;
    private ScreenManager screenManager = null;
    private BatteryManager batteryManager = null;

    public EnergyEstimator(Timer timer) {
        this.timer = timer;
        cpuManager = new CPUManager();
        screenManager = new ScreenManager();
        batteryManager = new BatteryManager();
    }

    public static double getWattBattery(){
        BatteryManager batteryManager = new BatteryManager();
        int current = batteryManager.getBatteryCurrent();
        int vol = batteryManager.getBatteryVoltage();
        double watt = ((current) * (vol)) / 10e12;
        return watt;
    }

    public static int getAmpeBattery(){
        BatteryManager batteryManager = new BatteryManager();
        return batteryManager.getBatteryCurrent();
    }

    public static int getVoltBattery(){
        BatteryManager batteryManager = new BatteryManager();
        return batteryManager.getBatteryVoltage();
    }

    public void runTrainingData(final GenerateModelListener generateModelListener) {
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            TrainData trainingData = new TrainData();
                            //Write CPU Frequency
                            cpuManager.SetCPUFrequencyRandom();
                            double cpuFrequency = Double.parseDouble(cpuManager.GetCurrentFrequency()) / 1000;
                            trainingData.setFrequency(cpuFrequency);

                            //Write CPU %
                            int[] cpuUsage = cpuManager.GetCpuUsageStatistic();
                            int cpuUsageTotal = cpuUsage[0] + cpuUsage[1] + cpuUsage[2] + cpuUsage[3];
                            trainingData.setCPUUsage(cpuUsageTotal);

                            //Write Screen Brightness
//                             screenManager.setScreenBrightRandom();
                            //cmn.writeToFile(screenManager.getScreenBright(context) + ",");

                            //Write Power
                            int current = batteryManager.getBatteryCurrent();
                            int vol = batteryManager.getBatteryVoltage();
                            double watt = ((current / 1000) * (vol / 1000)) / 1000 / 10;
                            trainingData.setPower(watt);

                            generateModelListener.onAnalysisListener(trainingData);

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }

    public void RunTest() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            CommonUtil cmn = new CommonUtil();

//                            //Write CPU Frequency
//                            cmn.writeToFile(Double.parseDouble(cpuManager.GetCurrentFrequency()) / 1000 + ",", "data.t");
//
//                            //Write CPU %
//                            int[] cpuUsage = cpuManager.GetCpuUsageStatistic();
//                            int cpuTotal = cpuUsage[0] + cpuUsage[1] + cpuUsage[2] + cpuUsage[3];
//                            cmn.writeToFile(cpuTotal + ",", FileName);
//
//                            //Write Power
//                            int current = batteryManager.getBatteryCurrent();
//                            int vol = batteryManager.getBatteryVoltage();
//                            double watt = ((current / 1000) * (vol / 1000)) / 1000 / 10;
//                            cmn.writeToFile(watt + "\n", FileName);

                        } catch (Exception e) {
                            Log.e("error", e.toString());
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 1000 ms
    }
}
