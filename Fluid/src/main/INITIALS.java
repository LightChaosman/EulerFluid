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

    public static Simulation GRAVITY() {
        double scale = 1;
        RigidBody rb = RigidBody.tiltedSquare(0, .05, .5 + 0.2, .5 + 0);
        RigidBody rb2 = RigidBody.tiltedSquare(0, .1, .5 - 0.2, .5 + 0);
        RigidBody rb3 = RigidBody.tiltedSquare(0, .15, .5, .5+.2 + 0);
        RigidBody rb4 = RigidBody.tiltedSquare(0, .2, .5, .5-.2 + 0);
        RigidBody[] rbs = new RigidBody[]{rb, rb2,rb3,rb4};
        int N = 100;
        Simulation s = new Simulation(N, 0.05, 0.0001, 0.0000001, 1, new RigidBodies(rbs, N));
        for (int i = 1; i <= s.N; i++) {
            for (int j = 1; j <= s.N; j++) {
                int dx = i - s.N / 2;
                int dy = j - s.N / 2;
                double dist = Math.sqrt(dx * dx + dy * dy) + 1;
                double mag = dist == 0 ? 0 : 1d / dist / dist;
                s.u.u[i][j] = scale * mag * dy;
                s.u.v[i][j] = -scale * mag * dx;
            }
        }
        return s;
    }

    public static Simulation FAN() {
        RigidBody rb = RigidBody.fan(.5, .5, .3);
        rb.Px = 0.001;
        rb.L = .025;

        RigidBody[] rbs = new RigidBody[]{rb};
        Simulation s = new Simulation(100, 0.05, 0.0001, 0.00001, .5, new RigidBodies(rbs, 100));
        return s;
    }

}
