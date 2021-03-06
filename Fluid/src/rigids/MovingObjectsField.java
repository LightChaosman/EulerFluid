package rigids;

import java.util.HashSet;

/**
 * The analog of staticobjectfield. This one handles the boundaries of the rigid bodies.
 *
 * @author Helmond
 */
public class MovingObjectsField {

    public static final byte E = 0;//empty cell
    public static final byte T = 1;//top open
    public static final byte TR = 2;//top and right open
    public static final byte R = 3;//right open
    public static final byte BR = 4;//bottom and right open
    public static final byte B = 5;//bottom open
    public static final byte BL = 6;//bottom and left open
    public static final byte L = 7;//left open
    public static final byte TL = 8;//top and left open
    public static final byte FTR = 9;//nothing open, topright open
    public static final byte FBR = 10;//nothing open, bottomright open
    public static final byte FBL = 11;//nothing open, bottomleft  open
    public static final byte FTL = 12;//nothing open, topright open
    public static final byte FTLBR = 13;//nothing open, topleft&bottomright open
    public static final byte FTRBL = 14;//nothing open, topright&bottomleft open
    public static final byte FF = 15;//nothing open
    private static final byte S = 16;//special, internal value

    public OccupiedCell[][] ocs2;
    public byte[][] ocs;
    public OccupiedCell[][] cells;
    private final HashSet<OccupiedCell> filledLocations = new HashSet<>();//contains the cells that have at least 1 free neightbour
    private final HashSet<OccupiedCell> fullLocations = new HashSet<>();//contains the cells that have no free neighbours

    private final int N;

    public MovingObjectsField(int size, boolean addBounds) {
        size = size + (addBounds ? 2 : 0);
        this.N = size - 2;
        this.ocs = new byte[size][size];
        this.cells = new OccupiedCell[size][size];
        for (int i = 0; i < N + 2; i++) {
            this.ocs[0][i] = S;
            this.ocs[i][0] = S;
            this.ocs[i][N + 1] = S;
            this.ocs[N + 1][i] = S;
        }

    }

    public void updateField(RigidBody[] bodies) {
        filledLocations.clear();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                ocs[i][j] = E;
                cells[i][j] = null;
            }
        }
        for (RigidBody b : bodies) {
            for (OccupiedCell c : b.getOccupiedCells()) {
                ocs[c.i][c.j] = S;
                cells[c.i][c.j] = c;
            }
        }
        relabelGrid();
    }

    /**
     * Sets the values of the grid to the correct values. Not the most effective
     * method, but this is the least spaghetti-like solution
     */
    private void relabelGrid() {
        filledLocations.clear();
        fullLocations.clear();
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (ocs[i][j] != E) {
                    relabelCell(i, j);
                }
            }
        }

    }

    private void relabelCell(int i, int j) {

        assert i >= 1 && i <= N && j >= 1 && j <= N;
        boolean t = ocs[i][j + 1] == E,
                tr = ocs[i + 1][j + 1] == E,
                r = ocs[i + 1][j] == E,
                br = ocs[i + 1][j - 1] == E,
                b = ocs[i][j - 1] == E,
                bl = ocs[i - 1][j - 1] == E,
                l = ocs[i - 1][j] == E,
                tl = ocs[i - 1][j + 1] == E;
        byte val;
        if (t) {
            if (r) {
                val = TR;
            } else if (l) {
                val = TL;
            } else {
                val = T;
            }
        } else if (b) {
            if (r) {
                val = BR;
            } else if (l) {
                val = BL;
            } else {
                val = B;
            }
        } else if (l) {
            val = L;
        } else if (r) {
            val = R;
        } else //all 4 direct neighbours are occupied
        {
            fullLocations.add(cells[i][j]);
            if (tr) {
                if (bl) {
                    val = FTRBL;
                } else {
                    val = FTR;
                }
            } else if (tl) {
                if (br) {
                    val = FTLBR;
                } else {
                    val = FTL;
                }
            } else if (bl) {
                val = FBL;
            } else if (br) {
                val = FBR;
            } else {
                val = FF;
            }
        }
        if (!fullLocations.contains(cells[i][j])) {
            filledLocations.add(cells[i][j]);
        }

        ocs[i][j] = val;
    }

    public void setBnd(double[][] x, int b) {
        for (OccupiedCell c : this.filledLocations) {
            if(b==3){setBND(c.i, c.j, x, b);}else{x[c.i][c.j]=0;}
            
        }
        for (OccupiedCell c : this.fullLocations) {
            if(b==3){setBND2(c.i, c.j, x, b);}else{x[c.i][c.j]=0;}
            
        }
        if (b == 1) {
            addXSpeed(x);
        }
        if (b == 2) {
            addYSpeed(x);
        }
    }

    private void setBND2(int i, int j, double[][] x, int b) {
        byte state = this.ocs[i][j];
        switch (state) {
            case (FTR):
                x[i][j] = (x[i][j + 1] + x[i + 1][j]) / 2d;
                break;
            case (FBR):
                x[i][j] = (x[i][j - 1] + x[i + 1][j]) / 2d;
                break;
            case (FBL):
                x[i][j] = (x[i][j - 1] + x[i - 1][j]) / 2d;
                break;
            case (FTL):
                x[i][j] = (x[i][j + 1] + x[i - 1][j]) / 2d;
                break;
            case (FTLBR):
            //notice: no break -> fall through
            case (FTRBL):
                x[i][j] = (x[i][j + 1] + x[i][j - 1] + x[i + 1][j] + x[i - 1][j]) / 4d;
                break;
            case (FF):
                x[i][j] = 0;
                break;
            default:
                assert false : state;
        }
    }

    private void setBND(int i, int j, double[][] x, int b) {
        byte state = this.ocs[i][j];
        double avg;
        switch (state) {
            case (T):
                x[i][j] = (b == 2 ? -x[i][j + 1] : x[i][j + 1]);
                break;
            case (TR):
                avg = (x[i][j + 1] + x[i + 1][j]) / 2d;
                x[i][j] = (b == 1 || b == 2) ? -avg : avg;
                break;
            case (R):
                x[i][j] = (b == 1 ? -x[i + 1][j] : x[i + 1][j]);
                break;
            case (BR):
                avg = (x[i][j - 1] + x[i + 1][j]) / 2d;
                x[i][j] = (b == 1 || b == 2) ? -avg : avg;
                break;
            case (B):
                x[i][j] = (b == 2 ? -x[i][j - 1] : x[i][j - 1]);
                break;
            case (BL):
                avg = (x[i][j - 1] + x[i - 1][j]) / 2d;
                x[i][j] = (b == 1 || b == 2) ? -avg : avg;
                break;
            case (L):
                x[i][j] = (b == 1 ? -x[i - 1][j] : x[i - 1][j]);
                break;
            case (TL):
                avg = (x[i][j + 1] + x[i - 1][j]) / 2d;
                x[i][j] = (b == 1 || b == 2) ? -avg : avg;
                break;

            default:
                assert false : state;
        }
    }

    public double[] semiLang(int i, int j, double u, double v, double dt0) {//http://www.cse.yorku.ca/~amana/research/grid.pdf

        double x = i - dt0 * u;
        double y = j - dt0 * v;//If the loop does not detect collision, this will be the result

        v = -v * dt0;
        u = -u * dt0;//inverse the speed...
        int X = i;
        int Y = j;

        //looking for intersections s.t. X+tu = 1/2+k, 0<=t<=1, similar for Y+tv
        int stepX = (int) Math.signum(u);
        int stepY = (int) Math.signum(v);
        double tMaxX = Math.abs(1d / (2 * u));//Because we know we start at the center of a cell.. solve i+ut = i+1 or i-1 -> 
        double tMaxY = Math.abs(1d / (2 * v));
        double tDeltaX = 1 / Math.abs(u);
        double tDeltaY = 1 / Math.abs(v);
        double t = 1;
        while (X >= 1 && Y >= 1 && X <= N && Y <= N) {
            if (tMaxX < tMaxY) {

                if (tMaxX > 1) {
                    break;
                }

                X = X + stepX;
                if (ocs[X][Y] != E) {
                    x = i + tMaxX * u;
                    y = j + tMaxX * v;
                    t = tMaxX;
                    break;
                }
                tMaxX = tMaxX + tDeltaX;
            } else {
                if (tMaxY > 1) {
                    break;
                }

                Y = Y + stepY;
                if (ocs[X][Y] != E) {
                    x = i + tMaxY * u;
                    y = j + tMaxY * v;
                    t = tMaxY;
                    break;
                }
                tMaxY = tMaxY + tDeltaY;
            }

        }

        return new double[]{x, y,t};
    }

    private void addXSpeed(double[][] x) {
        for(OccupiedCell c:filledLocations)
        {
            x[c.i][c.j]+=c.vx;
        }for(OccupiedCell c:fullLocations)
        {
            x[c.i][c.j]+=c.vy;
        }
    }

    private void addYSpeed(double[][] x) {
        for(OccupiedCell c:filledLocations)
        {
            x[c.i][c.j]+=c.vy;
        }for(OccupiedCell c:fullLocations)
        {
            x[c.i][c.j]+=c.vy;
        }
    }

}
