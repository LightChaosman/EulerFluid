package rigids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
    public double vx, vy; //speed
    public double omega; // angular velocity

    /*Computed quanties*/
    public double Fx, Fy;//Force
    public double tau;//torque
    public Polygon tp; //The transformed polygon
    private List<OccupiedCell> cells = new ArrayList<>();
    private HashSet<OccupiedCell> outsidecells = new HashSet<>();

    public static final RigidBody tiltedSquare(double theta, double sideLength, double mx, double my) {
        RigidBody r = new RigidBody();
        Polygon p = Polygon.regularPoly(4, Math.sqrt(2) * sideLength / 2d);
        r.p = p;
        r.M = sideLength * sideLength/5;
        r.I = r.M * r.M;//for this specific case...
        r.x = mx;
        r.y = my;
        r.theta = theta;
        r.setAuxs();

        return r;
    }
    
    public static RigidBody fan(double cx, double cy, double r) {
        RigidBody rb = new RigidBody();
        Polygon p = Polygon.fan(3, r);
        rb.p = p;
        rb.M = 100;
        rb.I = 1;//for this specific case...
        rb.x = cx;
        rb.y = cy;
        rb.setAuxs();

        return rb;
    }

    public void setAuxs() {
        Rxx = Math.cos(theta);
        Rxy = -Math.sin(theta);
        Ryx = -Rxy;
        Ryy = Rxx;

        vx = Px / M;
        vy = Py / M;
        omega = L / I;

        double[] px = new double[p.pxs.length];
        double[] py = new double[p.pxs.length];
        for (int i = 0; i < px.length; i++) {
            px[i] = p.pxs[i] * Rxx + p.pys[i] * Rxy + x;
            py[i] = p.pxs[i] * Ryx + p.pys[i] * Ryy + y;
        }
        this.tp = new Polygon(px, py);
    }
    
    public void updateOccupiedCells(int N)
    {
        cells.clear();
        outsidecells.clear();
        int n = p.pxs.length - 1;
        for (int i = 0; i <= n - 1; i++) {

            findOccupiedCells(N, tp.pxs[i], tp.pys[i], tp.pxs[i + 1], tp.pys[i + 1]);
        }
        findOccupiedCells(N, tp.pxs[n], tp.pys[n], tp.pxs[0], tp.pys[0]);
    }

    public List<OccupiedCell> getOccupiedCells()//Assume we are on a NxN grid of dimension 1x1, that is each cell has height==width==1/N
    {
        return cells;
    }
    
    public HashSet<OccupiedCell> getOutsideCells()//Assume we are on a NxN grid of dimension 1x1, that is each cell has height==width==1/N
    {
        return outsidecells;
    }

    private void findOccupiedCells(int N, double x1, double y1, double x2, double y2) {
        double u = x2 - x1;
        double v = y2 - y1;
        double h2 = 1d/(2*N);
        int X = (int) ((x1+h2) * N) + 1;
        int Y = (int) ((y1+h2) * N) + 1;
        addCell(X, Y, N);

        //looking for intersections s.t. X+tu = 1/2+k, 0<=t<=1, similar for Y+tv
        int stepX = (int) Math.signum(u);
        int stepY = (int) Math.signum(v);
        double tMaxX = u == 0 ? 2 : (u > 0
                ? (((X + .5) - 1d) / N - x1) / u
                : (((X - .5) - 1d) / N - x1) / u);
        double tMaxY = v == 0 ? 2 : (v > 0
                ? (((Y + .5) - 1d) / N - y1) / v
                : (((Y - .5) - 1d) / N - y1) / v);
        double tDeltaX = 1 / (Math.abs(u) * N);
        double tDeltaY = 1 / (Math.abs(v) * N);

        while (true) {
            if (tMaxX < tMaxY) {
                if (tMaxX > 1) {
                    break;
                }
                X = X + stepX;
                addCell(X, Y, N);
                tMaxX = tMaxX + tDeltaX;
            } else {
                if (tMaxY > 1) {
                    break;
                }
                Y = Y + stepY;
                addCell(X, Y, N);
                tMaxY = tMaxY + tDeltaY;
            }
        }
    }

    private void addCell(int X, int Y, int N) {
        addCell2(cells, X, Y,N);
        double ix = (X - 1d) / N;
        double iy = (Y - 1d) / N;
        double h = 1d / N;
        if(contains(ix+h,iy)){addCell2(cells, X+1, Y,N);}else{addCell2(outsidecells, X+1, Y,N);}
        if(contains(ix-h,iy)){addCell2(cells, X-1, Y,N);}else{addCell2(outsidecells, X-1, Y,N);}
        if(contains(ix,iy+h)){addCell2(cells, X, Y+1,N);}else{addCell2(outsidecells, X, Y+1,N);}
        if(contains(ix,iy-h)){addCell2(cells, X, Y-1,N);}else{addCell2(outsidecells, X, Y-1,N);}
    }

    private void addCell2(Collection<OccupiedCell> res, int X, int Y, int N) {
        if(X>=0 && X<= N+1 && Y>=0 && Y<+N+1)res.add(generateCell(X,Y,N));
    }

    
    public boolean contains(double px, double py) {
        return tp.contains(px, py);
    }

    private OccupiedCell generateCell(int X, int Y, int N) {
        double vx = this.vx;
        double vy = this.vy;
        
        double px = (X-1d)/N;
        double py = (Y-1d)/N;
        double dx = px-x;
        double dy = py-y;
        double d = Math.sqrt(dx*dx+dy*dy);
        double av = omega*d;
        
        double avx = d==0?0:(-dy)/d * av;
        double avy = d==0?0:dx/d*av;
        return new OccupiedCell(X,Y,vx+avx,vy+avy);
        
    }

}
