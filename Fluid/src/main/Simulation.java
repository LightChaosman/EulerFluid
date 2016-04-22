package main;

import fluid.VelocityField;
import fluid.DensityField;
import fluid.STEPS;
import fluid.StaticObjectsField;
import java.util.ArrayList;
import rigids.OccupiedCell;
import rigids.forces.Force;
import rigids.forces.LinearDragForce;
import rigids.forces.MouseSpring;
import rigids.RigidBodies;
import rigids.RigidBody;
import rigids.forces.QuadraticDragForce;

/**
 * Preconditions of internal functions will be enforced using "assert" commands.
 * These commands can be made very inefficient to check 'heavy' preconditions,
 * and can be disabled once we are certain our code is correct. These will thus
 * have no impact on performance of the final model!
 *
 * @author Helmond
 */
public class Simulation {

    public final VelocityField u; // The velocity field of our incompressable fluid
    public final DensityField rho; //The density field (particles floating in the fluid)
    public final StaticObjectsField so;
    public final RigidBodies rbodies;
    public final ArrayList<Force> forces;
    public final int N;//The ammount of gridcells in 1D, that is, we have N*N internal gridcels, (N+2)^2 counting the boundary
    private final double h;//Thegridspacing : 1/N -> our gridsides have dimension 1
    private final double dt;//The timestep
    private final double diff; //The diffusion rate
    private final double visc;//The viscosity of the fluid
    private final double epsilon;//The vorticity confinement parameter
    public boolean norm = false;

    public Simulation(int N, double dt, double diff, double visc, double epsilon, RigidBodies bodies) {//indices to be used: [1,N], not [0,N-1]!
        this.u = new VelocityField(N, true);
        this.rho = new DensityField(N, true);
        this.so = new StaticObjectsField(N, true);
        this.N = N;
        this.h = 1d / N;
        this.dt = dt;
        this.visc = visc;
        this.diff = diff;
        this.epsilon = epsilon;
        this.rbodies = bodies;
        forces = new ArrayList<>();
        forces.add(new MouseSpring());
        for (RigidBody b : bodies.bodies) {
            forces.add(new QuadraticDragForce(b, .01));
        }
        rbodies.findCells(N);
    }

    public void step(double[][] rhoInput, double[][] uInput, double[][] vInput) {
        System.out.println("a");
        rbodies.finalToX();
        System.out.println("b");
        rbodies.solveODE(0, dt, this);
        System.out.println("c");
        rho.dens_step(rhoInput, u.u, u.v, diff, dt, so, norm, rbodies);
        System.out.println("d");
        STEPS.computeVorticityForce(uInput, vInput, u.u, u.v, epsilon);
        System.out.println("e");
        u.vel_step(uInput, vInput, visc, dt, so, rbodies);
        System.out.println("f");
        rbodies.finalToBodies();
        System.out.println("g");
        rbodies.findCells(N);

    }

    void addStaticBlock(int x, int y) {
        so.addCell(x, y);
    }

    public void ComputeForceAndTorque(double t) {
        rbodies.clearForces();
        for (Force f : forces) {
            f.addForces();
        }
        for (RigidBody b : rbodies.bodies) {
            for (OccupiedCell c : b.getOutsideCells()) {
                if (b.getOccupiedCells().contains(c)) {
                    continue;
                }

                double intx = ((double) c.i) / N;
                double inty = ((double) c.j) / N;

                double vx = u.u[c.i][c.j];
                double vy = u.v[c.i][c.j];
                double Fix = (vx - c.vx) / N / N;//Normalise to N^2 -> bigger resolution should not result in a greater force
                double Fiy = (vy - c.vy) / N / N;
                b.Fx += Fix;
                b.Fy += Fiy;

                double rix = intx - b.x;
                double riy = inty - b.y;
                b.tau += (rix * Fiy - riy * Fix);
//TODO
            }
        }
    }

}
