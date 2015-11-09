package project.huyjack.traincpu.manager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import project.huyjack.traincpu.common.CommonUtil;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class CPUManager {
    private static final String TAG = CPUManager.class.getName();

    private static final String CPU_CURRENT_FREQUENCY = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    private static final String CPU0_CPUFREQ_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    private static final String CPU1_CPUFREQ_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_governor";
    private static final String CPU2_CPUFREQ_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_governor";
    private static final String CPU3_CPUFREQ_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_governor";
    private static final String CPU0_CPUFREQ_CPUINFO_MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
    private static final String CPU1_CPUFREQ_CPUINFO_MIN_FREQ = "/sys/devices/system/cpu/cpu1/cpufreq/cpuinfo_min_freq";
    private static final String CPU2_CPUFREQ_CPUINFO_MIN_FREQ = "/sys/devices/system/cpu/cpu2/cpufreq/cpuinfo_min_freq";
    private static final String CPU3_CPUFREQ_CPUINFO_MIN_FREQ = "/sys/devices/system/cpu/cpu3/cpufreq/cpuinfo_min_freq";
    private static final String CPU0_CPUFREQ_SCALING_SETSPEED = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed";
    private static final String CPU1_CPUFREQ_SCALING_SETSPEED = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_setspeed";
    private static final String CPU2_CPUFREQ_SCALING_SETSPEED = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_setspeed";
    private static final String CPU3_CPUFREQ_SCALING_SETSPEED = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_setspeed";
    private static final String GOV_USERSPACE = "userspace";
    private static final String GOV_PERFORMANCE = "performance";
    private static final String CPU_SCALING_FREQ_AVAILABLE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String ERROR = "error";

    private ArrayList<String> FREQUENCY = new ArrayList<String>();


    private CommonUtil commonUtil = null;
    public CPUManager() {
        initScalingFrequency();
        commonUtil = new CommonUtil();
    }

    private void initScalingFrequency() {
        String cpuScalingFreq = new CommonUtil().readCommand(CPU_SCALING_FREQ_AVAILABLE);
        if (cpuScalingFreq != ERROR) {
            int first = 0;
            for (int i = 0; i < cpuScalingFreq.length(); i++) {
                if (cpuScalingFreq.charAt(i) == ' ') {
                    FREQUENCY.add(cpuScalingFreq.substring(first, i));
                    first = i + 1;
                }
            }
        }
    }

    public String GetCurrentFrequency() {
        CommonUtil cmn = new CommonUtil();
        return cmn.readCommand(CPU_CURRENT_FREQUENCY);
    }

    public void SetCPUFrequencyRandom() {
        Random random = new Random();
        int randomNumber = random.nextInt(FREQUENCY.size());
        Log.e(TAG, "random freq:" + FREQUENCY.get(randomNumber));
        this.SetCPUFrequency(FREQUENCY.get(randomNumber));

    }

    public void SetCPUFrequency(String freq) {
        //Set CPU Govenor to userspace
        commonUtil.execCommand("echo " + GOV_USERSPACE + " > " + CPU0_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_USERSPACE + " > " + CPU1_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_USERSPACE + " > " + CPU2_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_USERSPACE + " > " + CPU3_CPUFREQ_SCALING_GOVERNOR);
        //Set CPU Frequency
        //cpuinfo_min_freq
        commonUtil.execCommand("echo \"" + 300000 + "\" > " + CPU0_CPUFREQ_CPUINFO_MIN_FREQ);
        commonUtil.execCommand("echo \"" + 300000 + "\" > " + CPU1_CPUFREQ_CPUINFO_MIN_FREQ);
        commonUtil.execCommand("echo \"" + 300000 + "\" > " + CPU2_CPUFREQ_CPUINFO_MIN_FREQ);
        commonUtil.execCommand("echo \"" + 300000 + "\" > " + CPU3_CPUFREQ_CPUINFO_MIN_FREQ);


        commonUtil.execCommand("echo \"" + freq + "\" > " + CPU0_CPUFREQ_SCALING_SETSPEED);
        commonUtil.execCommand("echo \"" + freq + "\" > " + CPU1_CPUFREQ_SCALING_SETSPEED);
        commonUtil.execCommand("echo \"" + freq + "\" > " + CPU2_CPUFREQ_SCALING_SETSPEED);
        commonUtil.execCommand("echo \"" + freq + "\" > " + CPU3_CPUFREQ_SCALING_SETSPEED);
    }

    public void SetDefaultCPU() {
        commonUtil.execCommand("echo " + GOV_PERFORMANCE + " > " + CPU0_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_PERFORMANCE + " > " + CPU1_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_PERFORMANCE + " > " + CPU2_CPUFREQ_SCALING_GOVERNOR);
        commonUtil.execCommand("echo " + GOV_PERFORMANCE + " > " + CPU3_CPUFREQ_SCALING_GOVERNOR);
    }

    /*
     * @return integer Array with 4 elements: user, system, idle and other cpu
     * usage in percentage.
     */
    public int[] GetCpuUsageStatistic() {

        String tempString = executeTop();

        tempString = tempString.replaceAll(",", "");
        tempString = tempString.replaceAll("User", "");
        tempString = tempString.replaceAll("System", "");
        tempString = tempString.replaceAll("IOW", "");
        tempString = tempString.replaceAll("IRQ", "");
        tempString = tempString.replaceAll("%", "");
        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }
        tempString = tempString.trim();
        String[] myString = tempString.split(" ");
        int[] cpuUsageAsInt = new int[myString.length];
        for (int i = 0; i < myString.length; i++) {
            myString[i] = myString[i].trim();
            cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
        }
        return cpuUsageAsInt;
    }


    private String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }
}
