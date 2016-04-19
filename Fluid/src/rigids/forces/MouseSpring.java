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
public class MouseSpring implements Force{
    
    public RigidBody body;
    public double x,y;//mouse location
    public double bbx, bby;//body-space locaion of grabbed point -> still needs to be transformed

    @Override
    public void addForces() {
        if(body==null)return;
        double tx = body.x+bbx*body.Rxx+bby*body.Rxy;
        double ty = body.y+bby*body.Ryx+bby*body.Ryy;
        double dx = x-tx;
        double dy = y-ty;
        body.Fx+=dx*0.0000001;
        body.Fy+=dy*0.0000001;
        
    }
    
    
    
}
