package net.yzimroni.replayconverter.utils;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.spacehq.mc.protocol.data.game.EntityMetadata;
import org.spacehq.mc.protocol.data.game.ItemStack;
import org.spacehq.mc.protocol.data.game.values.entity.ObjectType;
import org.spacehq.mc.protocol.util.NetUtil;
import org.spacehq.netty.buffer.Unpooled;
import org.spacehq.packetlib.tcp.io.ByteBufNetOutput;

import com.replaymod.replaystudio.studio.ReplayStudio;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
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

	public static EntityMetadata getMetadataById(EntityMetadata[] metadataList, int id) {
		for (EntityMetadata metadata : metadataList) {
			if (metadata.getId() == id) {
				return metadata;
			}
		}
		return null;
	}

	public static EntityType getEntityType(Enum<?> raw) {
		if (raw instanceof ObjectType) {
			if (raw == ObjectType.ITEM) {
				return EntityType.DROPPED_ITEM;
			}
		}
		return EntityType.valueOf(raw.name());
	}

	public static org.bukkit.inventory.ItemStack toBukkitItemStack(ItemStack item) {
		// TODO WIP
		org.spacehq.netty.buffer.ByteBuf original = Unpooled.buffer();
		ByteBufNetOutput output = new ByteBufNetOutput(original);
		try {
			NetUtil.writeItem(output, item);

			byte[] bytes = new byte[original.readableBytes()];
			original.readBytes(bytes);

			ByteBuf buf = io.netty.buffer.Unpooled.buffer();
			buf.writeBytes(bytes);

			PacketDataSerializer packetSer = new PacketDataSerializer(buf);
			Object nmsItemStack = packetSer.i();

			org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack
					.asBukkitCopy((net.minecraft.server.v1_8_R3.ItemStack) nmsItemStack);
			System.out.println("Item: " + bukkitItem);
			return bukkitItem;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
