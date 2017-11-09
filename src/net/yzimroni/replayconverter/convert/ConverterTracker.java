package net.yzimroni.replayconverter.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.ChatColor;
import org.spacehq.mc.auth.data.GameProfile;

import net.yzimroni.replayconverter.data.EntityData;
import net.yzimroni.replayconverter.data.PlayerData;
import net.yzimroni.replayconverter.data.Team;

public class ConverterTracker {

	private ReplayConverter converter;

	private HashMap<UUID, GameProfile> profiles = new HashMap<UUID, GameProfile>();
	private HashMap<Integer, EntityData> trackedEntities = new HashMap<>();
	private AtomicInteger schematicNumber = new AtomicInteger(0);
	private List<Team> teams = new ArrayList<Team>();

	public ConverterTracker(ReplayConverter converter) {
		super();
		this.converter = converter;
	}

	public Team getTeam(String name) {
		return teams.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
	}

	public Team getTeamByPlayer(String name) {
		return teams.stream().filter(t -> t.containsPlayer(name)).findAny().orElse(null);
	}

	public String getDisplayName(String player) {
		Team playerTeam = getTeamByPlayer(player);
		if (playerTeam == null) {
			System.out.println("Player " + player + " has no team");
			return null;
		}
		String name = ChatColor.getLastColors(playerTeam.getPrefix()) + player /*+ playerTeam.getSuffix()*/;
		System.out.println("Name for " + name + ": " + name + " (team: " + playerTeam + ")");
		return name;
	}

	public PlayerData getPlayer(String name) {
		return trackedEntities.values().stream().filter(PlayerData.class::isInstance).map(PlayerData.class::cast)
				.filter(p -> p.getName().equals(name)).findAny().orElse(null);
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

	public List<Team> getTeams() {
		return teams;
	}

}
