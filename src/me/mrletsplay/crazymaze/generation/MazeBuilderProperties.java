package me.mrletsplay.crazymaze.generation;

import me.mrletsplay.crazymaze.main.MaterialWithData;

public class MazeBuilderProperties {
	
	private int
		fieldSize,
		wallWidth,
		wallHeight;
	
	private MaterialWithData
		fieldMaterial,
		wallMaterial,
		subWallMaterial;

	public MazeBuilderProperties(int fieldSize, int wallWidth, int wallHeight, MaterialWithData fieldMaterial, MaterialWithData wallMaterial, MaterialWithData subWallMaterial) {
		this.fieldSize = fieldSize;
		this.wallWidth = wallWidth;
		this.wallHeight = wallHeight;
		this.fieldMaterial = fieldMaterial;
		this.wallMaterial = wallMaterial;
		this.subWallMaterial = subWallMaterial;
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
	
}
