package me.mrletsplay.crazymaze.main;

import org.bukkit.Material;

import me.mrletsplay.mrcore.bukkitimpl.versioned.MaterialDefinition;
import me.mrletsplay.mrcore.bukkitimpl.versioned.VersionedMaterial;

public class MaterialWithData {

	private Material material;
	private short data;
	
	public MaterialWithData(Material material, short data) {
		this.material = material;
		this.data = data;
	}
	
	public MaterialWithData(Material material) {
		this.material = material;
		this.data = 0;
	}
	
	public MaterialWithData(VersionedMaterial material) {
		MaterialDefinition def = material.getCurrentMaterialDefinition();
		this.material = def.getMaterial();
		this.data = def.getDamage();
	}

	public Material getMaterial() {
		return material;
	}

	public short getData() {
		return data;
	}
	
}
