package me.mrletsplay.crazymaze.main;
public class Cell {

	private boolean wallLeft;
	private boolean wallRight;
	private boolean wallUp;
	private boolean wallDown;
	private boolean visited;
	private boolean ignore;
	private boolean visited2;
	
	public Cell() {
		/*Random r = new Random();
		wallLeft = r.nextBoolean();
		wallRight = r.nextBoolean();
		wallUp = r.nextBoolean();
		wallDown = r.nextBoolean();*/
		wallUp = true;
		wallDown = true;
		wallLeft = true;
		wallRight = true;
		visited = false;
		visited2 = false;
	}

	public boolean isWallLeft() {
		return wallLeft;
	}

	public void setWallLeft(boolean wallLeft) {
		this.wallLeft = wallLeft;
	}

	public boolean isWallDown() {
		return wallDown;
	}

	public void setWallDown(boolean wallDown) {
		this.wallDown = wallDown;
	}

	public boolean isWallUp() {
		return wallUp;
	}

	public void setWallUp(boolean wallUp) {
		this.wallUp = wallUp;
	}

	public boolean isWallRight() {
		return wallRight;
	}

	public void setWallRight(boolean wallRight) {
		this.wallRight = wallRight;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean isVisited2() {
		return visited2;
	}

	public void setVisited2(boolean visited) {
		this.visited2 = visited;
	}
	
	public void ignore() {
		ignore = true;
	}
	
	public void unIgnore() {
		ignore = false;
	}

	public boolean isIgnored() {
		return ignore;
	}
	
}
