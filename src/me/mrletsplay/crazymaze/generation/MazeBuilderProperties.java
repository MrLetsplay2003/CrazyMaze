package me.mrletsplay.crazymaze.generation;

import me.mrletsplay.crazymaze.arena.ArenaGameMode;
import me.mrletsplay.crazymaze.main.MaterialWithData;
import me.mrletsplay.crazymaze.maze.MazeCell;

public class MazeBuilderProperties {

	private int
		fieldSize,
		wallWidth,
		wallHeight;
	
	private MaterialWithData
		fieldMaterial,
		wallMaterial,
		subWallMaterial;
	
	private ArenaGameMode mode;
	
	private MazeCell
		finishSignCell;
	
	private MazeCell
		redCell,
		blueCell,
		greenCell,
		yellowCell;
	
	private boolean
		powerupFields;
	
	private double
		powerupFieldChance;
	
	public MazeBuilderProperties(int fieldSize, int wallWidth, int wallHeight, MaterialWithData fieldMaterial, MaterialWithData wallMaterial, MaterialWithData subWallMaterial, ArenaGameMode mode, MazeCell finishSignCell, boolean powerupFields, double powerupFieldChance) {
		this.fieldSize = fieldSize;
		this.wallWidth = wallWidth;
		this.wallHeight = wallHeight;
		this.fieldMaterial = fieldMaterial;
		this.wallMaterial = wallMaterial;
		this.subWallMaterial = subWallMaterial;
		this.mode = mode;
		this.finishSignCell = finishSignCell;
		this.powerupFields = powerupFields;
		this.powerupFieldChance = powerupFieldChance;
	}
	
	public MazeBuilderProperties(int fieldSize, int wallWidth, int wallHeight, MaterialWithData fieldMaterial, MaterialWithData wallMaterial, MaterialWithData subWallMaterial, ArenaGameMode mode, MazeCell redCell, MazeCell blueCell, MazeCell greenCell, MazeCell yellowCell, boolean powerupFields, double powerupFieldChance) {
		this.fieldSize = fieldSize;
		this.wallWidth = wallWidth;
		this.wallHeight = wallHeight;
		this.fieldMaterial = fieldMaterial;
		this.wallMaterial = wallMaterial;
		this.subWallMaterial = subWallMaterial;
		this.mode = mode;
		this.redCell = redCell;
		this.blueCell = blueCell;
		this.greenCell = greenCell;
		this.yellowCell = yellowCell;
		this.powerupFields = powerupFields;
		this.powerupFieldChance = powerupFieldChance;
	}

	public int getFieldSize() {
		return fieldSize;
	}

	public int getWallWidth() {
		return wallWidth;
	}
	
	public int getCellSize() {
		return fieldSize + wallWidth;
	}

	public int getWallHeight() {
		return wallHeight;
	}

	public MaterialWithData getFieldMaterial() {
		return fieldMaterial;
	}

	public MaterialWithData getWallMaterial() {
		return wallMaterial;
	}

	public MaterialWithData getSubWallMaterial() {
		return subWallMaterial;
	}
	
	public ArenaGameMode getMode() {
		return mode;
	}
	
	public MazeCell getFinishSignCell() {
		return finishSignCell;
	}
	
	public MazeCell getRedCell() {
		return redCell;
	}
	
	public MazeCell getBlueCell() {
		return blueCell;
	}
	
	public MazeCell getGreenCell() {
		return greenCell;
	}
	
	public MazeCell getYellowCell() {
		return yellowCell;
	}
	
	public boolean isPowerupFields() {
		return powerupFields;
	}
	
	public double getPowerupFieldChance() {
		return powerupFieldChance;
	}
	
}
