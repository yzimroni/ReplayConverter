package net.yzimroni.replayconverter.data;

import org.bukkit.util.Vector;
import org.spacehq.mc.protocol.data.game.Position;

public class Location {

	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public Location(double x, double y, double z, float yaw, float pitch) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Location(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(Position position) {
		this(position.getX(), position.getY(), position.getZ());
	}

	public Location(Vector vector) {
		this(vector.getX(), vector.getY(), vector.getZ());
	}

	public Location add(double x, double y, double z) {
		return new Location(this.x + x, this.y + y, this.z + z, yaw, pitch);
	}

	public Location changeLook(float yaw, float pitch) {
		return new Location(x, y, z, yaw, pitch);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + "]";
	}

}
