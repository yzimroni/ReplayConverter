package net.yzimroni.replayconverter.utils;

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

}
