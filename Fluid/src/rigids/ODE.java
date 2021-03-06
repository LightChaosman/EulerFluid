package rigids;

/**
 *
 * @author Helmond
 */
public interface ODE {
    
    public final static ODE RK1 = new RK1();
    
    public void ode(double[] x0, double[] xFinal, int n, double t1, double t2, Derivative dxdt);
    
    static class RK1 implements ODE{

        @Override
        public void ode(double[] x0, double[] xFinal, int n, double t1, double t2, Derivative dxdt) {
            double[] xdot = new double[n];
            double dt = t2-t1;
            dxdt.dxdt(t2, x0, xdot);
            for(int i = 0; i < n;i++)
            {
                xFinal[i]=x0[i]+dt*xdot[i];
            }
        }
        
    }
    
}
