package main;

import fluid.STEPS;
import java.awt.BasicStroke;
import rigids.Polygon;
import rigids.RigidBody;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import rigids.forces.MouseSpring;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Helmond
 */
public class GUI extends javax.swing.JFrame {

    private final static String GUIDE = ""
            + "Controls:\n"
            + "Click anywhere to add 'smoke'\n"
            + "Click anywhere while holding CTRL to place solid walls\n"
            + "Click anywhere while holding SHIFT to exert a force on the fluid\n"
            + "Click on a moving solid to drag it around\n"
            + "Right click anywhere to reestablish the last connection to a rigid body\n"
            + "Press SPACE to toggle simulation on/off\n"
            + "Press V to toggle the rendering of the vector field\n"
            + "Press M to toggle wind-tunnel mode\n"
            + "Play with SHIFT + middle mouse button for fun";

    private HashMap<RigidBody, java.awt.Polygon> polymap = new HashMap<>();

    private final Simulation s;
    boolean running;

    boolean pressing = false;
    boolean ctrl = false;
    boolean shift = false;
    boolean showVecField = true;
    double fps = 0;
    boolean middle = false;

    RigidBody dragging = null, pdragging;
    double[] dragloc, pdragloc;
    Point intern;
    Point loc;
    Point pintern;

    /**
     * Creates new form GUI
     */
    public GUI(Simulation s) {
        initComponents();
        this.s = s;
        SwingWorker sw = new SwingWorker() {
            Exception e;

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    while (true) {
                        long x = System.nanoTime();
                        boolean handled = false;
                        if (pressing & ctrl && dragging == null && intern!=null) {
                            s.addStaticBlock(intern.x, intern.y);
                            handled = true;
                        }
                        if (running) {
                            double[][] rho = new double[s.N + 2][s.N + 2];
                            double[][] u = new double[s.N + 2][s.N + 2];
                            double[][] v = new double[s.N + 2][s.N + 2];
                            if (pressing && !handled && dragging == null) {
                                if (shift) {
                                    if (middle && pintern != null) {
                                        u[intern.x][intern.y] = (intern.x - pintern.x);
                                        v[intern.x][intern.y] = (intern.y - pintern.y);
                                    } else {
                                        double scale = 1;
                                        for (int i = -1; i <= 1; i++) {
                                            for (int j = -1; j <= 1; j++) {
                                                if (i == 0 && j == 0) {
                                                    continue;
                                                }
                                                double phi = Math.atan2(j, i);
                                                u[intern.x + i][intern.y + j] = scale * Math.sin(phi);
                                                v[intern.x + i][intern.y + j] = scale * Math.cos(phi);
                                            }

                                        }
                                    }
                                } else {
                                    rho[intern.x][intern.y] = s.N * s.N / 50;
                                }
                            } else if (dragging != null) {
                                MouseSpring ms = (MouseSpring) s.forces.get(0);
                                ms.body = dragging;
                                ms.x = (double) loc.x / jPanel1.getWidth();
                                ms.y = (double) loc.y / jPanel1.getHeight();
                            }
                            s.step(rho, u, v);
                        }
                        jPanel1.repaint();
                        long dt = System.nanoTime() - x;
                        if (dt > 0 && running) {
                            fps = 1000000000d / dt;
                        }

                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, ex);
                    System.out.println(ex);
                    ex.printStackTrace();
                    return null;
                } catch (Error ex) {
                    JOptionPane.showMessageDialog(rootPane, ex);
                    System.out.println(ex);
                    ex.printStackTrace();
                    return null;
                }

            }

        };
        sw.execute();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new VisPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI(INITIALS.GRAVITY()).setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    private class VisPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

        public VisPanel() {
            super();
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.setFocusable(true);
            this.addKeyListener(this);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g); //To change body of generated methods, choose Tools | Templates.
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            int N = s.N;
            int i0 = 0, j0 = 0;//i0=i-1, j0=j-1
            double dw = (double) this.getWidth() / s.N;
            double dh = (double) this.getHeight() / s.N;
            int dw2 = (int) dw;
            int dh2 = (int) dh;
            if (true) {
                drawCells(dw, i0, dh, j0, g, dw2, dh2);
            } else {
                drawNice(g);
            }

            i0 = 0;
            j0 = 0;
            if (showVecField) {
                drawVelocities(dw, i0, dh, j0, g);
            }
            drawRigidBodies(g);
            g.setColor(Color.WHITE);
            String guide2 = GUIDE + "\nfps: " + fps + "\nTotal mass in system: " + s.mass;
            drawString(g, guide2, 5, this.getHeight() - (g.getFontMetrics().getHeight() * (guide2.split("\n").length) + 5));
        }

        void drawString(Graphics g, String text, int x, int y) {
            for (String line : text.split("\n")) {
                g.drawString(line, x, y += g.getFontMetrics().getHeight());
            }
        }

        private void drawVelocities(double dw, int i0, double dh, int j0, Graphics g) {
            for (int i = 1; i <= s.N; i++) {
                for (int j = 1; j <= s.N; j++) {

                    g.setColor(Color.RED);
                    double u = s.u.u[i][j];
                    double v = s.u.v[i][j];
                    int centerx = (int) (dw * i0 + dw / 2d);
                    int centery = (int) (dh * j0 + dh / 2d);
                    int endx = (int) (centerx + getWidth() * u);
                    int endy = (int) (centery + getHeight() * v);
                    g.drawLine(centerx, centery, endx, endy);

                    j0 = j;
                }
                j0 = 0;
                i0 = i;
            }
        }

        private void drawCells(double dw, int i0, double dh, int j0, Graphics g, int dw2, int dh2) {
            for (int i = 1; i <= s.N; i++) {
                for (int j = 1; j <= s.N; j++) {

                    int x0 = (int) (dw * i0);
                    int y0 = (int) (dh * j0);
                     
                        double rho = s.rho.field[i][j];
                        double rho2 = Math.max(Math.min(1, rho), 0);
                        double rho3 = Math.max(Math.min(1, rho - 1), 0);
                        double rho4 = Math.max(Math.min(1, rho - 2), 0);
                        double rho5 = Math.max(Math.min(1, rho - 3), 0);
                        double rho6 = Math.max(Math.min(1, rho - 4), 0);
                        double rho7 = Math.max(Math.min(1, rho - 5), 0);
                        double rho8 = Math.max(Math.min(1, rho - 6), 0);
                        double rho9 = Math.max(Math.min(1, rho - 7), 0);
                        Color c = new Color((int) ((rho5) * 255), (int) ((rho2 - rho4 + rho7) * 255), (int) ((rho3 - rho6 + rho8) * 255));
                        g.setColor(c);
                        g.fillRect(x0, y0, dw2 + 1, dh2 + 1);
                    if (s.so.ocs[i][j] != 0) {
                        g.setColor(Color.BLUE);
                        g.drawRect(x0, y0, dw2 + 1, dh2 + 1);
                        g.setColor(Color.WHITE);
                        g.drawString(s.so.ocs[i][j] + "", x0, y0 + (int) dh);
                    }

                    j0 = j;
                }
                j0 = 0;
                i0 = i;
            }
        }

        private void drawNice(Graphics g) {
            for (int x = 0; x < this.getWidth(); x++) {
                for (int y = 0; y < this.getHeight(); y++) {
                    double intx = ((double) x / (this.getWidth())) * (s.N - 1) + 1; //[1..N]
                    double inty = ((double) y / (this.getHeight())) * (s.N - 1) + 1;
                    int x0 = (int) intx;
                    int x1 = x0 + 1;
                    int y0 = (int) inty;
                    int y1 = y0 + 1;
                    double dx = x1 - intx;
                    double dy = y1 - inty;
                    double dx2 = 1 - dx;
                    double dy2 = 1 - dy;
                    double rho = s.rho.field[x0][y0] * dx * dy + s.rho.field[x1][y0] * dx2 * dy + s.rho.field[x0][y1] * dx * dy2 + s.rho.field[x1][y1] * dx2 * dy2;
                    rho = Math.max(Math.min(1, rho * 10), 0);
                    Color c = new Color(0, (int) (rho * 255), 0);
                    g.setColor(c);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        private Point toInternal(Point mouse) {
            int x = (int) Math.floor((float) mouse.x * s.N / this.getWidth()) + 1;
            int y = (int) Math.floor((float) mouse.y * s.N / this.getHeight()) + 1;
            return new Point(Math.min(s.N, Math.max(1, x)), Math.min(s.N, Math.max(1, y)));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (pdragging != null && dragging == null) {
                    dragging = pdragging;
                    dragloc = pdragloc;
                    loc = e.getPoint();
                    pintern = intern;
                    intern = toInternal(loc);
                }
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                
                middle = true;

            } else {
                for (int i = 0; i < s.rbodies.bodies.length; i++) {
                    if (polymap.containsKey(s.rbodies.bodies[i]) && polymap.get(s.rbodies.bodies[i]).contains(e.getPoint())) {
                        dragging = s.rbodies.bodies[i];
                        double intx = (double) e.getPoint().x / this.getWidth();
                        double inty = (double) e.getPoint().y / this.getHeight();
                        intx -= dragging.x;
                        inty -= dragging.y;
                        double locx = intx * Math.cos(-dragging.theta) + inty * (-Math.sin(-dragging.theta));
                        double locy = intx * Math.sin(-dragging.theta) + inty * (Math.cos(-dragging.theta));
                        dragloc = new double[]{locx, locy};
                        ((MouseSpring) s.forces.get(0)).bbx = locx;
                        ((MouseSpring) s.forces.get(0)).bby = locy;
                    }
                }
            }
            pressing = true; //To change body of generated methods, choose Tools | Templates.
            loc = e.getPoint();
            pintern = intern;
            intern = toInternal(loc);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            middle = false;
            pressing = false;
            pdragging = dragging != null ? dragging : pdragging;
            dragging = null;
            pdragloc = dragloc != null ? dragloc : pdragloc;
            dragloc = null;
            ((MouseSpring) s.forces.get(0)).body = null;

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            
            loc = e.getPoint();
            pintern = intern;
            intern = toInternal(loc);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    running = !running;
                    System.out.println("Running: " + running);
                    break;
                case KeyEvent.VK_CONTROL:
                    ctrl = true;
                    break;
                case KeyEvent.VK_SHIFT:
                    shift = true;
                    break;
                case KeyEvent.VK_V:
                    showVecField = !showVecField;
                    break;
                case KeyEvent.VK_M:
                    STEPS.bndmode = (STEPS.bndmode + 1) % (STEPS.MAXMODE + 1);
                default:
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_CONTROL:
                    ctrl = false;
                    break;
                case KeyEvent.VK_SHIFT:
                    shift = false;
                    break;
                default:
                    break;
            }
        }

        private void drawRigidBodies(Graphics g) {
            Color c = Color.YELLOW;
            g.setColor(c);
            for (RigidBody r : s.rbodies.bodies) {
                Polygon p = r.p;
                int[] xs = new int[p.pxs.length];
                int[] ys = new int[p.pxs.length];
                for (int i = 0; i < xs.length; i++) {
                    double rx = p.pxs[i] * r.Rxx + p.pys[i] * r.Rxy;
                    double ry = p.pxs[i] * r.Ryx + p.pys[i] * r.Ryy;
                    xs[i] = (int) ((rx + r.x) * this.getWidth());
                    ys[i] = (int) ((ry + r.y) * this.getHeight());
                }
                java.awt.Polygon p2 = new java.awt.Polygon(xs, ys, xs.length);
                g.fillPolygon(p2);
                polymap.put(r, p2);
                int cx = (int) (this.getWidth() * r.x);
                int cy = (int) (this.getHeight() * r.y);
                int dvy = (int) (this.getHeight() * r.vy);
                int dvx = (int) (this.getWidth() * r.vx);
                int dFy = (int) (this.getHeight() * r.Fy * 100);
                int dFx = (int) (this.getWidth() * r.Fx * 100);
                g.setColor(Color.BLUE);
                g.drawLine(cx, cy, cx + dvx, cy + dvy);
                g.setColor(Color.GREEN);
                g.drawLine(cx, cy, cx + dFx, cy + dFy);
            }

            if (dragging != null) {
                double x = dragging.x + dragloc[0] * dragging.Rxx + dragloc[1] * dragging.Rxy;
                double y = dragging.y + dragloc[0] * dragging.Ryx + dragloc[1] * dragging.Ryy;
                ((Graphics2D) g).setStroke(new BasicStroke(2f));
                g.setColor(Color.PINK);
                g.drawLine(loc.x, loc.y, (int) (x * this.getWidth()), (int) (y * this.getHeight()));
                ((Graphics2D) g).setStroke(new BasicStroke(1f));
            }
        }

    }
}
