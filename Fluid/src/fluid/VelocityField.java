package fluid;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A two dimensional vector field.
 * That is, R^2->R^2
 *
 * @author Helmond
 */
public class VelocityField {

    public double[][] u;//x direction of the velocity
    public double[][] v;//y direction of the velocity
    public final int N;
    public final double h;

    public VelocityField(int size, boolean addBounds) {
        size = size + (addBounds ? 2 : 0);
        this.N = size - 2;
        this.h = 1d / this.N;
        this.u = new double[size][size];
        this.v = new double[size][size];
    }

    public void vel_step(double[][] u0, double[][] v0,
             double visc, double dt, StaticObjectsField so) {
        double[][] u = this.u;
        double[][] v = this.v;
        double[][] temp;
        STEPS.addSource( u, u0, dt);
        STEPS.addSource( v, v0, dt);
        temp=u0;u0=u;u=temp;
        STEPS.diffuse(1, u, u0, visc, dt,so);
        temp=v0;v0=v;v=temp;
        STEPS.diffuse(2, v, v0, visc, dt,so);
        STEPS.project(u, v, u0, v0,so);
        temp=u0;u0=u;u=temp;
        temp=v0;v0=v;v=temp;
        STEPS.advect(1, u, u0, u0, v0, dt,so);
        STEPS.advect(2, v, v0, u0, v0, dt,so);
        STEPS.project(u, v, u0, v0,so);//*/
        this.u=u;
        this.v=v;
    }

}
