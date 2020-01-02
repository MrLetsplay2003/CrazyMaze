package me.mrletsplay.crazymaze.main;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

public class Maze3D{
	
	public int size;
	public int layers;
	public int scale;
	public int x, y, z;
	public int wS;
	public int sc;
	public static int perTick = 10;
	public static int perStep = 1;
	private static Cell[][][] grid;
	private ArrayList<DoubleInt> last;
	public double i = 0;
	public boolean powerups;
	
	private int currX = 0, currY = 0, currZ = 0;
	public static int wallHeight = 3;
	
	public static int finishX;
	public static int finishY;
	
	private long seed = new Random().nextLong();
	//private int seed = 0;
	private Random r = new Random(seed);
	
	private boolean finished = false;
	public List<BukkitTask> tasks;
	
	private MazeSolver3D solver;

	public Material
		field,
		walls,
		between;
	
	public byte
		fieldD,
		wallD,
		betD;
	
	public Maze3D(Material field, byte fieldD, Material walls, byte wallD, Material between, byte betD) {
		this.field = field;
		this.fieldD = fieldD;
		this.walls = walls;
		this.wallD = wallD;
		this.between = between;
		this.betD = betD;
	}
	
	public void init(int xP, int yP, int zP, int size, int wWidth, int scl, int layers, boolean powerups, Runnable fin, Runnable pfin, List<BukkitTask> tasks) {
		//i = 0;
		this.size = size;
		this.x = xP;
		this.y = yP;
		this.z = zP;
		this.layers = layers;
		this.powerups = powerups;
		this.tasks = tasks;
		wS = wWidth;
		scale = scl;
		sc = scale+wS;
		grid = new Cell[size][size][layers];
		//Timer t = new Timer();
		//t.schedule(new EveryTickEvent(this), 10, 10);
		solver = null;
		finished = false;
		last = new ArrayList<DoubleInt>();
		finishX = size-1;
		finishY = size-1;
		currX=0;
		currY=0;
		//scale = 1000/size;
		last.add(new DoubleInt(currX, currY));
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				for(int z = 0; z < layers; z++){
					grid[x][y][z] = new Cell();
				}
			}
		}
		generate(fin, pfin);
	}
	
	public double map(double val, int min, int max, int min2, int max2){
		double p1 = val/max;
		double toR = (double)max2*p1;
		return toR;
	}

	public void generate(Runnable fin, Runnable pfin) {
		while(!finished){
			for(int i=0; i<perStep; i++){
				step();
			}
		}
		if(solver==null){
			solver = new MazeSolver3D(grid, this, currZ);
			solver.solve(fin, pfin);
		}else{
			solver.solve(fin, pfin);
		}
	}
	
	private void step(){
		String[] neighbors = getNeighbors(currX, currY, currZ);
		if(neighbors.length > 0){
			String dir = neighbors[r.nextInt(neighbors.length)];
			if(dir.equals("UP")){
				int nX = currX;
				int nY = currY-1;
				grid[currX][currY][currZ].setWallUp(false);
				grid[nX][nY][currZ].setWallDown(false);
				grid[nX][nY][currZ].setVisited(true);
				currX = nX;
				currY = nY;
				last.add(new DoubleInt(nX, nY));
			}else if(dir.equals("DOWN")){
				int nX = currX;
				int nY = currY+1;
				grid[currX][currY][currZ].setWallDown(false);
				grid[nX][nY][currZ].setWallUp(false);
				grid[nX][nY][currZ].setVisited(true);
				currX = nX;
				currY = nY;
				last.add(new DoubleInt(nX, nY));
			}else if(dir.equals("LEFT")){
				int nX = currX-1;
				int nY = currY;
				grid[currX][currY][currZ].setWallLeft(false);
				grid[nX][nY][currZ].setWallRight(false);
				grid[nX][nY][currZ].setVisited(true);
				currX = nX;
				currY = nY;
				last.add(new DoubleInt(nX, nY));
			}else if(dir.equals("RIGHT")){
				int nX = currX+1;
				int nY = currY;
				grid[currX][currY][currZ].setWallRight(false);
				grid[nX][nY][currZ].setWallLeft(false);
				grid[nX][nY][currZ].setVisited(true);
				currX = nX;
				currY = nY;
				last.add(new DoubleInt(nX, nY));
			}
		}else{
			if(last.size() > 0){
				/*Cell c = grid[currX][currY];
				c.ignore();
				*/
				DoubleInt lastC = last.get(last.size()-1);
				last.remove(last.size()-1);
				int nX = lastC.getInt1();
				int nY = lastC.getInt2();
				currX = nX;
				currY = nY;
			}else{
				//Finished
				if(!finished){
					finished = true;
					//System.exit(0);
				}
			}
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public void save(Runnable fin, Runnable pfin){
		fill(this.x-wS,this.y+(currZ*(wallHeight+1)),this.z-wS,size*sc+wS,wallHeight,size*sc+wS,Material.AIR,Config.cmWorld,10);
		
		for(int x = this.x-wS; x < size*sc+wS-1; x++) {
			fill(x,this.y-1+(currZ*(wallHeight+1)),this.z-wS,1,1,size*sc+wS, between, Config.cmWorld, betD, 0.1);
		}
		
		i+=10/Config.genSpeed;
		Random r = new Random();
		
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Material m = field;
				byte b = fieldD;
				if(powerups && r.nextDouble()<=Config.fieldChance) {
					PowerupField field = PowerupField.values()[r.nextInt(PowerupField.values().length)];
					m = field.material;
					b = field.data;
				}
				fill(this.x+x*sc, this.z+y*sc, scale, scale, this.y-1+(currZ*(wallHeight+1)), m, Config.cmWorld, b, 0.02);
			}
		}
		
		//TODO: Draw path
		//solver.draw(w, this.x, this.y+(currZ*(wallHeight+1)), this.z);
		
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				final int x2 = x;
				final int y2 = y;
				Cell c = Maze3D.grid[x2][y2][currZ];
				if(c.isWallUp()){
					fill(this.x+(x2*sc-wS), this.y+(currZ*(wallHeight+1)), this.z+(y2*sc-wS), sc+wS, wallHeight, wS, walls, Config.cmWorld, wallD, 0.02);
				}
				
				if(c.isWallDown()){
					fill(this.x+(x2*sc-wS), this.y+(currZ*(wallHeight+1)), this.z+(y2*sc+sc-wS), sc+wS, wallHeight, wS, walls, Config.cmWorld, wallD, 0.02);
				}
				
				if(c.isWallLeft()){
					fill(this.x+(x2*sc-wS), this.y+(currZ*(wallHeight+1)), this.z+(y2*sc-wS), wS, wallHeight, sc+wS, walls, Config.cmWorld, wallD, 0.02);
				}
				
				if(c.isWallRight()){
					fill(this.x+(x2*sc+sc-wS), this.y+(currZ*(wallHeight+1)), this.z+(y2*sc-wS), wS, wallHeight, sc+wS, walls, Config.cmWorld, wallD, 0.02);
				}
			}
		}
		//i = 0;
		if(currZ==0){
			//fill(this.x-wS, this.y, this.z, scale, wallHeight, scale, Material.AIR, w);
		}else if(currZ%2==0){
			//fill(1, 10+(currZ*(wallHeight+1)), 0, scale, 1, scale, Material.AIR, w);
			fill(this.x, this.y+((currZ-1)*(wallHeight+1)), this.z, 1, wallHeight+1, scale, Material.LADDER, Config.cmWorld, (byte)5, 1);
		}else{
			//fill((size-1)*sc, 10+(currZ*(wallHeight+1)), (size-1)*sc, scale, 1, scale, Material.AIR, w);
			fill(this.x+((size-1)*sc)+scale-1, this.y+((currZ-1)*(wallHeight+1)), this.z+(size-1)*sc, 1, wallHeight+1, scale, Material.LADDER, Config.cmWorld, (byte)4, 1);
		}
		if(currZ == layers-1) {
			final int cZ = currZ;
			tasks.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
				Block s;
				if(cZ%2==0) {
					s = new Location(Config.cmWorld, this.x+((size-1)*sc)+scale-1, this.y+(cZ*(wallHeight+1)), this.z+(size-1)*sc).getBlock();
					s.setType(Material.SIGN_POST);
					s.setData((byte) 4);
				}else {
					s = new Location(Config.cmWorld, this.x, this.y+(cZ*(wallHeight+1)), this.z).getBlock();
					s.setType(Material.SIGN_POST);
					s.setData((byte) 12);
				}
				Sign ss = (Sign) s.getState();
				ss.setLine(0, Config.signLayout0);
				ss.setLine(1, Config.signLayout1_2);
				ss.update();
				if(fin!=null) fin.run();
			}, (long) (i+10)));
			if(pfin!=null) pfin.run();
		}
		currZ++;
		if(currZ<layers){
			init(x, y, z, size, wS, scale, layers, powerups, fin, pfin, tasks);
		}else {
			for(int x = this.x-wS; x < size*sc+wS-1; x++) {
				fill(x,this.y+(currZ*wallHeight),this.z-wS,1,1,size*sc+wS,Material.BARRIER,Config.cmWorld, 0.1);
			}
		}
	}
	
	public void fill(int x1, int z1, int width, int height, int y, Material m, World w, double delay){
		i+=delay/Config.genSpeed;
		tasks.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
			for(int x = x1; x < x1+width; x++){
				for(int z = z1; z < z1+height; z++){
					final int x2 = x;
					final int y2 = y;
					final int z2 = z;
					Block b = w.getBlockAt(x2, y2, z2);
					if(!b.getType().equals(m)){
						b.setType(m);
					}
				}
			}
		}, (long) i));
	}
	
	@SuppressWarnings("deprecation")
	public void fill(int x1, int z1, int width, int height, int y, Material m, World w, byte dat, double delay){
		i+=delay/Config.genSpeed;
		tasks.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
			for(int x = x1; x < x1+width; x++){
				for(int z = z1; z < z1+height; z++){
					final int x2 = x;
					final int y2 = y;
					final int z2 = z;
					Block b = w.getBlockAt(x2, y2, z2);
					if(!b.getType().equals(m)){
						b.setType(m);
						b.setData(dat);
					}
				}
			}
		}, (long) i));
	}
	
	public void fill(int x1, int y1, int z1, int width, int height, int length, Material m, World w, double delay){
		fill(x1, y1, z1, width, height, length, m, w, delay, null);
	}
	
	public void fill(int x1, int y1, int z1, int width, int height, int length, Material m, World w, double delay, Runnable fin){
		i += delay/Config.genSpeed;
		tasks.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
			fill_r(x1, y1, z1, width, height, length, m, w, fin);
		}, (long) i));
	}
	
	public void fill_r(int x1, int y1, int z1, int width, int height, int length, Material m, World w, Runnable fin){
		for(int x = x1; x < x1+width; x++){
			for(int z = z1; z < z1+length; z++){
				for(int y = y1+height-1; y > y1-1; y--){ 
					final int x2 = x;
					final int y2 = y;
					final int z2 = z;
					Block b = w.getBlockAt(x2, y2, z2);
					if(!b.getType().equals(m)){
						b.setType(m);
					}
				}
			}
		}
		if(fin!=null) fin.run();
	}
	
	@SuppressWarnings("deprecation")
	public void fill(int x1, int y1, int z1, int width, int height, int length, Material m, World w, byte data, double delay){
		i+=delay/Config.genSpeed;
		tasks.add(Bukkit.getScheduler().runTaskLater(Main.pl, () -> {
			for(int x = x1; x < x1+width; x++){
				for(int z = z1; z < z1+length; z++){
					for(int y = y1+height-1; y > y1-1; y--){ 
						final int x2 = x;
						final int y2 = y;
						final int z2 = z;
						Block b = w.getBlockAt(x2, y2, z2);
						b.setType(m);
						b.setData(data);
					}
				}
			}
		}, (long) i));
	}
	
	private String[] getNeighbors(int x, int y, int z){
		ArrayList<String> list = new ArrayList<String>();
		if(x > 0){
			if(!grid[x-1][y][z].isVisited()){
				list.add("LEFT");
			}
		}
		if(y > 0){
			if(!grid[x][y-1][z].isVisited()){
				list.add("UP");
			}
		}
		if(x < size-1){
			if(!grid[x+1][y][z].isVisited()){
				list.add("RIGHT");
			}
		}
		if(y < size-1){
			if(!grid[x][y+1][z].isVisited()){
				list.add("DOWN");
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
}
