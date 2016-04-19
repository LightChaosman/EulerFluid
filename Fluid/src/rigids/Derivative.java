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
public interface Derivative {
    
    public void dxdt(double t, double[] x, double[] xdot);
    
}
