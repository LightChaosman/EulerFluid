package rigids;

/**
 * Simple datatype, that for a given cell can store location and speed.
 * Hashcode modified so it hashes on i & j, assuming 0<= i,j<= 10000.
 *
 * @author Helmond
 */
public class OccupiedCell {
    
    public int i;
    public int j;
    public double vx;
    public double vy;

    public OccupiedCell(int i, int j, double vx, double vy) {
        this.i = i;
        this.j = j;
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public int hashCode() {
        return i*100000+j; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OccupiedCell other = (OccupiedCell) obj;
        if (this.i != other.i) {
            return false;
        }
        if (this.j != other.j) {
            return false;
        }
        return true;
    }
    
    
    
}
