package net.yzimroni.replayconverter.data;

import java.util.UUID;

import org.bukkit.entity.EntityType;

public class PlayerData extends EntityData {

	private UUID uuid;
	private String name;
	
	private String lastCustomName;

	public PlayerData(int entityId, Location location, UUID uuid, String name) {
		super(entityId, EntityType.PLAYER, location);
		this.uuid = uuid;
		this.name = name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getLastCustomName() {
		return lastCustomName;
	}

	public void setLastCustomName(String lastCustomName) {
		this.lastCustomName = lastCustomName;
	}

}
