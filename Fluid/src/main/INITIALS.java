package main;


import rigids.RigidBodies;
import rigids.RigidBody;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Helmond
 */
public class INITIALS {
    
    
    public static Simulation GRAVITY()
    {
        double scale = 1;
        RigidBody rb = RigidBody.tiltedSquare(Math.PI*2d/360d * 0, .1, .5+0.2, .5+0);
        rb.Px = -.0001;
        //rb.L = -.00001;
        Simulation s = new Simulation(100,0.05,0.0000001,0.0001,1,new RigidBodies(new RigidBody[]{rb}));
        for(int i = 1; i <= s.N; i++){for(int j = 1; j <= s.N;j++){
            int dx = i-s.N/2;
            int dy = j-s.N/2;
            double dist = Math.sqrt(dx*dx+dy*dy)+1;
            double mag = dist==0?0:1d/dist/dist;
            s.u.u[i][j] = scale* mag * dy;
            s.u.v[i][j] = -scale * mag * dx;
        }
        }
        return s;
    }
    

    

    
}
