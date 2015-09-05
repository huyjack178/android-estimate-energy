package project.huyjack.traincpu;

/**
 * Created by AnhHuy on 6/9/2015.
 */
public class Experiment {
    public double EstimateTotalPower(double [][] component){
        double [] model = new Training(0).GetModel();
        double totalPower = 0;
        for (int i = 0; i < component.length ; i++){
            totalPower += model[0] + model[1]*component[i][1] + model[2]*component[i][2];
        }

        return totalPower;
    }

}
