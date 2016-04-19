package rigids;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Polygon class using doubles for coordinates
 * center of mass is assumed to be at (0,0)
 * 
 * @author Helmond
 */
public class Polygon {
   
    
    public final double[] pxs,pys,nxs,nys;

    public Polygon(double[] pxs, double[] pys, double[] nxs, double[] nys) {
        this.pxs = pxs;
        this.pys = pys;
        this.nxs = nxs;
        this.nys = nys;
    }
    
        /**
     * 
     * @param sides The ammount of sides this approximation has
     * @param radius The radius of the circle
     * @param mx The x coordinate of the centre
     * @param my The y coordinate of the centre
     * @return 2 Polygons: the first is the actual polygon, the second contains the normal vectors. That is, the first Point of p2 is the normal to the first segoment of p1
     */
    public static Polygon regularPoly(int sides, double radius)
    {
        double[] pxs = new double[sides],pys = new double[sides], nxs = new double[sides],nys = new double[sides];
        for(int i = 0; i < sides;i++)
        {
            double a1 = Math.PI*2d/sides * i+Math.PI/4;//used for point
            double a2 = Math.PI*2d/sides * (i+ 0.5d)+Math.PI/4;//used for normal
            pxs[i] = Math.sin(a1)*radius;
            pys[i] = Math.cos(a1)*radius;
            nxs[i] = Math.cos(a2);
            nys[i] = -Math.sin(a2);
            //Note that the normal vector already has length 1!
        }
        return new Polygon(pxs, pys, nxs, nys);
    }
    
       
    
}
