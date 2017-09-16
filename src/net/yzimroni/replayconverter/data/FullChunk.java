package net.yzimroni.replayconverter.data;

import org.bukkit.util.Vector;
import org.spacehq.mc.protocol.data.game.Chunk;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;

@SuppressWarnings("deprecation")
public class FullChunk extends CuboidClipboard {

	private int x;
	private int z;
	private Chunk[] columns;

	public FullChunk(int x, int z, Chunk[] columns) {
		super(new com.sk89q.worldedit.Vector(16, 256, 16), new com.sk89q.worldedit.Vector(0, 0, 0));
		this.x = x;
		this.z = z;
		this.columns = columns;
	}

	public boolean isEmpty() {
		for (Chunk chunk : columns) {
			if (chunk != null && !chunk.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public String getChunkId() {
		return x + ";" + z;
	}

	public BaseBlock getBlock(int x, int y, int z) {
		int chunkIndex = y / 16;
		Chunk colum = columns[chunkIndex];
		if (colum == null) {
			return new BaseBlock(0);
		}
		y = y - (chunkIndex * 16);
		int blockId = colum.getBlocks().getBlock(x, y, z);
		int data = colum.getBlocks().getData(x, y, z);
		return new BaseBlock(blockId, data);
	}

	@Override
	public BaseBlock getPoint(com.sk89q.worldedit.Vector position) throws ArrayIndexOutOfBoundsException {
		return getBlock(position);
	}

	@Override
	public BaseBlock getBlock(com.sk89q.worldedit.Vector position) throws ArrayIndexOutOfBoundsException {
		return getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ());
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public Chunk[] getColumns() {
		return columns;
	}

	public Vector getChunkSize() {
		return new Vector(16, 16 * columns.length, 16);
	}

}
