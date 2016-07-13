package fluid;

import rigids.RigidBodies;

/**
 * A Scalar field
 * That is, R^2->R
 *
 * @author Helmond
 */
public class DensityField {

    public double[][] field;
    public final double[][] permsources;
    private final int N;
    private final double h;
    public double mass;

    public DensityField(int size, boolean addBounds) {
        size = size + (addBounds ? 2 : 0);
        this.N = size - 2;
        this.h = 1d / this.N;
        this.field = new double[size][size];
        this.permsources = new double[size][size];
    }
    

   

    public void dens_step(double[][] source, double[][] u, double[][] v, double diff,
            double dt, StaticObjectsField so, boolean renormalize,RigidBodies rb) {
        double[][] x = field;
        double[][] x0 = source;//readability
        double[][] temp;
        STEPS.addSource(x,dt, x0,permsources);
        mass = 0;
        for(int i = 1; i <= N; i++)for(int j = 1; j <= N;j++)mass+=field[i][j]*(so.ocs[i][j]==so.E?1:0);
        
        temp = x0;
        x0 = x;
        x = temp;
        STEPS.diffuse(3, x, x0, diff, dt,so,rb);
        temp = x0;
        x0 = x;
        x = temp;
        STEPS.advect(3, x, x0, u, v, dt,so,rb);
        if(renormalize && mass>0 )
        {
            double s = 0;
            for(int i = 1; i <= N; i++)for(int j = 1; j <= N;j++)s+=x[i][j]*(so.ocs[i][j]==so.E?1:0);
            double fact = (mass)/(s);
            if(s>0){
            for(int i = 1; i <= N; i++)for(int j = 1; j <= N;j++)x[i][j]=x[i][j]*fact;
            STEPS.set_bnd(3, x, so,rb);}
        }
        field = x;
    }
    
    public void incSource(int i, int j)
    {
        permsources[i][j] = Math.min(100, permsources[i][j]+1);
    }
    public void decSource(int i, int j)
    {
        permsources[i][j] = Math.max(0, permsources[i][j]-1);
    }
 
    
}
