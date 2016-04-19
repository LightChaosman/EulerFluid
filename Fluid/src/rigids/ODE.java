/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
                if(i==8)
                {
                    
                System.out.println(x0[i] + " --" + dt +"--> " +xFinal[i]);
                }
            }
        }
        
    }
    
}
