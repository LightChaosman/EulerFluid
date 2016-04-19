package rigids;

/**
 *
 * @author Helmond
 */
public class RigidBody {
    /*Constant quantities*/
    public double M;//mass
    public double I;//Inertia tensor........TODO?
    public Polygon p;//The actual body
    
    /* State variables */
    public double x, y; // position
    public double Px, Py;// Linear momentum
    public double L; // Angular momentum
    public double theta;
    public double Rxx, Rxy, Ryx, Ryy; // Current rotation
    //Rxx Rxy
    //Ryx Ryy
    
    
    
    /* Derived quantities*/
    public double vx,vy; //speed
    public double omega; // angular velocity
    
    /*Computed quanties*/
    public double Fx, Fy;//Force
    public double tau;//torque
    
    
    
    public static final RigidBody tiltedSquare(double theta, double sideLength, double mx, double my)
    {
        RigidBody r = new RigidBody();
        Polygon p = Polygon.regularPoly(4,Math.sqrt(2)*sideLength/2d);
        r.p = p;
        r.I = Math.pow(sideLength, 4);
        r.M = sideLength*sideLength;
        r.x = mx;
        r.y = my;
        r.theta=theta;
        r.setAuxs();
        
        return r;
    }

    public void setAuxs() {
        Rxx = Math.cos(theta);
        Rxy = -Math.sin(theta);
        Ryx = Math.sin(theta);
        Ryy = Math.cos(theta);
        
        vx = Px / M;
        vy = Py / M;
        omega = L / I;
    }
    
    
    
    
    
}
