package project.huyjack.traincpu.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.common.CommonUtil;
import project.huyjack.traincpu.EnergyEstimator;
import project.huyjack.traincpu.manager.BatteryManager;
import project.huyjack.traincpu.manager.ScreenManager;

/**
 * Created by huyjack on 10/6/15.
 */
public class DataCollector {

    private static final String TAG = DataCollector.class.getName();
    private static final String FILE_NAME = "data";
    private static final String FILE_SEP = "_";
    private static final String FILE_EXTENSION = ".txt";
    private static final String LEVEL = "level";


    private final int mPercent = 0;
    private final int mStartPercent = 0;

    private long mCurrentTime = 0;
    private Context mContext = null;
    private int mPercentCount = -1;
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
            int temp = intent.getIntExtra(android.os.BatteryManager.EXTRA_TEMPERATURE, 0) / 10;

            Log.e(TAG, "Battery level: " + String.valueOf(level));

            Double wattTotal = 0d;
            Double ampeTotal = 0d;
            Double voltTotal = 0d;
            String resultStr = "";
            String fileName = FILE_NAME + FILE_SEP + mPercent + FILE_SEP + mCurrentTime + FILE_EXTENSION;

            resultStr += String.valueOf(level) + "\n";


            if (mPercentCount == 0) {
                if (level == mStartPercent) {
                    timer.schedule(timerTask, 0, 1000);
                    mPercentCount++;
                }

            } else if (mPercentCount == mPercent) {
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
                    resultStr += ampeTotal / watts.size() + "µA " + temp + " *C\n";
                    String fileStr = CommonUtil.readFromFile(fileName);
                    CommonUtil.write(fileName, fileStr + resultStr);
                }
                mPercentCount++;
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
                    String fileStr = CommonUtil.readFromFile(fileName);
                    CommonUtil.write(fileName, fileStr + resultStr);
                }
                mPercentCount++;
            }
            watts.clear();
            ampes.clear();
            volts.clear();

        }


    };

    public DataCollector() {
    }

    public void startCollectData(final int timeOut, final Context context) {
        //Init list data
        final List<Double> watts = new LinkedList<Double>();
        final List<Double> ampes = new LinkedList<Double>();
        final List<Double> volts = new LinkedList<Double>();

        //Wake lock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        final PowerManager.WakeLock partialWl = pm.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK), "TAG");

        //Notification sound
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(context, notification);
        final BatteryManager batteryManager = new BatteryManager();
        final int startLevel = batteryManager.getBatteryLevel();
        final long startTime = System.currentTimeMillis() / 1000;
        final String fileName =
                FILE_NAME + FILE_SEP
                + timeOut + FILE_SEP
                + startLevel + FILE_SEP
                + startTime + FILE_EXTENSION;

        final android.os.Handler handler = new android.os.Handler();
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
                            String resultStr = "";

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
                                String fileStr = CommonUtil.readFromFile(fileName);
                                CommonUtil.write(fileName, fileStr + resultStr);
                                wakeLock.acquire();
                                r.play();
                            } else {
                                if (watts.size() % 10 == 1) {
                                    partialWl.acquire();
                                    ScreenManager.turnScreenOff(context);
                                    if (wakeLock.isHeld())
                                        wakeLock.release();
                                } else if (watts.size() % 10 == 5) {
                                    if (partialWl.isHeld())
                                        partialWl.release();
                                    wakeLock.acquire();
                                }
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

    public void startCollectDataByPercent() {
        this.mCurrentTime = System.currentTimeMillis();
        this.mPercentCount = -1;
        this.mContext.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


}
