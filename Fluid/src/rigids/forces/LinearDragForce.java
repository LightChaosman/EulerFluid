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
public class LinearDragForce implements Force{
    
    RigidBody body;
    private double k;

    public LinearDragForce(RigidBody body, double k) {
        this.body = body;
        this.k = k;
    }
    
    

    @Override
    public void addForces() {
        body.Fx += -k*body.vx;
        body.Fy += -k*body.vy;
    }
    
}
