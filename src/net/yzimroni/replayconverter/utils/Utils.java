package net.yzimroni.replayconverter.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.spacehq.mc.protocol.data.game.EntityMetadata;
import org.spacehq.mc.protocol.data.game.ItemStack;
import org.spacehq.mc.protocol.data.game.values.entity.MobType;
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

	private static final Map<Object, EntityType> ENTITY_TYPE_MAP = new HashMap<Object, EntityType>();
	private static final Map<Integer, String> POTION_EFFECTS = new HashMap<Integer, String>();

	static {
		PacketHandler.getHandlers().keySet().forEach(p -> {
			STUDIO.setParsing(p, true);
		});
		initEntityMap();
		initPotionEffectMap();
	}

	private Utils() {

	}

	private static void initEntityMap() {
		ENTITY_TYPE_MAP.put(ObjectType.ITEM, EntityType.DROPPED_ITEM);
		ENTITY_TYPE_MAP.put(ObjectType.FIREWORK_ROCKET, EntityType.FIREWORK);
		ENTITY_TYPE_MAP.put(ObjectType.FISH_HOOK, EntityType.FISHING_HOOK);
		ENTITY_TYPE_MAP.put(ObjectType.POTION, EntityType.SPLASH_POTION);
		ENTITY_TYPE_MAP.put(ObjectType.EXP_BOTTLE, EntityType.THROWN_EXP_BOTTLE);
		// TODO check fireballs maps
		ENTITY_TYPE_MAP.put(ObjectType.BLAZE_FIREBALL, EntityType.SMALL_FIREBALL);
		ENTITY_TYPE_MAP.put(ObjectType.GHAST_FIREBALL, EntityType.FIREBALL);
		ENTITY_TYPE_MAP.put(ObjectType.WITHER_HEAD_PROJECTILE, EntityType.WITHER_SKULL);
		ENTITY_TYPE_MAP.put(ObjectType.EYE_OF_ENDER, EntityType.ENDER_SIGNAL);
		// ENTITY_TYPE_MAP.put(ObjectType.FALLING_DRAGON_EGG, EntityType.); TODO
		ENTITY_TYPE_MAP.put(ObjectType.LEASH_KNOT, EntityType.LEASH_HITCH);

		ENTITY_TYPE_MAP.put(MobType.ZOMBIE_PIGMAN, EntityType.PIG_ZOMBIE);
		ENTITY_TYPE_MAP.put(MobType.GIANT_ZOMBIE, EntityType.GIANT);
		ENTITY_TYPE_MAP.put(MobType.MOOSHROOM, EntityType.MUSHROOM_COW);
	}

	@SuppressWarnings("deprecation")
	private static void initPotionEffectMap() {
		try {
			Field[] fields = PotionEffectType.class.getFields();
			for (Field field : fields) {
				if (field.getType().equals(PotionEffectType.class) && Modifier.isStatic(field.getModifiers())) {
					PotionEffectType effect = (PotionEffectType) field.get(null);
					POTION_EFFECTS.put(effect.getId(), field.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EntityMetadata getMetadataById(EntityMetadata[] metadataList, int id) {
		for (EntityMetadata metadata : metadataList) {
			if (metadata.getId() == id) {
				return metadata;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static EntityType getEntityType(Object raw) {
		if (ENTITY_TYPE_MAP.containsKey(raw)) {
			return ENTITY_TYPE_MAP.get(raw);
		}
		if (raw instanceof String) {
			return EntityType.fromName((String) raw);
		}
		if (raw instanceof Enum) {
			try {
				return EntityType.valueOf(((Enum<?>) raw).name());
			} catch (IllegalArgumentException e) {
				System.out.println("Unknown entity type: " + ((Enum<?>) raw).name() + " " + raw.getClass());
				throw e;
			}
		}
		throw new RuntimeException("Unhandled getEntityType object type: " + raw);
	}

	@SuppressWarnings("deprecation")
	public static Map<String, Object> serializeItem(ItemStack item) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (item == null) {
			return map;
		}
		map.put("type", Material.getMaterial(item.getId()));
		map.put("amount", item.getAmount());
		if (item.getData() != 0) {
			map.put("damage", item.getData());
		}
		// TODO nbt
		return map;
	}
	
	public static String getPotionEffectName(int id) {
		return POTION_EFFECTS.get(id);
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
