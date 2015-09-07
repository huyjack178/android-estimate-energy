package project.huyjack.traincpu.Common;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class CommonUtil {

    public static final String TAG = CommonUtil.class.getName();

    public String readCommand(String cmd) {
        try {
            RandomAccessFile reader = new RandomAccessFile(cmd, "r");
            return reader.readLine();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "error";
        }
    }


    public Boolean execCommand(String command) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(command + "\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public void writeToFile(String data, String fileName) {
        try {

            File myDir = new File("storage/sdcard1/");
            myDir.mkdirs();
            FileWriter out = new FileWriter(new File(myDir, fileName), true);

            out.write(data);

            out.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    public ArrayList<String> readFromFile(String fileName) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File("storage/sdcard1/" + fileName);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line + "\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public void deleteFile() {
        File file = new File("storage/sdcard1/data.txt");
        boolean deleted = file.delete();
    }
}
