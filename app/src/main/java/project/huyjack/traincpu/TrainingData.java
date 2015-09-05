package project.huyjack.traincpu;

/**
 * Created by AnhHuy on 5/19/2015.
 */
public class TrainingData
{
    private String Frequency;
    private int CPUUsage;
    private double Power;

    public double [][] component;
    public double[] power;

    public TrainingData(int sizeX, int sizeY){
        component = new double[sizeX][sizeY];
        power = new double[sizeX];
    }


}
