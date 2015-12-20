package project.huyjack.traincpu;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class TrainData
{
    private double Frequency;
    private int CPUUsage;
    private int ScreenBright;
    private double Power;

    public int getScreenBright() {
        return ScreenBright;
    }

    public void setScreenBright(int screenBright) {
        ScreenBright = screenBright;
    }

    public double getFrequency() {
        return Frequency;
    }

    public void setFrequency(double frequency) {
        Frequency = frequency;
    }

    public int getCPUUsage() {
        return CPUUsage;
    }

    public void setCPUUsage(int CPUUsage) {
        this.CPUUsage = CPUUsage;
    }

    public double getPower() {
        return Power;
    }

    public void setPower(double power) {
        Power = power;
    }

    public double [][] component;
    public double[] power;

    public TrainData(){

    }

    public TrainData(int sizeX, int sizeY){
        component = new double[sizeX][sizeY];
        power = new double[sizeX];
    }


}
