package project.huyjack.traincpu.manager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

import project.huyjack.traincpu.R;
import project.huyjack.traincpu.screenoff.ScreenOffAdminReceiver;

/**
 * Created by AnhHuy on 6/2/2015.
 */
public class ScreenManager {

    private static String TAG;

    public int getScreenBright(Context context){
        int brightness = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
        return brightness;
        }

    public void setScreenBrightRandom(Window window){

        Random random = new Random() ;
        int brightness = random.nextInt(255) + 1;

        //preview brightness changes at this window
        //get the current window attributes
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //set the brightness of this window
        layoutParams.screenBrightness = brightness / (float)255;
        //apply attribute changes to this window
        window.setAttributes(layoutParams);
    }

    /**
     * Turns the screen off and locks the device, provided that proper rights
     * are given.
     *
     * @param context
     *            - The application context
     */
    public static void turnScreenOff(final Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        policyManager.lockNow();
        if (admin) {
            TAG = ScreenManager.class.getName();
            Log.i(TAG, "Going to sleep now.");
            policyManager.lockNow();
        } else {
            Log.i(TAG, "Not an admin");
            Toast.makeText(context, R.string.device_admin_not_enabled,
                    Toast.LENGTH_LONG).show();
        }
    }
}
