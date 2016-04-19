package fluid;


import java.util.HashSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Helmond
 */
public class StaticObjectsField {

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

    public byte[][] ocs;
    private final HashSet<int[]> filledLocations = new HashSet<>();
    private final int N;

    public StaticObjectsField(int size, boolean addBounds) {
        size = size + (addBounds ? 2 : 0);
        this.N = size - 2;
        this.ocs = new byte[size][size];
        for(int i = 0; i < N+2;i++)
        {
            this.ocs[0][i]=S;
            this.ocs[i][0]=S;
            this.ocs[i][N+1]=S;
            this.ocs[N+1][i]=S;
        }

    }

    public void addCell(int x, int y) {
        if(this.ocs[x][y]==0){
        this.filledLocations.add(new int[]{x, y});}
        if(y<N && this.ocs[x][y+1]==0){
        this.filledLocations.add(new int[]{x, y + 1});}
        if(x<N && this.ocs[x+1][y]==0){
        this.filledLocations.add(new int[]{x + 1, y});}
        if(x<N&&y<N && this.ocs[x+1][y+1]==0){
        this.filledLocations.add(new int[]{x + 1, y + 1});}
        this.ocs[x][y] = S;
        this.ocs[x + 1][y] = S;
        this.ocs[x][y + 1] = S;
        this.ocs[x + 1][y + 1] = S;
        
        fixEdgecases(x, y);
        relabelGrid();

    }

    /**
     * Ensures we do not get thin parts
     *
     * @param x
     * @param y
     */
    private void fixEdgecases(int x, int y) {
        if (ocs[x - 1][y - 1] != 0 && ocs[x - 1][y] == 0 && ocs[x][y - 1] == 0) {
            ocs[x - 1][y] = S;
            ocs[x][y - 1] = S;
        }

        if (x != N) {
            if (ocs[x + 2][y - 1] != 0 && ocs[x + 1][y - 1] == 0 && ocs[x + 2][y] == 0) {
                ocs[x + 1][y - 1] = S;
                ocs[x + 2][y] = S;
            }
        }
        if (y != N) {
            if (ocs[x - 1][y + 2] != 0 && ocs[x][y + 2] == 0 && ocs[x - 1][y + 1] == 0) {
                ocs[x][y + 2] = S;
                ocs[x - 1][y + 1] = S;
            }
        }
        if (x != N && y != N) {
            if (ocs[x + 2][y + 2] != 0 && ocs[x + 1][y + 2] == 0 && ocs[x + 2][y + 1] == 0) {
                ocs[x + 1][y + 2] = S;
                ocs[x + 2][y + 1] = S;
            }
        }
    }

    /**
     * Sets the values of the grid to the correct values. Not the most effective
     * method, but this is the least spaghetti-like solution
     */
    private void relabelGrid() {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (ocs[i][j] != 0) {
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
        } else//all 4 direct neighbours are occupied
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
        ocs[i][j] = val;
    }

    public void setBnd(double[][] x, int b) {
        for (int[] cs : this.filledLocations) {
            int i = cs[0];
            int j = cs[1];
            setBND(i, j, x, b);
        }
    }

    private void setBND(int i, int j, double[][] x, int b) {
        byte state = this.ocs[i][j];
        switch (state) {
            case (T):
                x[i][j] = (b==2?-x[i][j+1]:x[i][j+1]);
                break;
            case (TR):
                x[i][j] = (b==1||b==2)?-x[i+1][j+1]:x[i+1][j+1];
                break;
            case (R):
                x[i][j] = (b==1?-x[i+1][j]:x[i+1][j]);
                break;
            case (BR):
                x[i][j] = (b==1||b==2)?-x[i+1][j-1]:x[i+1][j-1];
                break;
            case (B):
                x[i][j] = (b==2?-x[i][j-1]:x[i][j-1]);
                break;
            case (BL):
                x[i][j] = (b==1||b==2)?-x[i-1][j-1]:x[i-1][j-1];
                break;
            case (L):
                x[i][j] = (b==1?-x[i-1][j]:x[i-1][j]);
                break;
            case (TL):
                x[i][j] = (b==1||b==2)?-x[i-1][j+1]:x[i-1][j+1];
                break;//TODO something clever here...altough we don't seem to come here often...
            case (FTR):
                break;
            case (FBR):
                break;
            case (FBL):
                break;
            case (FTL):
                break;
            case (FTLBR):
                break;
            case (FTRBL):
                break;
            case (FF):
                x[i][j]=0;
                break;
            default: assert false;
        }
    }

}
