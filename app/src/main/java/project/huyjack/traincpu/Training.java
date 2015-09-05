package project.huyjack.traincpu;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import project.huyjack.traincpu.Manager.BatteryManager;
import project.huyjack.traincpu.Manager.CPUManager;
import project.huyjack.traincpu.Manager.Common;
import project.huyjack.traincpu.Manager.ScreenManager;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class Training implements  Runnable{
    private int TIMER_COUNT = 600000;
    private static String FileName = "data.txt";

    public Training(int timeOut){
        TIMER_COUNT  = timeOut;
    }


    public void RunAnalysis() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            CPUManager cpuManager = new CPUManager();
                            Common cmn  = new Common();
                            BatteryManager batteryManager = new BatteryManager();
                            ScreenManager screenManager = new ScreenManager();
//
                            //Write CPU Frequency
                            cpuManager.SetCPUFrequencyRandom();
                            cmn.writeToFile(Double.parseDouble(cpuManager.GetCurrentFrequency())/1000 + ",", FileName);
                            //txtContent.append(Double.parseDouble(cpuManager.GetCurrentFrequency())/1000 + ",");

                            //Write CPU %
                            int[] cpuUsage = cpuManager.GetCpuUsageStatistic();
                             int cpuTotal = cpuUsage[0] + cpuUsage[1] + cpuUsage[2] + cpuUsage[3];
                            cmn.writeToFile(cpuTotal + ",", FileName);
                            //txtContent.append(cpuTotal + ",");
                            //Write Screen Brightness
                           // screenManager.setScreenBrightRandom( window);
                            //cmn.writeToFile(screenManager.getScreenBright(context) + ",");

                            //Write Power
                            int current = batteryManager.getBatteryCurrent();
                            int vol = batteryManager.getBatteryVoltage();
                            double watt = ((current/1000)*(vol/1000))/1000/10;
                            cmn.writeToFile(watt +"\n", FileName);
                            //txtContent.append(watt +"\n");

                            //}
                        } catch (Exception e) {
                            Log.e("error" , e.toString());
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask  , 0, 1000); //execute in every 1000 ms
    }

    public void RunTest() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            CPUManager cpuManager = new CPUManager();
                            Common cmn = new Common();
                            BatteryManager batteryManager = new BatteryManager();
                            ScreenManager screenManager = new ScreenManager();

                            //Write CPU Frequency
                            cmn.writeToFile(Double.parseDouble(cpuManager.GetCurrentFrequency()) / 1000 + ",", FileName);

                            //Write CPU %
                            int[] cpuUsage = cpuManager.GetCpuUsageStatistic();
                            int cpuTotal = cpuUsage[0] + cpuUsage[1] + cpuUsage[2] + cpuUsage[3];
                            cmn.writeToFile(cpuTotal + ",", FileName);

                            //Write Power
                            int current = batteryManager.getBatteryCurrent();
                            int vol = batteryManager.getBatteryVoltage();
                            double watt = ((current / 1000) * (vol / 1000)) / 1000 / 10;
                            cmn.writeToFile(watt + "\n", FileName);

                        } catch (Exception e) {
                            Log.e("error", e.toString());
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 1000 ms
    }

    public TrainingData RunTest1(int time) {
        TrainingData trainingData = new TrainingData(time, 3);
        int i = 0;
        while (trainingData.component.length <= time){
            try {
                CPUManager cpuManager = new CPUManager();
                Common cmn = new Common();
                BatteryManager batteryManager = new BatteryManager();
                ScreenManager screenManager = new ScreenManager();

                //Write CPU Frequency
                //cmn.writeToFile(Double.parseDouble(cpuManager.GetCurrentFrequency()) / 1000 + ",", FileName);
                trainingData.component[i][0] = Double.parseDouble(cpuManager.GetCurrentFrequency()) / 1000 ;
                //Write CPU %
                int[] cpuUsage = cpuManager.GetCpuUsageStatistic();
                int cpuTotal = cpuUsage[0] + cpuUsage[1] + cpuUsage[2] + cpuUsage[3];
                //cmn.writeToFile(cpuTotal + ",", FileName);
                trainingData.component[i][0] = cpuTotal;
                //Write Power
                int current = batteryManager.getBatteryCurrent();
                int vol = batteryManager.getBatteryVoltage();
                double watt = ((current / 1000) * (vol / 1000)) / 1000 / 10;
                //cmn.writeToFile(watt + "\n", FileName);

            } catch (Exception e) {
                Log.e("error", e.toString());
                // TODO Auto-generated catch block
            }
        }


       return trainingData;
    }

    public ArrayList<String> GetArrayFromTxt(String txt){
        ArrayList<String> arr = new ArrayList<String>();
        int firstIndex = 0;
        for (int i = 0; i < txt.length() ;i++){
            if (txt.charAt(i) == '\n'){
                arr.add(txt.substring(firstIndex, i) + '\n');
                firstIndex = i + 1;
            }
        }
        return  arr;
    }


    public TrainingData GetData(String fileName){

        Common cmn = new Common();
        ArrayList<String> strLst =  cmn.readFromFile(fileName);
            TrainingData data = new TrainingData(strLst.size() - 1, 3);

            for (int i = 0; i < strLst.size() - 1 ; i++) {
                String str = strLst.get(i);
                //Assign first element
                data.component[i][0] = 1;

                int countItem = 1;
                int firstIndex = 0;

                for (int j = 0; j < str.length(); j++) {
                    if (str.charAt(j) == ',') {
                        data.component[i][countItem] = Double.parseDouble(str.substring(firstIndex, j));
                        countItem++;
                        firstIndex = j + 1;
                    } else if (str.charAt(j) == '\n') {
                        data.power[i] = Double.parseDouble(str.substring(firstIndex, j));
                    }
                }
            }
            return data;
    }

    public double[] GetModel(){
        double[] model = new double[3];
        Common cmn = new Common();
        ArrayList<String> strLst =  cmn.readFromFile("model.txt");
        for (int i = 0 ; i < strLst.size() ; i++){
            String str = strLst.get(i);
            int firstIndex = 0;
            int count = 0;
            for (int j = 0 ; j < str.length() ; j++){
                if (str.charAt(j) == ',') {
                    model[count] = Double.parseDouble(str.substring(firstIndex, j));
                    count++;
                    firstIndex = j + 1;
                }
                else if (str.charAt(j) == '\n') {
                    model[count] = Double.parseDouble(str.substring(firstIndex, j));
                }
            }
        }
        return model;
    }

    public ArrayList<TrainingData> Run(Context context){
        ArrayList<TrainingData> data = new ArrayList<TrainingData>();
        final CPUManager cpuManager = new CPUManager();

        BatteryManager batteryManager = new BatteryManager();
        double batteryCapacity = batteryManager.getBatteryCapacity(context);

        for (int i = 0 ; i < cpuManager.FREQUENCY.size() ; i++){
            cpuManager.SetCPUFrequency(cpuManager.FREQUENCY.get(i));
            Common cmnMain = new Common();
            final ArrayList<Integer> cpuUseMain = new ArrayList<Integer>();
            int beforeBatteryLevel = batteryManager.getBatteryLevel();
            int afterBatteryLevel = 0;
            try {
                Thread.sleep(TIMER_COUNT);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            cmnMain.writeToFile(cpuManager.FREQUENCY.get(i) + ", ", FileName);

            int[] cpuUse = cpuManager.GetCpuUsageStatistic();
            int cpuTotal = cpuUse[0] + cpuUse[1] + cpuUse[2] + cpuUse[3];
            cmnMain.writeToFile(cpuTotal + ", ",FileName);

            afterBatteryLevel = batteryManager.getBatteryLevel();
            double power = (beforeBatteryLevel - afterBatteryLevel) * batteryCapacity / 100;

            cmnMain.writeToFile(power + "\n",FileName);
        }
        cpuManager.SetDefaultCPU();
        return data;
    }


    @Override
    public void run() {
        RunTest1(TIMER_COUNT);
    }
}
