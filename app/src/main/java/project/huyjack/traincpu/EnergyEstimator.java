package project.huyjack.traincpu;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.common.CommonUtil;
import project.huyjack.traincpu.listener.GenerateModelListener;
import project.huyjack.traincpu.manager.BatteryManager;
import project.huyjack.traincpu.manager.CPUManager;
import project.huyjack.traincpu.manager.ScreenManager;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class EnergyEstimator {
    public static final String TAG = EnergyEstimator.class.getName();
    private Timer timer = null;
    private CPUManager cpuManager = null;
    private ScreenManager screenManager = null;
    private static BatteryManager batteryManager = new BatteryManager();
    private static int current = 0;
    private static int voltage = 0;

    public EnergyEstimator(Timer timer) {
        this.timer = timer;
        cpuManager = new CPUManager();
        screenManager = new ScreenManager();
    }

    public static void readBatteryStatus() {
        current = batteryManager.getBatteryCurrent();
        voltage = batteryManager.getBatteryVoltage();
    }

    public static double getWattBattery() {
        BigDecimal watt = new BigDecimal(current).multiply(new BigDecimal(voltage));
        watt = watt.divide(new BigDecimal(Math.pow(10, 12)));
        //Log.e(TAG, current + " " + voltage + " " + watt);
        return watt.doubleValue();
    }

    public static int getAmpeBattery() {
        return current;
    }

    public static int getVoltBattery() {
        return voltage;
    }

    public void runTrainingData(final GenerateModelListener generateModelListener, final Context context) {
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

//                            Write Screen Brightness
                            trainingData.setScreenBright(screenManager.getScreenBright(context));


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
}
