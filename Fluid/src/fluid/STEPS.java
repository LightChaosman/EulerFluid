package fluid;



/*
 * This class contains overloaded operations on both double[][] and Vector2[][] fields.
 * Could have been done using generics, which is 'less error prone', as we would not have code dupplication.
 * However, as we have just 2 cases, we choose this approach for both efficiency and readability
 */
/**
 *
 * @author Helmond
 */
public class STEPS {

    public static void addSource(double[][] x, double[][] s, double dt) {
        assert s.length == x.length;
        assert s[0].length == s.length;
        assert x[0].length == x.length;
        assert x.length >= 2;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {//Notice that since field.length == field[0].length this is justified
                x[i][j] += s[i][j] * dt;
            }
        }
    }


    public static void diffuse(int b, double[][] x, double[][] x0, double diff, double dt, StaticObjectsField so) {
        assert x0.length == x.length;
        assert x0[0].length == x0.length;
        assert x[0].length == x.length;
        assert x.length >= 2;
        int N = x.length - 2;
        double a = dt * diff * N * N;
        for (int k = 0; k < 20; k++) {
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    x[i][j] = (x0[i][j] + a * (x[i - 1][j] + x[i + 1][j]
                            + x[i][j - 1] + x[i][j + 1])) / (1 + 4 * a);
                }
            }
            set_bnd(b, x,so);
        }
    }


    public static void advect(int b, double[][] d, double[][] d0, double[][] u, double[][] v, double dt, StaticObjectsField so) {
        assert d.length == d.length;
        assert d0[0].length == d0.length;
        assert d[0].length == d.length;
        assert d.length == u.length && d.length == u[0].length && d.length == v.length && d.length == v[0].length;
        assert d.length >= 2;
        int N = d.length - 2;
        int i, j, i0, j0, i1, j1;
        double x, y, s0, t0, s1, t1, dt0;
        dt0 = dt * N;
        
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                x = i - dt0 * u[i][j];
                y = j - dt0 * v[i][j];
                if (x < 0.5) {
                    x = 0.5;
                }
                if (x > N + 0.5) {
                    x = N + 0.5;
                }
                i0 = (int) x;
                i1 = i0 + 1;
                if (y < 0.5) {
                    y = 0.5;
                }
                if (y > N + 0.5) {
                    y = N + 0.5;
                }
                j0 = (int) y;
                j1 = j0 + 1;
                s1 = x - i0;
                s0 = 1 - s1;
                t1 = y - j0;
                t0 = 1 - t1;
                d[i][j] = s0 * (t0 * d0[i0][j0] + t1 * d0[i0][j1])
                        + s1 * (t0 * d0[i1][j0] + t1 * d0[i1][j1]);
            }
        }
        set_bnd(b, d,so);
    }

    public static void set_bnd(int b, double[][] x, StaticObjectsField so) {
        assert x.length == x[0].length;
        int N = x.length - 2;
        so.setBnd(x, b);
        int i;
        for (i = 1; i <= N; i++) {
            x[0][i] = b == 1 ? -x[1][i] : x[1][i];
            x[N + 1][i] = b == 1 ? -x[N][i] : x[N][i];
            x[i][0] = b == 2 ? -x[i][1] : x[i][1];
            x[i][N + 1] = b == 2 ? -x[i][N] : x[i][N];
        }
        x[0][0] = 0.5 * (x[1][0] + x[0][1]);
        x[0][N + 1] = 0.5 * (x[1][N + 1] + x[0][N]);
        x[N + 1][0] = 0.5 * (x[N][0] + x[N + 1][1]);
        x[N + 1][N + 1] = 0.5 * (x[N][N + 1] + x[N + 1][N]);
    }

    public static void project(double[][] u, double[][] v, double[][] p, double[][] div, StaticObjectsField so) {
        int N = u.length - 2;
        int i, j, k;
        double h;
        h = 1.0 / N;
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                div[i][j] = -0.5 * h * (u[i + 1][j] - u[i - 1][j]
                        + v[i][j + 1] - v[i][j - 1]);
                p[i][j] = 0;
            }
        }
        set_bnd(0, div,so);
        set_bnd(0, p,so);
        for (k = 0; k < 20; k++) {
            for (i = 1; i <= N; i++) {
                for (j = 1; j <= N; j++) {
                    p[i][j] = (div[i][j] + p[i - 1][j] + p[i + 1][j]
                            + p[i][j - 1] + p[i][j + 1]) / 4;
                }
            }
            set_bnd(0, p,so);
        }
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                u[i][j] -= 0.5 * (p[i + 1][j] - p[i - 1][j]) / h;
                v[i][j] -= 0.5 * (p[i][j + 1] - p[i][j - 1]) / h;
            }
        }
        set_bnd(1, u,so);
        set_bnd(2, v,so);
    }

    /**
     * Adds the vorticity forces to the user input forces.
     * User input forces assumed to be given in resu and resv
     * @param resu
     * @param resv
     * @param u
     * @param v
     * @param epsilon 
     */
    public static void computeVorticityForce(double[][] resu, double[][] resv, double[][] u, double[][] v, double epsilon)
    {
        assert resu.length == resv.length && resu[0].length == resu.length && resv[0].length == resv.length;
        int N = u.length-2;
        double h = 1d/N;
        double h2=2*h;
        double[][] omegazs = new double[u.length][u.length];
        for(int i = 1; i <= N; i++)
        {
            for(int j = 1; j <= N; j++)
            {
                //approximating spatial derivatives using central scheme
                double vdx = (v[i+1][j]-v[i-1][j])/h2;
                double udy = (u[i][j+1]-u[i][j-1])/h2;
                double omegaz = vdx-udy;//The z component of nabla cross (u,v,0)
                omegazs[i][j]=omegaz;
            }
        }
        for(int i = 1; i <= N; i++)
        {
            for(int j = 1; j <= N; j++)
            {
                double etax = (Math.abs(omegazs[i+1][j])-Math.abs(omegazs[i-1][j]))/h2;//the x derivative
                double etay = (Math.abs(omegazs[i][j+1])-Math.abs(omegazs[i][j-1]))/h2;
                double etasize = Math.sqrt(etax*etax+etay*etay); // abs(eta)
                if(etasize == 0) continue;
                double Nx = etax/etasize;//The x and y components of N
                double Ny = etay/etasize;
                
                double omegaz = omegazs[i][j];
                
                double NXomegax = Ny * omegaz - 0*0;
                double NXomegay = 0*0-Nx*omegaz;
                
                resu[i][j]+=epsilon*h*NXomegax;
                resv[i][j]+=epsilon*h*NXomegay;
            }
        }
    }
    
}