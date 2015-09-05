package project.huyjack.traincpu.Manager;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by AnhHuy on 6/2/2015.
 */
public class ScreenManager {

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
}
