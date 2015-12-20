package project.huyjack.traincpu.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

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


    public Double calculateTotalWattFromFile(String fileName) {
        List<String> arrInput = new LinkedList<String>();
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                arrInput.add(line);
            }
            if (arrInput.size() == 0) {
                return null;
            }
            arrInput.remove(arrInput.size()-1);
            arrInput.remove(arrInput.size()-1);
            br.close();
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (IndexOutOfBoundsException ie) {

        }
        Double totalWatt = 0d;

        LinkedList<Double> wattsList = new LinkedList<Double>();
        LinkedList<Double> ampesList = new LinkedList<Double>();
        LinkedList<Double> voltList = new LinkedList<Double>();

        for (String element : arrInput) {
            char[] arrChar = new char[50];
            char[] arrElement = element.toCharArray();
            int indexArrChar = 0;
            int indexArrElement = 0;
            for (char c : arrElement) {
                int val = c - '0';
                if ((val >= 0 && val < 10) || c == '.' || c == 'E') {
                    arrChar[indexArrChar] = c;
                    indexArrChar++;
                }
                if (c == 'W') {
                    wattsList.add(Double.parseDouble(String.valueOf(arrChar)));
                    arrChar = new char[50];
                }
                if (c == 'µ') {
                    if (arrElement[indexArrElement+1]=='A') {
                        ampesList.add(Double.parseDouble(String.valueOf(arrChar)));
                    }
                    else if (arrElement[indexArrElement+1]=='V') {
                        voltList.add(Double.parseDouble(String.valueOf(arrChar)));
                    }
                    arrChar = new char[50];
                }
                if (c == '%') {
                    arrChar = new char[50];
                }
                indexArrElement++;
            }
        }
        Double max = Double.MIN_VALUE;
        Double sumOfAmpe = 0d;
        for (int i = 0 ; i < ampesList.size() ; i++) {
            max = Math.max(voltList.get(i), max);
            sumOfAmpe += ampesList.get(i);
            if (i % 10 == 9) {
                totalWatt += max * sumOfAmpe;
                max = Double.MIN_VALUE;
                sumOfAmpe = 0d;
            }
        }

        return totalWatt;
    }
}
