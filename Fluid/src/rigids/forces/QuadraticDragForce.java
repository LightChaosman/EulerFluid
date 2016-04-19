/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rigids.forces;

import rigids.RigidBody;

/**
 *
 * @author Helmond
 */
public class QuadraticDragForce implements Force{
    
    RigidBody body;
    private double k;

    public QuadraticDragForce(RigidBody body, double k) {
        this.body = body;
        this.k = k;
    }
    
    

    @Override
    public void addForces() {
        double speed2 = body.vx*body.vx+body.vy*body.vy;
        double l = Math.sqrt(speed2);
        body.Fx += -k*body.vx/l*speed2;
        body.Fy += -k*body.vy/l*speed2;
        
        body.tau += -k*k*k*k*body.omega*Math.abs(body.omega);
    }
    
}
