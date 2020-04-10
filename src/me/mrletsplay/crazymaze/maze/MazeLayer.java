package me.mrletsplay.crazymaze.maze;

public class MazeLayer {
	
	private int layerIndex;
	private MazeCell[][] cells;
	private int
		sizeX,
		sizeY;
	
	public MazeLayer(int layerIndex, int sizeX, int sizeY) {
		this.layerIndex = layerIndex;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		
		this.cells = new MazeCell[sizeX][sizeY];
		
		for(int x = 0; x < sizeX; x++) {
			for(int y = 0; y < sizeY; y++) {
				cells[x][y] = new MazeCell(this, x, y);
			}
		}
	}
	
	public int getLayerIndex() {
		return layerIndex;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public MazeCell getCell(int x, int y) {
		if(x < 0 || y < 0 || x >= sizeX || y >= sizeY) return null;
		return cells[x][y];
	}
	
	public MazeCell[][] getCells() {
		return cells;
	}

}
