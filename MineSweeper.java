package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Madhusudan Gopanna
 */
public class MineSweeper {
    private int cellsLeft;
    private final int rows, cols;
    private final int[][] grid;
    private final boolean[][] show;
    private final List<List<Coord>> groups;
    
    public MineSweeper(int rows, int cols){
        this.rows=rows;
        this.cols=cols;
        this.grid=new int[rows][cols];
        this.show=new boolean[rows][cols];
        this.groups=new ArrayList<>();
        cellsLeft=rows*cols;
        initGame();
    }
    
    private boolean canUpdate(int x, int y){
        return x>=0 && x<rows && y>=0 && y<cols && grid[x][y]<9;
    }
    
    private void update(int x, int y){
        if(canUpdate(x,y)){
            grid[x][y]++;
        }
    }
    
    private void updateAround(int x, int y){
        for(int i=x-1;i<x+2;i++){
            for(int j=y-1;j<y+2;j++){
                if(!(x==i && y==j)){
                    update(i,j);
                }
            }
        }
    }
    
    private int mine(int currentVal){
        return Math.random()<0.5?9:currentVal;
    }
    
    private void initGame(){
        for(int x=0;x<rows;x++){
            for(int y=0;y<cols;y++){
                grid[x][y]=mine(grid[x][y]);
                show[x][y]=false;
                if(grid[x][y]==9){
                    cellsLeft--;
                    updateAround(x,y);
                }else{
                    List<Coord> neighList = getPredNeigbors(x,y);
                    Iterator<List<Coord>> grpIter = groups.iterator();
                    boolean once=false;
                    boolean found;
                    while(grpIter.hasNext()){
                        found=false;
                        List<Coord> emptyCells = grpIter.next();
                        Iterator<Coord> eListIter = emptyCells.iterator();
                        while(!found && eListIter.hasNext()){
                            Coord empty = eListIter.next();
                            Iterator<Coord> nListIter = neighList.iterator();
                            while(!found && nListIter.hasNext()){
                                Coord neigh = nListIter.next();
                                if(empty.equals(neigh)){
                                    emptyCells.add(new Coord(x,y));
                                    found=true;
                                    once=true;
                                }
                            }
                        }
                    }
                    if(!once){
                        //displayGrps();
                        List<Coord> newList = new ArrayList<>();
                        newList.add(new Coord(x,y));
                        groups.add(newList);
                        //displayGrps();
                    }
                }
            }
        }
        //displayGrps();
    }
    
    private void displayGrps(){
        Iterator<List<Coord>> grpIter = groups.iterator();
        int i=0;
        String row;
        while(grpIter.hasNext()){
            row="i:"+i+" ";
            List<Coord> emptyCells = grpIter.next();
            Iterator<Coord> iter = emptyCells.iterator();
            while(iter.hasNext()){
                row+=iter.next()+" ";
            }
            i++;
            System.out.println(row);
        }
    }
    
    private String canShow(boolean override, int x, int y){
        return show[x][y] || override?String.valueOf(grid[x][y]):".";
    }
    
    private void uncoverNearbyEmptyCells(int x, int y){
        Iterator<List<Coord>> grpIter = groups.iterator();
        List<Coord> group;
        Iterator<Coord> iter;
        while(grpIter.hasNext()){
            group = grpIter.next();
            Iterator<Coord> listIter = group.iterator();
            while(listIter.hasNext()){
                Coord frmGrp = listIter.next();
                if(frmGrp.equals(x,y)){
                    iter = group.iterator();
                    while(iter.hasNext()){
                        uncover(iter.next());
                    }
                    break;
                }
            }
        }
    }
    
    private void uncover(Coord coord){
        if(!show[coord.getX()][coord.getY()]){
            show[coord.getX()][coord.getY()]=true;
            cellsLeft--;
        }
    }
    
    private void display(boolean override){
        String header="#|";
        for(int y=0;y<cols;y++){
            header+=y+"|";
        }
        System.out.println(header);
        for(int x=0;x<rows;x++){
            String row=x+"|";
            for(int y=0;y<cols;y++){
                row+=canShow(override,x,y)+" ";
            }
            System.out.println(row);
        }
    }

    private List<Coord> getPredNeigbors(int x, int y) {
        List<Coord> coords = new ArrayList<>();
        if(x>0 && y>0){
            coords.add(new Coord(x-1,y-1));
        }
        if(x>0){
            coords.add(new Coord(x-1,y));
        }
        if(x>0 && y+1<cols){
            coords.add(new Coord(x-1,y+1));
        }
        if(y>0){
            coords.add(new Coord(x,y-1));
        }
        return coords;
    }
    
    public void play(Scanner scanner){
        boolean foul;
        String cellChoice;
        int x, y, pos;
        do{
            foul=false;
            System.out.println("Enter cell coordinate <x y>:");
            cellChoice = scanner.nextLine();
            pos=cellChoice.indexOf(" ");
            if(pos>0){
                try{
                    x = Integer.parseInt(cellChoice.substring(0, pos).trim());
                    y = Integer.parseInt(cellChoice.substring(pos+1).trim());
                    if(!(x>=0 && x<rows && y>=0 && y<cols)){
                        System.out.println("Error: Please enter valid coordinates of the form <x y>.");
                        foul=true;
                    }else if(!show[x][y]){
                        if(grid[x][y] == 9){
                            System.out.println("Boom! You have struck a mine!");
                        }else{
                            uncoverNearbyEmptyCells(x,y);
                            display(false);
                            foul=true;
                        }
                    }else{
                        System.out.println("Error: Please pick a hidden cell and try again.");
                        foul=true;
                    }
                }catch(NumberFormatException ex){
                    System.out.println("Error: Please enter valid coordinates of the form <x y>.");
                    foul=true;
                }
            }else{
                System.out.println("Error: Please use space to separate the coordinates of the form <x y>.");
                foul=true;
            }
            System.out.println("Cells Left:"+cellsLeft);
        }while(cellsLeft > 0 && foul);
        if(cellsLeft==0){
            System.out.println("You won!");
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continu=true;
        int rows, cols;
        do{
            try{
                System.out.println("Enter number of rows:");
                rows = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter number of columns:");
                cols = Integer.parseInt(scanner.nextLine());
                MineSweeper ms = new MineSweeper(rows, cols);
                ms.display(false);
                ms.play(scanner);
                System.out.println("Continue playing Y/N?");
                continu = scanner.nextLine().equalsIgnoreCase("y");  
            }catch(NumberFormatException ex){
                System.out.println("Error: Please enter a valid number and try again.");
            }
        }while(continu);
    }
}
