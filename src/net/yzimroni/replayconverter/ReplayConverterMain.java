package net.yzimroni.replayconverter;

import java.io.File;
import java.io.FileInputStream;

import com.replaymod.replaystudio.replay.Replay;

import net.citizensnpcs.api.CitizensAPI;
import net.yzimroni.replayconverter.bukkit.Citizens;
import net.yzimroni.replayconverter.convert.ReplayConverter;
import net.yzimroni.replayconverter.utils.Utils;

public class ReplayConverterMain {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Not enough arguments, usage: [input] [output]");
			System.exit(1);
		}

		System.out.println("Starting");
		CitizensAPI.setImplementation(new Citizens());
		try {
			System.out.println("Loading replay");
			Replay replay = loadReplay(new File(args[0]));
			if (replay != null) {
				System.out.println("Replay loaded!");
				ReplayConverter converter = new ReplayConverter(replay, new File(args[1]));
				converter.convert();
				converter.save();
			} else {
				System.out.println("Replay is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Replay loadReplay(File f) {
		try {
			if (Utils.STUDIO
					.isCompatible(Utils.STUDIO.readReplayMetaData(new FileInputStream(f)).getFileFormatVersion())) {
				Replay replay = Utils.STUDIO.createReplay(new FileInputStream(f));
				return replay;
			} else {
				System.err.println(f.getAbsolutePath() + " is not supported");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
