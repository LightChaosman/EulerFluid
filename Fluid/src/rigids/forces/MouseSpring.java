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
    public double k = 0.0001;

    @Override
    public void addForces() {
        if(body==null)return;
        double tx = body.x+bbx*body.Rxx+bby*body.Rxy;
        double ty = body.y+bbx*body.Ryx+bby*body.Ryy;
        double dx = x-tx;
        double dy = y-ty;
        double Fix = dx*k;
        double Fiy = dy*k;
        body.Fx+=Fix;
        body.Fy+=Fiy;
        
        
        double rix = tx-body.x;
        double riy = ty-body.y;
        body.tau+= (rix * Fiy - riy *Fix);
        //(ri(t)− x(t))cross Fi(t)):
    }
    
    
    
}
