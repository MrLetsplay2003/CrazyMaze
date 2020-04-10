package me.mrletsplay.crazymaze.maze;

public class Maze3D {
	
	private int
		sizeX,
		sizeY;
	
	private MazeLayer[] layers;
	
	public Maze3D(int sizeX, int sizeY, int numLayers) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.layers = new MazeLayer[numLayers];
		
		for(int l = 0; l < numLayers; l++) {
			layers[l] = new MazeLayer(l, sizeX, sizeY);
		}
	}
	
	public MazeLayer getLayer(int layer) {
		return layers[layer];
	}
	
	public MazeLayer[] getLayers() {
		return layers;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public int getNumLayers() {
		return layers.length;
	}

}
