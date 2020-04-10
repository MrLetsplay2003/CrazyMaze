package me.mrletsplay.crazymaze.maze;

public enum MazeDirection {
	
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	public MazeDirection getOpposite() {
		switch (this) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			default:
				throw new IllegalArgumentException();
		}
	}

}
