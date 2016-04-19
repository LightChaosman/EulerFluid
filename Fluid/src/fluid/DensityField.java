package fluid;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A Scalar field
 * That is, R^2->R
 *
 * @author Helmond
 */
public class DensityField {

    public double[][] field;
    private final int N;
    private final double h;

    public DensityField(int size, boolean addBounds) {
        size = size + (addBounds ? 2 : 0);
        this.N = size - 2;
        this.h = 1d / this.N;
        this.field = new double[size][size];
    }
    

   

    public void dens_step(double[][] source, double[][] u, double[][] v, double diff,
            double dt, StaticObjectsField so) {
        double[][] x = field;
        double[][] x0 = source;//readability
        double[][] temp;
        STEPS.addSource(x, x0, dt);
        temp = x0;
        x0 = x;
        x = temp;
        STEPS.diffuse(0, x, x0, diff, dt,so);
        temp = x0;
        x0 = x;
        x = temp;
        STEPS.advect(0, x, x0, u, v, dt,so);
        field = x;
    }

    
}
