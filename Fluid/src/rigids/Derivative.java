package rigids;

/**
 * Auxillary class than enables to give a method as arument.
 *
 * @author Helmond
 */
public interface Derivative {
    
    public void dxdt(double t, double[] x, double[] xdot);
    
}
