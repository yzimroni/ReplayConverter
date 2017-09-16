package net.yzimroni.replayconverter.convert;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.spacehq.mc.auth.data.GameProfile;

import net.yzimroni.replayconverter.data.EntityData;

public class ConverterTracker {

	private ReplayConverter converter;

	private HashMap<UUID, GameProfile> profiles = new HashMap<UUID, GameProfile>();
	private HashMap<Integer, EntityData> trackedEntities = new HashMap<>();
	private AtomicInteger schematicNumber = new AtomicInteger(0);

	public ConverterTracker(ReplayConverter converter) {
		super();
		this.converter = converter;
	}

	public HashMap<UUID, GameProfile> getProfiles() {
		return profiles;
	}

	public HashMap<Integer, EntityData> getTrackedEntities() {
		return trackedEntities;
	}

	public AtomicInteger getSchematicNumber() {
		return schematicNumber;
	}

}
