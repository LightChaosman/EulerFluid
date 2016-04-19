package rigids;

import main.Simulation;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Helmond
 */
public class RigidBodies {

    private static final int ENTRIES_PER_BODY = 6;

    public final RigidBody[] bodies;
    public final double[] x0, xFinal;
    ODE solver = ODE.RK1;

    public RigidBodies() {
        this.bodies = new RigidBody[0];
        this.x0 = new double[0];
        this.xFinal = new double[0];
    }

    public RigidBodies(RigidBody[] bodies) {
        this.bodies = bodies;
        this.x0 = new double[this.bodies.length * ENTRIES_PER_BODY];
        this.xFinal = new double[this.bodies.length * ENTRIES_PER_BODY];

        BodiesToArray(this.bodies, this.xFinal);
    }

    public RigidBodies(List<RigidBody> l) {
        this(l.toArray(new RigidBody[l.size()]));
    }

    public void finalToX() {
        System.arraycopy(xFinal, 0, x0, 0, x0.length);
    }

    public void finalToBodies() {
        ArrayToBodies(bodies, xFinal);
    }

    public void solveODE(double t, double tplusdt, Simulation s) {
        solver.ode(x0, xFinal, ENTRIES_PER_BODY * bodies.length, t, tplusdt, new Derivative() {
            @Override
            public void dxdt(double t, double[] x, double[] xdot) {
                dxdt2(t, x, xdot, s);
            }
        });
    }

    /**
     * Computes dxdt and stores it in xdot
     *
     * @param t
     * @param x
     * @param xdot
     */
    private void dxdt2(double t, double[] x, double[] xdot, Simulation s) {
        ArrayToBodies(bodies, x);
            s.ComputeForceAndTorque(t);//TODO!!!
        for (int i = 0; i < bodies.length; i++) {
            DdtStateToArray(xdot, i * ENTRIES_PER_BODY, bodies[i]);
        }
    }

    private static double[] BodiesToArray(RigidBody[] bodies, double[] x) {
        assert x.length == bodies.length * ENTRIES_PER_BODY;
        for (int i = 0; i < bodies.length; i++) {
            RigidBody body = bodies[i];
            int offset = i * ENTRIES_PER_BODY;
            StateToArray(x, offset, body);
        }
        return x;
    }

    private static void StateToArray(double[] x, int offset, RigidBody body) {
        x[offset] = body.x;
        x[offset + 1] = body.y;
        x[offset + 2] = body.theta;
        x[offset + 3] = body.Px;
        x[offset + 4] = body.Py;
        x[offset + 5] = body.L;
    }

    private static void ArrayToBodies(RigidBody[] bodies, double[] stateVector) {
        assert stateVector.length == bodies.length * ENTRIES_PER_BODY;
        for (int i = 0; i < bodies.length; i++) {
            RigidBody body = bodies[i];
            int offset = i * ENTRIES_PER_BODY;
            ArrayToState(body, stateVector, offset);
        }
    }

    private static void ArrayToState(RigidBody rb, double[] stateVector, int offset) {
        rb.x = stateVector[offset];
        rb.y = stateVector[offset + 1];

        rb.theta = stateVector[offset + 2];
        rb.setAuxs();
        rb.Px = stateVector[offset + 3];
        rb.Py = stateVector[offset + 4];

        rb.L = stateVector[offset + 5];

        /*Compute auxiliary variables*/
    }

    private static void DdtStateToArray(double[] xdot, int offset, RigidBody body) {
        xdot[offset] = body.vx;
        xdot[offset + 1] = body.vy;
        //xdot[offset + 2] = -body.omega * body.Rxy;//TODO ddt of rotations! tempted to
        //xdot[offset + 3] = body.omega * body.Ryy;
        //xdot[offset + 4] = body.omega * -body.Rxx;
        //xdot[offset + 5] = body.omega * body.Ryx;
        xdot[offset + 2] = body.omega;//scale?
        xdot[offset + 3] = body.Fx;
        xdot[offset + 4] = body.Fy;
        xdot[offset + 5] = body.tau;
    }

    public void clearForces() {
        for(RigidBody r:bodies)
        {
            r.Fx = 0;
            r.Fy = 0;
            r.tau = 0;
        }
    }

}
