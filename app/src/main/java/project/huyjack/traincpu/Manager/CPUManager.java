package project.huyjack.traincpu.Manager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class CPUManager {

    private static String CPU_SCALING_FREQ_AVAILABLE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static String ERROR = "error";

    public ArrayList<String> FREQUENCY = new ArrayList<String>();

    public CPUManager(){
        GetScalingFrequency();

    }

    private void GetScalingFrequency(){
        String cpuScalingFreq = new Common().ReadCommand(CPU_SCALING_FREQ_AVAILABLE);
        if (cpuScalingFreq != ERROR){
            int first = 0;
            for (int i = 0; i < cpuScalingFreq.length() ; i++){
                if (cpuScalingFreq.charAt(i) == ' '){
                    FREQUENCY.add(cpuScalingFreq.substring(first, i));
                    first = i + 1;
                }
            }
        }
    }

    public String GetCurrentFrequency(){
        Common cmn = new Common();
        return cmn.ReadCommand("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    }

    public void SetCPUFrequencyRandom(){
        Random random = new Random() ;
        int randomNumber = random.nextInt(FREQUENCY.size());
        Log.e("randCPU: ", randomNumber + "");
        this.SetCPUFrequency(FREQUENCY.get(randomNumber));

    }
    public void SetCPUFrequency(String freq){
        //Set CPU Govenor to userspace
        this.execCommands("echo userspace > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        this.execCommands("echo userspace > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor");
        //Set CPU Frequency
        this.execCommands("echo \"" + freq + "\" > /sys/devices/system/cpu/cpu0/cpufreq/scaling_setspeed");
        this.execCommands("echo \"" + freq + "\" > /sys/devices/system/cpu/cpu1/cpufreq/scaling_setspeed");
    }

    public void SetDefaultCPU(){
        this.execCommands("echo ondemand > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        this.execCommands("echo ondemand > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor");
    }
    /*
        * @return integer Array with 4 elements: user, system, idle and other cpu
*         usage in percentage.
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



    private Boolean execCommands(String command) {
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
