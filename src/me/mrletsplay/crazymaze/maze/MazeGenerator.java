package me.mrletsplay.crazymaze.maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
	
	private Random random;
	private double holeyness;
	private MazeCell currentCell;
	private List<MazeCell> visited;
	private Deque<MazeCell> path;
	
	public MazeGenerator(double holeyness) {
		this.random = new Random();
		this.holeyness = holeyness;
		reset();
	}
	
	private void reset() {
		this.path = new ArrayDeque<>();
		this.visited = new ArrayList<>();
	}
	
	public MazeGenerator() {
		this(0);
	}
	
	public List<MazeCell> populateLayer(MazeLayer layer, MazeCell start, MazeCell end) {
		List<MazeCell> pathToFinish = null;
		
		currentCell = start;
		visited.add(start);
		path.add(start);
		
		while(!path.isEmpty()) {
			if(!takeRandomStep()) {
				// Take a step back
				path.removeLast();
				if(path.isEmpty()) break;
				currentCell = path.getLast();
			}
			
			if(pathToFinish == null && currentCell.equals(end)) {
				pathToFinish = new ArrayList<>(path);
			}
		}
		
		MazeCell one = layer.getCell(layer.getSizeX() - 1, 0);
		one.removeWall(MazeDirection.DOWN);
		one.getRelative(MazeDirection.DOWN).removeWall(MazeDirection.UP);
		one.removeWall(MazeDirection.LEFT);
		one.getRelative(MazeDirection.LEFT).removeWall(MazeDirection.RIGHT);
		
		MazeCell two = layer.getCell(0, layer.getSizeY() - 1);
		two.removeWall(MazeDirection.UP);
		two.getRelative(MazeDirection.UP).removeWall(MazeDirection.DOWN);
		two.removeWall(MazeDirection.RIGHT);
		two.getRelative(MazeDirection.RIGHT).removeWall(MazeDirection.LEFT);
		
		reset();
		return pathToFinish;
	}
	
	private boolean takeRandomStep() {
		List<MazeDirection> dirs = getWalkDirections();
		if(dirs.isEmpty()) return false;
		
		MazeDirection dir = dirs.get(random.nextInt(dirs.size()));
		
		currentCell.removeWall(dir);
		currentCell = currentCell.getRelative(dir);
		
		visited.add(currentCell);
		path.add(currentCell);
		
		if(holeyness != 0 && random.nextDouble() < holeyness) {
			// Remove a random wall
			MazeDirection rDir = MazeDirection.values()[random.nextInt(MazeDirection.values().length)];
			MazeCell o = currentCell.getRelative(rDir);
			if(o != null) {
				currentCell.removeWall(rDir);
				o.removeWall(rDir.getOpposite());
			}
		}
		
		currentCell.removeWall(dir.getOpposite());
		return true;
	}
	
	private List<MazeDirection> getWalkDirections() {
		List<MazeDirection> dirs = new ArrayList<>();
		for(MazeDirection dir : MazeDirection.values()) {
			MazeCell o = currentCell.getRelative(dir);
			if(o == null || visited.contains(o)) continue;
			dirs.add(dir);
		}
		return dirs;
	}

}
