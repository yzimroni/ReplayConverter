package net.yzimroni.replayconverter.utils;

import org.bukkit.entity.EntityType;
import org.spacehq.mc.protocol.data.game.EntityMetadata;
import org.spacehq.mc.protocol.data.game.values.entity.ObjectType;

import com.replaymod.replaystudio.studio.ReplayStudio;

import net.yzimroni.replayconverter.PacketHandler;

public class Utils {

	public static final ReplayStudio STUDIO = new ReplayStudio();

	static {
		PacketHandler.getHandlers().keySet().forEach(p -> {
			STUDIO.setParsing(p, true);
		});
	}

	private Utils() {

	}

	public static EntityMetadata getMetadataById(EntityMetadata[] metadataList, int id) {
		for (EntityMetadata metadata : metadataList) {
			if (metadata.getId() == id) {
				return metadata;
			}
		}
		return null;
	}

	public static EntityType getEntityType(Enum<?> raw) {
		if (raw instanceof ObjectType) {
			if (raw == ObjectType.ITEM) {
				return EntityType.DROPPED_ITEM;
			}
		}
		return EntityType.valueOf(raw.name());

	}

}
