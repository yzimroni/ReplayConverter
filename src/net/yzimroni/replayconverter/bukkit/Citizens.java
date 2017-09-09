package net.yzimroni.replayconverter.bukkit;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import com.avaje.ebean.EbeanServer;

import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.ai.speech.SpeechFactory;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.NPCSelector;
import net.citizensnpcs.api.trait.TraitFactory;

public class Citizens implements CitizensPlugin {

	@Override
	public FileConfiguration getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getDataFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EbeanServer getDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginDescriptionFile getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginLoader getPluginLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResource(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNaggable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveDefaultConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveResource(String arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNaggable(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NPCRegistry createAnonymousNPCRegistry(NPCDataStore arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NPCRegistry createNamedNPCRegistry(String arg0, NPCDataStore arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NPCSelector getDefaultNPCSelector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NPCRegistry getNamedNPCRegistry(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<NPCRegistry> getNPCRegistries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NPCRegistry getNPCRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getOwningClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getScriptFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpeechFactory getSpeechFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TraitFactory getTraitFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onImplementationChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeNamedNPCRegistry(String arg0) {
		// TODO Auto-generated method stub

	}

}
