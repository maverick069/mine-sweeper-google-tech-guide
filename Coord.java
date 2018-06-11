package game;

/**
 *
 * @author Madhusudan Gopanna
 */
public class Coord{

    private int x=-1;
    private int y=-1;
    
    public Coord(int x, int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Coord o) {
        return x==o.getX() && y==o.getY();
    }
    
    public boolean equals(int x, int y) {
        return this.x==x && this.y==y;
    }
    
    @Override
    public String toString(){
        return x+","+y;
    }
}
