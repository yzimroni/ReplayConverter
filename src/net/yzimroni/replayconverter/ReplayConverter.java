package net.yzimroni.replayconverter;

import java.io.File;

import com.replaymod.replaystudio.PacketData;
import com.replaymod.replaystudio.replay.Replay;

public class ReplayConverter {

	private Replay replay;
	private File output;

	public ReplayConverter(Replay replay, File output) {
		super();
		this.replay = replay;
		this.output = output;
	}

	public void start() {
		for (PacketData packet : replay) {
			System.out.println(packet.getTime());
			System.out.println(packet.getPacket().getClass());
			System.out.println();
		}
	}

}
