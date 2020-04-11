package me.mrletsplay.crazymaze.maze;

import java.util.EnumSet;

public class MazeCell {
	
	private MazeLayer layer;
	
	private int
		x,
		y;
	
	private EnumSet<MazeDirection> walls;
	
	public MazeCell(MazeLayer layer, int x, int y) {
		this.layer = layer;
		this.x = x;
		this.y = y;
		this.walls = EnumSet.allOf(MazeDirection.class);
	}
	
	public MazeLayer getLayer() {
		return layer;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void removeWall(MazeDirection wall) {
		walls.remove(wall);
	}
	
	public boolean hasWall(MazeDirection dir) {
		return walls.contains(dir);
	}
	
	public EnumSet<MazeDirection> getWalls() {
		return walls;
	}
	
	public MazeCell getRelative(MazeDirection dir) {
		switch (dir) {
			case UP:
				return layer.getCell(x, y - 1);
			case DOWN:
				return layer.getCell(x, y + 1);
			case LEFT:
				return layer.getCell(x - 1, y);
			case RIGHT:
				return layer.getCell(x + 1, y);
			default:
				throw new IllegalArgumentException();
		}
	}
	
	@Override
	public String toString() {
		return String.format("[%s/%s]", x, y);
	}

}
