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
	
	public boolean coordinatesEqual(int x, int y) {
		return this.x == x && this.y == y;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MazeCell other = (MazeCell) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[%s/%s]", x, y);
	}

}
