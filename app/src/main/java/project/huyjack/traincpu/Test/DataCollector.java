package project.huyjack.traincpu.Test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.Common.CommonUtil;
import project.huyjack.traincpu.EnergyEstimator;
import project.huyjack.traincpu.Manager.BatteryManager;

/**
 * Created by huyjack on 10/6/15.
 */
public class DataCollector {

    private static final String TAG = DataCollector.class.getName();
    private static final String FILE_NAME = "data";
    private static final String FILE_EXTENSION = ".txt";
    private static final String LEVEL = "level";

    private final int mPercent;
    private Context mContext;
    private int mPercentCount = 0;

    public DataCollector(Context context, int percent) {
        this.mContext = context;
        this.mPercent = percent;
    }

    public void startCollectDataByTimeout(final int timeOut) {
        final android.os.Handler handler = new android.os.Handler();
        final List<Double> watts = new LinkedList<Double>();
        final List<Double> ampes = new LinkedList<Double>();
        final List<Double> volts = new LinkedList<Double>();
        final BatteryManager batteryManager = new BatteryManager();
        final int startLevel = batteryManager.getBatteryLevel();

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Double wattTotal = 0d;
                            Double ampeTotal = 0d;
                            Double voltTotal = 0d;

                            String resultStr = String.valueOf(timeOut) + " seconds\n";
                            if (watts.size() == timeOut) {
                                timer.cancel();
                                timer.purge();
                                for (int i = 0; i < watts.size(); i++) {
                                    wattTotal += watts.get(i);
                                    ampeTotal += ampes.get(i);
                                    voltTotal += volts.get(i);
                                    resultStr += watts.get(i).toString() + "W "
                                            + ampes.get(i).toString() + "µA "
                                            + volts.get(i).toString() + "µV\n";
                                }
                                Log.e(TAG, wattTotal + "W " + ampeTotal + "µA " + voltTotal + "µV");
                                resultStr += wattTotal + "W " + ampeTotal + "µA " + voltTotal + "µV\n";

                                resultStr += ampeTotal / timeOut + "µA ";
                                resultStr += startLevel - batteryManager.getBatteryLevel() + "%\n";

                                String fileStr = CommonUtil.readFromFile(FILE_NAME + timeOut + FILE_EXTENSION);
                                CommonUtil.write(FILE_NAME + timeOut + FILE_EXTENSION, fileStr + resultStr);
                            } else {
                                EnergyEstimator.readBatteryStatus();
                                Double watt = EnergyEstimator.getWattBattery();
                                Double ampe = EnergyEstimator.getAmpeBattery() * 1.0;
                                Double voltage = EnergyEstimator.getVoltBattery() * 1.0;
                                Log.e(TAG, watt + " " + ampe / 1000000 + " " + voltage / 1000000);
                                watts.add(watt);
                                ampes.add(ampe);
                                volts.add(voltage);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);


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
                            Log.e(TAG, watt + " " + ampe / 1000000 + " " + voltage / 1000000);
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
            int level = intent.getIntExtra(LEVEL, 0);
            Log.e(TAG, "Battery level: " + String.valueOf(level));

            Double wattTotal = 0d;
            Double ampeTotal = 0d;
            Double voltTotal = 0d;
            String resultStr = String.valueOf(level) + "\n";

            if (mPercentCount == 0)
                timer.schedule(timerTask, 0, 1000);
            else if (mPercentCount == mPercent) {
                timer.cancel();
                timer.purge();
                if (watts.size() != 0) {
                    for (int i = 0; i < watts.size(); i++) {
                        wattTotal += watts.get(i);
                        ampeTotal += ampes.get(i);
                        voltTotal += volts.get(i);
                        resultStr += watts.get(i).toString() + "W "
                                + ampes.get(i).toString() + "µA "
                                + volts.get(i).toString() + "µV\n";
                    }
                    Log.e(TAG, wattTotal + "W " + ampeTotal + "µA " + voltTotal + "µV");
                    resultStr += wattTotal + "W " + ampeTotal + "µA " + voltTotal + "µV\n";
                    String fileStr = CommonUtil.readFromFile(FILE_NAME + mPercent + FILE_EXTENSION);
                    CommonUtil.write(FILE_NAME + mPercent + FILE_EXTENSION, fileStr + resultStr);
                }
            } else {
                if (watts.size() != 0) {
                    for (int i = 0; i < watts.size(); i++) {
                        wattTotal += watts.get(i);
                        ampeTotal += ampes.get(i);
                        voltTotal += volts.get(i);
                        resultStr += watts.get(i).toString() + "W "
                                + ampes.get(i).toString() + "µA "
                                + volts.get(i).toString() + "µV\n";
                    }
                    Log.e(TAG, wattTotal + "W " + ampeTotal + "µA " + voltTotal + "V");
                    resultStr += wattTotal + "W " + ampeTotal + "µA " + voltTotal + "µV\n";
                    String fileStr = CommonUtil.readFromFile(FILE_NAME + mPercent + FILE_EXTENSION);
                    CommonUtil.write(FILE_NAME + mPercent + FILE_EXTENSION, fileStr + resultStr);
                }
            }
            watts.clear();
            ampes.clear();
            volts.clear();
            mPercentCount++;
        }
    };

    public void startCollectDataByPercent() {
        this.mContext.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }


}
