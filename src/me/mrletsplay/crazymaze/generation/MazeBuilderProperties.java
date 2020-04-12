package me.mrletsplay.crazymaze.generation;

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
	
	private MazeCell
		finishSignCell;
	
	private boolean
		powerupFields;
	
	private double
		powerupFieldChance;
	
	public MazeBuilderProperties(int fieldSize, int wallWidth, int wallHeight, MaterialWithData fieldMaterial, MaterialWithData wallMaterial, MaterialWithData subWallMaterial, MazeCell finishSignCell, boolean powerupFields, double powerupFieldChance) {
		this.fieldSize = fieldSize;
		this.wallWidth = wallWidth;
		this.wallHeight = wallHeight;
		this.fieldMaterial = fieldMaterial;
		this.wallMaterial = wallMaterial;
		this.subWallMaterial = subWallMaterial;
		this.finishSignCell = finishSignCell;
		this.powerupFields = powerupFields;
		this.powerupFieldChance = powerupFieldChance;
	}
	
	public int getFieldSize() {
		return fieldSize;
	}

	public int getWallWidth() {
		return wallWidth;
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
	
	public MazeCell getFinishSignCell() {
		return finishSignCell;
	}
	
	public boolean isPowerupFields() {
		return powerupFields;
	}
	
	public double getPowerupFieldChance() {
		return powerupFieldChance;
	}
	
}
