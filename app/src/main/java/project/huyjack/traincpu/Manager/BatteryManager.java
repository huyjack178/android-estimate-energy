package project.huyjack.traincpu.manager;

import android.content.Context;

import project.huyjack.traincpu.common.CommonUtil;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class BatteryManager {
    private static final String BATTERY_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    private static final String BATTERY_VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";
    private static final String BATTERY_LEVEL = "/sys/class/power_supply/battery/capacity";
    public static final String POWER_PROFILE = "com.android.internal.os.PowerProfile";
    public static final String BATTERY_CAPACITY = "battery.capacity";
    public static final String GET_AVERAGE_POWER_METHOD = "getAveragePower";


    public int getBatteryLevel() {
        return Integer.parseInt(new CommonUtil().readCommand(BATTERY_LEVEL));
    }

    public int getBatteryCurrent() {
        return Integer.parseInt(new CommonUtil().readCommand(BATTERY_CURRENT_NOW));
    }

    public int getBatteryVoltage() {
        return Integer.parseInt(new CommonUtil().readCommand(BATTERY_VOLTAGE_NOW));
    }

    public double getBatteryCapacity(Context ct) {


        Object mPowerProfile_ = null;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = POWER_PROFILE;

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(android.content.Context.class).newInstance(ct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod(GET_AVERAGE_POWER_METHOD, java.lang.String.class)
                    .invoke(mPowerProfile_, BATTERY_CAPACITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }
}
