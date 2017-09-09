package net.yzimroni.replayconverter.data;

import org.bukkit.entity.EntityType;

public class EntityData {

	private int entityId;
	private EntityType type;
	private Location location;

	public EntityData(int entityId, EntityType type, Location location) {
		super();
		this.entityId = entityId;
		this.type = type;
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getEntityId() {
		return entityId;
	}

	public EntityType getType() {
		return type;
	}

}
