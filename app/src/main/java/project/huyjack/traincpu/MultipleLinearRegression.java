package project.huyjack.traincpu;

import android.util.Log;

import java.util.ArrayList;

import Jama.Matrix;
import Jama.QRDecomposition;

public class MultipleLinearRegression {
    private final int N;        // number of 
    private final int p;        // number of dependent variables
    private final Matrix beta;  // regression coefficients
    private double SSE;         // sum of squared
    private double SST;         // sum of squared

    public static void main(String[] args) {
        double[][] x = {{1, 100 ,300, 100 },
				        {1, 99, 300, 200},
				        {1, 98, 300, 100},
				        {1, 95, 300, 200},
				        {1, 92, 300, 160},
				        {1, 94, 200, 100},
				        {1, 80, 300, 300},
				        {1, 33, 360, 160},
				        {1, 45, 300, 400},
				        {1, 66, 160, 200},
				        {1, 77, 260, 100},
				        {1, 35, 400, 400}};
        
        double[] y = { 1200, 1100, 1040, 1000, 980, 700, 800, 500, 400, 335, 530, 731};
        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);

        System.out.printf("Y = %.2f + %.2fX1 + %.2fX2 + %.2fX3 (R^2 = %.2f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.beta(3), regression.R2());
    }



    public MultipleLinearRegression(double[][] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("dimensions don't agree");
        N = y.length;
        p = x[0].length;

        Matrix X = new Matrix(x);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);


        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }
}

