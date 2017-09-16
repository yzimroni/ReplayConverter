package net.yzimroni.replayconverter.data;

import java.util.ArrayList;
import java.util.List;

import org.spacehq.mc.protocol.data.game.values.scoreboard.NameTagVisibility;
import org.spacehq.mc.protocol.data.game.values.scoreboard.TeamColor;

public class Team {

	private String name;
	private String prefix;
	private String suffix;
	private NameTagVisibility nameTagVisible;
	private TeamColor color;
	private List<String> players = new ArrayList<String>();

	public Team(String name, String prefix, String suffix, NameTagVisibility nameTagVisible, TeamColor color) {
		super();
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.nameTagVisible = nameTagVisible;
		this.color = color;
	}

	public void addPlayer(String name) {
		players.add(name);
	}

	public void removePlayer(String name) {
		players.remove(name);
	}

	public boolean containsPlayer(String name) {
		return players.contains(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public NameTagVisibility isNameTagVisible() {
		return nameTagVisible;
	}

	public void setNameTagVisible(NameTagVisibility nameTagVisible) {
		this.nameTagVisible = nameTagVisible;
	}

	public TeamColor getColor() {
		return color;
	}

	public void setColor(TeamColor color) {
		this.color = color;
	}

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	@Override
	public String toString() {
		return "Team [name=" + name + ", prefix=" + prefix + ", suffix=" + suffix + ", nameTagVisible=" + nameTagVisible
				+ ", color=" + color + ", players=" + players + "]";
	}

}
