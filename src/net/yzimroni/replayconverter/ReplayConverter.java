package net.yzimroni.replayconverter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.spacehq.mc.auth.data.GameProfile.Property;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.replay.Replay;

import net.yzimroni.bukkitanimations.animation.AnimationManager;
import net.yzimroni.bukkitanimations.data.action.ActionData;
import net.yzimroni.bukkitanimations.record.RecordingSession;
import net.yzimroni.replayconverter.bukkit.BukkitWorld;
import net.yzimroni.replayconverter.data.EntityData;

public class ReplayConverter {

	private Replay replay;
	private RecordingSession recorder;
	private File output;
	private PacketHandler packetHandler;

	private int lastTick;

	private Cache<UUID, Property> skinCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();
	private HashMap<Integer, EntityData> trackedEntities = new HashMap<>();

	public ReplayConverter(Replay replay, File output) {
		super();
		this.replay = replay;
		this.output = output;

		AnimationManager.get().setAnimationsFolder(new File("export"));
		this.recorder = new RecordingSession(replay.getMetaData().getServerName() + replay.getMetaData().getDate(),
				UUID.randomUUID(), new Location(BukkitWorld.WORLD, 0, 0, 0), new Location(BukkitWorld.WORLD, 0, 0, 0));

		this.packetHandler = new PacketHandler(this);
	}

	public void start() {
		for (PacketData packet : replay) {
			lastTick = ((int) (packet.getTime() / 50)) + 1;
			System.out.println(packet.getTime());
			System.out.println(packet.getPacket().getClass());
			System.out.println();
			packetHandler.handle(packet.getPacket());
		}
		write();
	}

	public void addAction(ActionData action) {
		if (action.getTick() == -1) {
			action.setTick(lastTick);
		}
		recorder.addAction(action);
	}

	public void write() {
		try {
			Method writeMethod = recorder.getClass().getDeclaredMethod("writeAnimation");
			writeMethod.setAccessible(true);
			writeMethod.invoke(recorder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cache<UUID, Property> getSkinCache() {
		return skinCache;
	}

	public HashMap<Integer, EntityData> getTrackedEntities() {
		return trackedEntities;
	}

}
