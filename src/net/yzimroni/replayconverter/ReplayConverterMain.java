package net.yzimroni.replayconverter;

import java.io.File;
import java.io.FileInputStream;

import com.replaymod.replaystudio.replay.Replay;

import net.citizensnpcs.api.CitizensAPI;
import net.yzimroni.replayconverter.bukkit.Citizens;
import net.yzimroni.replayconverter.utils.Utils;

public class ReplayConverterMain {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Not enough arguments, usage: [input] [output]");
			System.exit(1);
		}

		CitizensAPI.setImplementation(new Citizens());
		try {
			Replay replay = Utils.STUDIO.createReplay(new FileInputStream(args[0]));
			ReplayConverter converter = new ReplayConverter(replay, new File(args[1]));
			converter.convert();
			converter.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
