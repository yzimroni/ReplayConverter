package net.yzimroni.replayconverter;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.spacehq.mc.auth.data.GameProfile;

import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.replay.Replay;

import net.yzimroni.bukkitanimations.animation.AnimationData;
import net.yzimroni.bukkitanimations.record.Recorder;
import net.yzimroni.replayconverter.data.EntityData;

public class ReplayConverter extends Recorder {

	private Replay replay;
	private File output;
	private PacketHandler packetHandler;

	private HashMap<UUID, GameProfile> profiles = new HashMap<UUID, GameProfile>();
	private HashMap<Integer, EntityData> trackedEntities = new HashMap<>();
	private AtomicInteger schematicNumber = new AtomicInteger(0);

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
		System.out.println("Saving to " + output.getAbsolutePath());
		writeAnimation(output);
		System.out.println("Saved!");
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
