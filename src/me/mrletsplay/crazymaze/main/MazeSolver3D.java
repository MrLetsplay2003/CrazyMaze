package me.mrletsplay.crazymaze.main;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;

public class MazeSolver3D {

	private int scale, sc;
	private Cell[][][] grid;
	private ArrayList<DoubleInt> last;
	
	public int currX = 0;
	public int currY = 0;
	public int currZ = 0;
	private int finishX;
	private int finishY;
	private int size;
	
	
	public boolean finished = false;
	
	
	private int seed = new Random().nextInt();
	private Random r = new Random(seed);
	
	private Maze3D panel;
	
	public MazeSolver3D(Cell[][][] grid, Maze3D p, int layer) {
		this.grid = grid;
		this.sc = p.sc;
		this.scale = p.scale;
		this.size = p.size;
		panel = p;
		finishX = Maze3D.finishX;
		finishY = Maze3D.finishY;
		currZ = layer;
		last = new ArrayList<DoubleInt>();
		last.add(new DoubleInt(currX, currY));
	}

	public void draw(World w, int x, int y, int z){
		for(int i = 0; i < last.size(); i++){
			DoubleInt l = last.get(i);
			panel.fill(x+l.getInt1()*sc, z+l.getInt2()*sc, scale, scale, y-1, Material.CLAY, w, 0.02);
		}
	}
	
	public double map(double val, int min, int max, int min2, int max2){
		double p1 = val/max;
		double toR = (double)max2*p1;
		return toR;
	}
	
	public void solve(Runnable fin, Runnable pfin){
		while(!finished){
			String[] neighbors = getNeighbors(currX, currY, currZ);
			grid[currX][currY][currZ].setVisited2(true);
			if(neighbors.length > 0){
				String dir = neighbors[r.nextInt(neighbors.length)];
				int nX = 0;
				int nY = 0;
				if(dir.equals("UP")){
					nX = currX;
					nY = currY-1;
					currX = nX;
					currY = nY;
				}else if(dir.equals("DOWN")){
					nX = currX;
					nY = currY+1;
					currX = nX;
					currY = nY;
				}else if(dir.equals("LEFT")){
					nX = currX-1;
					nY = currY;
					currX = nX;
					currY = nY;
				}else if(dir.equals("RIGHT")){
					nX = currX+1;
					nY = currY;
					currX = nX;
					currY = nY;
				}
				last.add(new DoubleInt(currX, currY));
				if(currX==finishX && currY==finishY ){
					finish(fin, pfin);
				}
			}else{
				if(last.size()>0 && (currX!=finishX || currY!=finishY)){
					//System.out.println("CASE 1");
					/*Cell c = grid[currX][currY];
					c.ignore();
					*/
					DoubleInt lastC = last.get(last.size()-1);
					int nX = lastC.getInt1();
					int nY = lastC.getInt2();
					last.remove(last.size()-1);
					currX = nX;
					currY = nY;
					if(getNeighbors(currX, currY, currZ).length>0){
						last.add(new DoubleInt(currX, currY));
					}
				}else if(last.size() == 0){
					//System.out.println("CASE 2");
//					System.out.println("Couldn't find path");
					//finish();
				}else if(currX==finishX && currY == finishY){
					//System.out.println("CASE 3");
					//Finished
					finish(fin, pfin);
				}else{
					//System.out.println("CASE 4");
//					System.out.println("ERROR");
//					System.out.println(last.size());
//					System.out.println(currX+"/"+currY+"/"+finishX+"/"+finishY);
				}
			}
		}
	}
	
	private void finish(Runnable fin, Runnable pfin){
		if(!finished){
			finished = true;
			/*for(DoubleInt i : last){
				grid[i.getInt1()][i.getInt2()].ignore();
			}*/
			panel.save(fin, pfin);
//			System.out.println("Finished solving");
			//System.exit(0);
		}
	}
	
	public Cell[][][] getGrid(){
		return grid;
	}
	
	private String[] getNeighbors(int x, int y, int z){
		ArrayList<String> list = new ArrayList<String>();
		if(x > 0){
			if(!grid[x][y][z].isWallLeft() && !grid[x-1][y][z].isVisited2()){
				list.add("LEFT");
			}
		}
		if(y > 0){
			if(!grid[x][y][z].isWallUp() && !grid[x][y-1][z].isVisited2()){
				list.add("UP");
			}
		}
		if(x < size-1){
			if(!grid[x][y][z].isWallRight() && !grid[x+1][y][z].isVisited2()){
				list.add("RIGHT");
			}
		}
		if(y < size-1){
			if(!grid[x][y][z].isWallDown() && !grid[x][y+1][z].isVisited2()){
				list.add("DOWN");
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
}
