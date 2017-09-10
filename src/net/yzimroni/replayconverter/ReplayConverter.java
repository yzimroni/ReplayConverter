package net.yzimroni.replayconverter;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spacehq.mc.auth.data.GameProfile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.replay.Replay;

import net.yzimroni.bukkitanimations.animation.AnimationData;
import net.yzimroni.bukkitanimations.record.Recorder;
import net.yzimroni.replayconverter.data.EntityData;

public class ReplayConverter extends Recorder {

	private Replay replay;
	private File output;
	private PacketHandler packetHandler;

	private Cache<UUID, GameProfile> profileCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS)
			.build();
	private HashMap<Integer, EntityData> trackedEntities = new HashMap<>();

	public ReplayConverter(Replay replay, File output) {
		super(new AnimationData(replay.getMetaData().getServerName() + replay.getMetaData().getDate(),
				UUID.randomUUID()));
		this.replay = replay;
		this.output = output;

		this.packetHandler = new PacketHandler(this);
	}

	public void convert() {
		for (PacketData packet : replay) {
			setTick(((int) (packet.getTime() / 50)) + 1);
			packetHandler.handle(packet.getPacket());
		}
	}

	public void save() {
		writeAnimation(output);
	}

	public Cache<UUID, GameProfile> getProfileCache() {
		return profileCache;
	}

	public HashMap<Integer, EntityData> getTrackedEntities() {
		return trackedEntities;
	}

}
