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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import rigids.OccupiedCell;
import rigids.forces.MouseSpring;
/**
 * Very messy I/O and GUI handling class
 * Just take it at face value...
 *
 * @author Helmond
 */
public class GUI extends javax.swing.JFrame {

    private HashMap<RigidBody, java.awt.Polygon> polymap = new HashMap<>();

    private Simulation s;
    boolean running;

    boolean pressing = false;
    boolean ctrl = false;
    boolean shift = false;
    boolean showVecField = true;
    double fps = 0;
    boolean middle = false;
    boolean splus = false, smin = false;
    boolean cdown = false;
    byte drawRigidBounds = 0;
    boolean images = false;

    RigidBody dragging = null, pdragging;
    double[] dragloc, pdragloc;
    Point intern;
    Point loc;
    Point pintern;

    /**
     * Creates new form GUI
     */
    public GUI(Simulation s2) {
        initComponents();
        this.s = s2;
        SwingWorker sw = new SwingWorker() {
            Exception e;

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    while (true) {
                        long x = System.nanoTime();
                        boolean handled = false;
                        if (pressing & ctrl && dragging == null && intern != null) {
                            s.addStaticBlock(intern.x, intern.y);
                            handled = true;
                        }
                        if (running) {
                            double[][] rho = new double[s.N + 2][s.N + 2];
                            double[][] u = new double[s.N + 2][s.N + 2];
                            double[][] v = new double[s.N + 2][s.N + 2];
                            if (pressing && !handled && dragging == null) {
                                if (splus) {
                                    s.rho.incSource(intern.x, intern.y);
                                } else if (smin) {
                                    s.rho.decSource(intern.x, intern.y);
                                } else if (shift) {
                                    AddMouseForces(u, v);
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
                        if (images && running) {
                            BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2 = image.createGraphics();
                            jPanel1.paint(g2);
                            jPanel1.repaint();
                            File f = new File("images/");
                            f.mkdir();
                            
                            ImageIO.write(image, "png", new File("images/"+System.currentTimeMillis() + ".png"));
                        } else {
                            jPanel1.repaint();
                        }
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

            private void AddMouseForces(double[][] u, double[][] v) {
                if (middle && pintern != null) {
                    double k = 2;
                    u[intern.x][intern.y] = (intern.x - pintern.x) * k;
                    v[intern.x][intern.y] = (intern.y - pintern.y) * k;
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

        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 1000));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                new GUI(INITIALS.FAN()).setVisible(true);

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

        private int N;
        private int cellWidth;
        private int cellHeight;
        private double h;
        private double h2;
        Graphics2D g;

        void fillCell(int i, int j, Color c) {
            g.setColor(c);
            g.fillRect(getCellTopLeftX(i, j), getCellTopLeftY(i, j), cellWidth, cellHeight);
        }

        void drawCell(int i, int j, Color c) {
            g.setColor(c);
            g.drawRect(getCellTopLeftX(i, j), getCellTopLeftY(i, j), cellWidth, cellHeight);
        }

        void drawCell(int i, int j, Color c, String s) {
            g.setColor(c);
            g.drawRect(getCellTopLeftX(i, j), getCellTopLeftY(i, j), cellWidth, cellHeight);
            g.setColor(Color.WHITE);
            g.drawString(s, getCellTopLeftX(i, j) + 2, getCellTopLeftY(i, j) + cellHeight - 1);
        }

        int getCellCenterX(int i, int j) {
            return (int) (((i - 1) + .5) / N * this.getWidth());
        }

        int getCellCenterY(int i, int j) {
            return (int) (((j - 1) + .5) / N * this.getHeight());
        }

        int getCellTopLeftX(int i, int j) {
            return (int) (((i - 1d)) / N * this.getWidth());
        }

        int getCellTopLeftY(int i, int j) {
            return (int) (((j - 1d)) / N * this.getHeight());
        }

        int convertXtoInt(double x) {
            return (int) ((x + h2) * this.getWidth());
        }

        int convertYtoInt(double y) {
            return (int) ((y + h2) * this.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g); //To change body of generated methods, choose Tools | Templates.
            this.g = (Graphics2D) g;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            N = s.N;
            h = 1d / N;
            h2 = 1d / (2d * N);
            cellWidth = this.getWidth() / N + 1;
            cellHeight = this.getHeight() / N + 1;

            drawCells();

            if (showVecField) {
                drawVelocities();
            }
            drawFixedSolids();
            drawRigidBodies();
            g.setColor(Color.WHITE);
            String guide2 = buildGuide(cdown);
            drawString(guide2, 5, this.getHeight() - (g.getFontMetrics().getHeight() * (guide2.split("\n").length) + 5));
        }

        void drawString(String text, int x, int y) {
            for (String line : text.split("\n")) {
                g.drawString(line, x, y += g.getFontMetrics().getHeight());
            }
        }

        private void drawCells() {
            for (int i = 1; i <= s.N; i++) {
                for (int j = 1; j <= s.N; j++) {
                    double rho = s.rho.field[i][j];
                    double rho2 = Math.max(Math.min(1, rho), 0);
                    double rho3 = Math.max(Math.min(1, rho - 1), 0);
                    double rho4 = Math.max(Math.min(1, rho - 2), 0);
                    double rho5 = Math.max(Math.min(1, rho - 3), 0);
                    double rho6 = Math.max(Math.min(1, rho - 4), 0);
                    double rho7 = Math.max(Math.min(1, rho - 5), 0);
                    double rho8 = Math.max(Math.min(1, rho - 6), 0);
                    Color c = new Color((int) ((rho5) * 255), (int) ((rho2 - rho4 + rho7) * 255), (int) ((rho3 - rho6 + rho8) * 255));
                    fillCell(i, j, c);
                }
            }
        }

        private void drawFixedSolids() {
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    if (s.so.ocs[i][j] != 0) {
                        fillCell(i, j, Color.BLUE);
                    }
                }
            }
        }

        private void drawVelocities() {
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {

                    g.setColor(Color.RED);
                    double u = s.u.u[i][j];
                    double v = s.u.v[i][j];
                    int centerx = getCellCenterX(i, j);
                    int centery = getCellCenterY(i, j);
                    int endx = (int) (centerx + getWidth() * u / 2);
                    int endy = (int) (centery + getHeight() * v / 2);
                    g.drawLine(centerx, centery, endx, endy);
                }
            }
        }

        private Point toInternal(Point mouse) {
            int x = (int) Math.floor((float) mouse.x * s.N / this.getWidth()) + 1;
            int y = (int) Math.floor((float) mouse.y * s.N / this.getHeight()) + 1;
            return new Point(Math.min(s.N, Math.max(1, x)), Math.min(s.N, Math.max(1, y)));
        }

        private void drawRigidBodies() {

            for (RigidBody b : s.rbodies.bodies) {
                if (drawRigidBounds == 3) {
                    for (OccupiedCell oc : b.getOutsideCells()) {
                        drawCell(oc.i, oc.j, Color.PINK, "" + s.rbodies.field.ocs[oc.i][oc.j]);
                    }
                }
                if (drawRigidBounds == 1) {
                    for (OccupiedCell oc : b.getOccupiedCells()) {
                        drawCell(oc.i, oc.j, Color.CYAN, "" + s.rbodies.field.ocs[oc.i][oc.j]);
                    }
                } else if (drawRigidBounds >= 2) {
                    for (OccupiedCell oc : b.getOccupiedCells()) {
                        fillCell(oc.i, oc.j, Color.CYAN/*,""+s.rbodies.field.ocs[oc.i][oc.j]*/);
                    }
                }
            }

            for (RigidBody r : s.rbodies.bodies) {
                Color c = Color.YELLOW;
                g.setColor(c);
                Polygon p = r.tp;
                int[] xs = new int[p.pxs.length];
                int[] ys = new int[p.pxs.length];
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = convertXtoInt(p.pxs[i]);
                    ys[i] = convertYtoInt(p.pys[i]);
                }
                java.awt.Polygon p2 = new java.awt.Polygon(xs, ys, xs.length);
                if (drawRigidBounds % 2 == 0) {
                    g.fillPolygon(p2);
                } else {
                    g.drawPolygon(p2);
                }
                polymap.put(r, p2);
                int cx = (int) (this.getWidth() * r.x);
                int cy = (int) (this.getHeight() * r.y);
                int dvy = (int) (this.getHeight() * r.vy);
                int dvx = (int) (this.getWidth() * r.vx);
                int dFy = (int) (this.getHeight() * r.Fy * 1000);
                int dFx = (int) (this.getWidth() * r.Fx * 1000);
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
                g.drawLine(loc.x, loc.y, (int) ((x + h2) * this.getWidth()), (int) ((y + h2) * this.getHeight()));
                ((Graphics2D) g).setStroke(new BasicStroke(1f));
            }
        }

        private String buildGuide(boolean show) {
            String s2;
            if (show) {
                s2 = "Controls:\n"
                        + "Click anywhere to add 'smoke'\n"
                        + "Click anywhere while holding CTRL to place solid walls\n"
                        + "Click anywhere while holding SHIFT to exert a force on the fluid\n"
                        + "Click on a moving solid to drag it around\n"
                        + "Right click anywhere to reestablish the last connection to a rigid body\n"
                        + "Play with SHIFT + middle mouse button for fun\n"
                        + "Press SPACE to toggle simulation on/off\n"
                        + "Press V to toggle the rendering of the vector field: " + (showVecField ? "ON" : "OFF") + "\n"
                        + "Press M to toggle wind-tunnel mode: " + ((STEPS.bndmode == 1) ? "ON" : "OFF") + "\n"
                        + "Press N to enable toggle density conservation: " + (s.norm ? "ON" : "OFF") + "\n"
                        + "Press B to cycle through rigig body drawing options\n"
                        + "Press 1/2 to switch between initial states\n"
                        + "Press +/- to toggle increase/decrease permanent source: " + (splus ? "ON/" : "OFF/") + (smin ? "ON" : "OFF");
            } else {
                s2 = "Hold C to show controls";
            }
            return s2 + "\nfps: " + fps + "\nTotal mass in system: " + s.rho.mass;

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
                for (RigidBody body : s.rbodies.bodies) {
                    if (polymap.containsKey(body) && polymap.get(body).contains(e.getPoint())) {
                        dragging = body;
                        double intx = (double) e.getPoint().x / this.getWidth() - 1d / (2 * s.N);
                        double inty = (double) e.getPoint().y / this.getHeight() - 1d / (2 * s.N);
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
                    break;
                case KeyEvent.VK_N:
                    s.norm = !s.norm;
                    break;
                case KeyEvent.VK_MINUS:
                    smin = !smin;
                    if (splus && smin) {
                        splus = false;
                    }
                    break;
                case KeyEvent.VK_EQUALS:
                case KeyEvent.VK_PLUS:
                    splus = !splus;
                    if (smin && splus) {
                        smin = false;
                    }
                    break;
                case KeyEvent.VK_C:
                    cdown = true;
                    break;
                case KeyEvent.VK_I:
                    images = true;
                    break;
                case KeyEvent.VK_B:
                    drawRigidBounds = (byte) ((drawRigidBounds + 1) % 4);
                    break;
                case KeyEvent.VK_1:
                    s = INITIALS.GRAVITY();
                    break;
                case KeyEvent.VK_2:
                    s = INITIALS.FAN();
                    break;
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
                case KeyEvent.VK_C:
                    cdown = false;
                    break;case KeyEvent.VK_I:
                    images = false;
                    break;
                default:
                    break;
            }
        }

    }
}
