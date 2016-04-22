package fluid;

import rigids.OccupiedCell;
import rigids.RigidBodies;
import rigids.RigidBody;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * A two dimensional vector field. That is, R^2->R^2
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
            double visc, double dt, StaticObjectsField so, RigidBodies rb) {
        double[][] u = this.u;
        double[][] v = this.v;
        double[][] temp;
        double[][] rigidSourceX = convertToRigidSourceX(rb,u);
        double[][] rigidSourceY = convertToRigidSourceY(rb,v);
        STEPS.addSource(u, dt, u0, rigidSourceX);
        STEPS.addSource(v, dt, v0, rigidSourceY);
        assert inv(u);
        assert inv(v);
        temp = u0;
        u0 = u;
        u = temp;
        STEPS.diffuse(1, u, u0, visc, dt, so, rb);
        assert inv(u);
        assert inv(v);
        temp = v0;
        v0 = v;
        v = temp;
        STEPS.diffuse(2, v, v0, visc, dt, so, rb);
        
        assert inv(u);
        assert inv(v);
        STEPS.project(u, v, u0, v0, so, rb);
        
        assert inv(u);
        assert inv(v);
        temp = u0;
        u0 = u;
        u = temp;
        temp = v0;
        v0 = v;
        v = temp;
        STEPS.advect(1, u, u0, u0, v0, dt, so, rb);
        assert inv(u);
        assert inv(v);
        STEPS.advect(2, v, v0, u0, v0, dt, so, rb);
        
        assert inv(u);
        assert inv(v);
        STEPS.project(u, v, u0, v0, so, rb);//*/
        
        assert inv(u);
        assert inv(v);
        this.u = u;
        this.v = v;
    }

    private boolean inv(double[][] u1) {
        for (int i = 0; i <= N+1; i++) {
            for (int j = 0; j<= N+1; j++) {
                assert !Double.isNaN(u1[i][j]);
            }
        }
        return true;
    }

    private double[][] convertToRigidSourceX(RigidBodies rb, double[][] u) {
        double[][] force = new double[N+2][N+2];
        for(RigidBody b:rb.bodies)
        {
            for(OccupiedCell c:b.getOccupiedCells())
            {
                force[c.i][c.j] = (c.vx-u[c.i][c.j]);
            }
        }
        return force;
    }

    private double[][] convertToRigidSourceY(RigidBodies rb,double[][] v) {
        double[][] force = new double[N+2][N+2];
        for(RigidBody b:rb.bodies)
        {
            for(OccupiedCell c:b.getOccupiedCells())
            {
                force[c.i][c.j] = (c.vy-v[c.i][c.j]);
            }
        }
        return force;
    }

}
