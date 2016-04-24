package rigids;

import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Polygon class using doubles for coordinates center of mass is assumed to be
 * at (0,0)
 *
 * @author Helmond
 */
public class Polygon {

    public final double[] pxs, pys;

    public Polygon(double[] pxs, double[] pys) {
        this.pxs = pxs;
        this.pys = pys;
    }

    /**
     *
     * @param sides The ammount of sides this approximation has
     * @param radius The radius of the circle
     * @param mx The x coordinate of the centre
     * @param my The y coordinate of the centre
     * @return 2 Polygons: the first is the actual polygon, the second contains
     * the normal vectors. That is, the first Point of p2 is the normal to the
     * first segoment of p1
     */
    public static Polygon regularPoly(int sides, double radius) {
        double[] pxs = new double[sides], pys = new double[sides];
        for (int i = 0; i < sides; i++) {
            double a1 = Math.PI * 2d / sides * i + Math.PI / 4;//used for point
            double a2 = Math.PI * 2d / sides * (i + 0.5d) + Math.PI / 4;//used for normal
            pxs[i] = Math.sin(a1) * radius;
            pys[i] = Math.cos(a1) * radius;
            //nxs[i] = Math.cos(a2);
            //nys[i] = -Math.sin(a2);
            //Note that the normal vector already has length 1!
        }
        return new Polygon(pxs, pys);
    }
    
    public static Polygon fan(int blades, double radius)
    {
        final int POINTSPERBLADE=4;
        double[] pxs = new double[blades*POINTSPERBLADE],pys = new double[blades*POINTSPERBLADE];
        for(int i = 0; i < blades; i++)
        {
            double a = 2*Math.PI/blades * i;
            double a1 = a-1d/blades;
            double a2 = a+1d/blades;
            double sin1 = Math.sin(a1);
            double sin2 = Math.sin(a2);
            double cos1 = Math.cos(a1);
            double cos2 = Math.cos(a2);
            double r1 = radius/10;
            double r2 = radius;
            pxs[i*4+0] = sin1*r1;
            pxs[i*POINTSPERBLADE+1] = sin1*r2;
            pxs[i*POINTSPERBLADE+2] = sin2*r2;
            pxs[i*POINTSPERBLADE+3] = sin2*r1;
            pys[i*POINTSPERBLADE+0] = cos1*r1;
            pys[i*POINTSPERBLADE+1] = cos1*r2;
            pys[i*POINTSPERBLADE+2] = cos2*r2;
            pys[i*POINTSPERBLADE+3] = cos2*r1;
        }
        System.out.println(Arrays.toString(pxs));
        System.out.println(Arrays.toString(pys));
        return new Polygon(pxs, pys);
    }

    /**
     * Return true if the given point is contained inside the boundary. See:
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     *
     * @param test The point to check
     * @return true if the point is inside the boundary, false otherwise
     *
     */
    boolean contains(double px, double py) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = pxs.length - 1; i < pxs.length; j = i++) {
            if ((pys[i] > py) != (pys[j] > py)
                    && (px < (pxs[j] - pxs[i]) * (py - pys[i]) / (pys[j] - pys[i]) + pxs[i])) {
                result = !result;
            }
        }
        return result;
    }

}
