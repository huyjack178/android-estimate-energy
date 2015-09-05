package project.huyjack.traincpu.Manager;

import android.content.Context;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class BatteryManager {
    private static String BATTERY_LEVEL = "/sys/class/power_supply/battery/capacity";

    private double BATTERY_CAP;




    public int getBatteryLevel() {
        return  Integer.parseInt(new Common().ReadCommand(BATTERY_LEVEL));
    }

    public int getBatteryCurrent(){
        return Integer.parseInt(new Common().ReadCommand("/sys/class/power_supply/battery/current_now"));
    }

    public int getBatteryVoltage(){
        return Integer.parseInt(new Common().ReadCommand("/sys/class/power_supply/battery/voltage_now"));
    }

    public double getBatteryCapacity(Context ct) {
        Object mPowerProfile_ = null;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(android.content.Context.class).newInstance(ct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }


    public double getBATTERY_CAP() {
        return BATTERY_CAP;
    }
}
