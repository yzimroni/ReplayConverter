package net.yzimroni.replayconverter.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spacehq.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import org.spacehq.packetlib.packet.Packet;

import com.google.common.base.Preconditions;
import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.data.Marker;
import com.replaymod.replaystudio.replay.Replay;

import net.yzimroni.bukkitanimations.animation.AnimationData;
import net.yzimroni.bukkitanimations.record.Recorder;
import net.yzimroni.replayconverter.utils.Utils;

public class ReplayConverter extends Recorder {

	private Replay replay;
	private File output;
	private PacketHandler packetHandler;

	/**
	 * NOTE: Recording won't start at 'startAt', but rather at the first world
	 * switch (Respawn packet) after 'startAt'
	 */
	private int startAt = 2650;
	private int endAt = 46868;

	private boolean recordingPackets;
	private int startedAt;

	private ConverterTracker tracker;

	private List<Packet> handlingQueue = new ArrayList<Packet>();

	public ReplayConverter(Replay replay, File output) {
		super(new AnimationData(replay.getMetaData().getServerName() + replay.getMetaData().getDate(),
				UUID.randomUUID()));
		this.replay = replay;
		this.output = output;

		this.tracker = new ConverterTracker(this);
		this.packetHandler = new PacketHandler(this);
	}

	public void convert() {
		if (startAt > 0) {
			recordingPackets = false;
		} else {
			startedAt = 0;
			recordingPackets = true;
		}
		for (PacketData packet : replay) {
			// World change handling
			if (packet.getPacket() instanceof ServerRespawnPacket) {
				if (!onWorldChanged(packet)) {
					recordingPackets = false;
					return;
				}
			}

			// Packet handling
			if (recordingPackets) {
				if (!handlingQueue.isEmpty()) {
					handlingQueue.forEach(this::processPacket);
					handlingQueue.clear();
				}
				setTick(getRelativeTick(getPacketTime(packet)));
				processPacket(packet.getPacket());
			} else {
				if (Utils.SHOULD_SAVE_BEFORE_RECORDING.contains(packet.getPacket().getClass())) {
					handlingQueue.add(packet.getPacket());
				}
			}

			// Checking if should stop recording
			if (endAt > 0 && recordingPackets && (getTick() + startedAt) >= endAt) {
				recordingPackets = false;
				return;
			}
		}

	}

	private int getPacketTime(PacketData packet) {
		return ((int) (packet.getTime() / 50)) + 1;
	}

	private int getRelativeTick(int absolute) {
		if (!recordingPackets) {
			return absolute;
		}
		return (absolute - startedAt) + 1;
	}

	private void processPacket(Packet packet) {
		packetHandler.handle(packet);
	}

	private boolean onWorldChanged(PacketData packet) {
		if (!recordingPackets && getPacketTime(packet) >= startAt) {
			System.out.println("Start recording");
			recordingPackets = true;
			startedAt = getPacketTime(packet);
		}
		return true;
	}

	public void save() {
		System.out.println("Saving to " + output.getAbsolutePath());
		writeAnimation(output);
		System.out.println("Saved!");
	}

	public void setStartAtMarker(String markerName) {
		setStartAt(getMarkerTime(markerName));
	}

	public void setEndAtMarker(String markerName) {
		setEndAt(getMarkerTime(markerName));
	}

	private int getMarkerTime(String markerName) {
		try {
			Marker marker = replay.getReplayFile().get().getMarkers().get().stream()
					.filter(m -> m.getName().equals(markerName)).findAny().orElse(null);
			Preconditions.checkNotNull(marker, "Marker not exists!");
			return (marker.getTime() / 50 + 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getStartAt() {
		return startAt;
	}

	public void setStartAt(int startAt) {
		this.startAt = startAt;
	}

	public int getEndAt() {
		return endAt;
	}

	public void setEndAt(int endAt) {
		this.endAt = endAt;
	}

	public boolean isRecordingPackets() {
		return recordingPackets;
	}

	public void setRecordingPackets(boolean recordingPackets) {
		this.recordingPackets = recordingPackets;
	}

	public ConverterTracker getTracker() {
		return tracker;
	}

}
