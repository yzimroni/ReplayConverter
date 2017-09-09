package net.yzimroni.replayconverter.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spacehq.packetlib.packet.Packet;

import com.replaymod.replaystudio.studio.ReplayStudio;

public class Utils {

	public static List<Class<? extends Packet>> PACKETS = Collections.unmodifiableList(Arrays.asList(Packet.class)); // TODO
	public static final ReplayStudio STUDIO = new ReplayStudio();

	static {
		PACKETS.forEach(p -> {
			STUDIO.setParsing(p, true);
		});
	}

	private Utils() {

	}

}
