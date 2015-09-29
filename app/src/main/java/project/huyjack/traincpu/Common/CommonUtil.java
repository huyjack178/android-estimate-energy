package project.huyjack.traincpu.Common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
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

    public static void write(String fileName, String data) {
        Writer writer;
        File outDir = new File("sdcard" + File.separator);
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory EZ_time_tracker. Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            Log.e(TAG, outputFile.getAbsolutePath());
            writer.write(data);
            writer.close();

        } catch (IOException e) {
            Log.w("eztt", e.getMessage(), e);
        }

    }

    public static String readFromFile(String fileName) {
        String ret = "";
        try {
            File file = new File("sdcard/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                ret += line + "\n";
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
