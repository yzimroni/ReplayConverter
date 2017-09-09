package net.yzimroni.replayconverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;

import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.data.game.EntityMetadata;
import org.spacehq.mc.protocol.data.game.Position;
import org.spacehq.mc.protocol.data.game.values.PlayerListEntry;
import org.spacehq.mc.protocol.data.game.values.PlayerListEntryAction;
import org.spacehq.mc.protocol.data.game.values.entity.FallingBlockData;
import org.spacehq.mc.protocol.data.game.values.entity.GlobalEntityType;
import org.spacehq.mc.protocol.data.game.values.entity.HangingDirection;
import org.spacehq.mc.protocol.data.game.values.entity.ProjectileData;
import org.spacehq.mc.protocol.data.game.values.entity.player.Animation;
import org.spacehq.mc.protocol.data.game.values.world.block.BlockChangeRecord;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerAnimationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerCollectItemPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerDestroyEntitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import org.spacehq.packetlib.packet.Packet;

import net.yzimroni.bukkitanimations.data.action.ActionData;
import net.yzimroni.bukkitanimations.data.action.ActionType;
import net.yzimroni.replayconverter.data.EntityData;
import net.yzimroni.replayconverter.data.Location;
import net.yzimroni.replayconverter.utils.Utils;

public class PacketHandler {

	private static final HashMap<Class<? extends Packet>, BiConsumer<? extends Packet, ReplayConverter>> HANDLERS = new HashMap<>();

	private ReplayConverter converter;

	static {
		initHandlers();
	}

	public PacketHandler(ReplayConverter converter) {
		super();
		this.converter = converter;
	}

	@SuppressWarnings("deprecation")
	private static void initHandlers() {
		/*
		 * @formatter:off
		 * TODO ItemStacks saving
		 * 
		 * ServerEntityEquipmentPacket
		 * ? ServerEntityHeadLookPacket
		 * ServerEntityNBTUpdatePacket
		 * ? ServerEntityPropertiesPacket
		 * ServerEntityRemoveEffectPacket
		 * ? ServerEntityStatusPacket
		 * ? ServerEntityVelocityPacket
		 * ? ServerBlockValuePacket
		 * ServerChunkDataPacket
		 * 
		 * ? ServerMapDataPacket
		 * ServerMultiChunkDataPacket
		 * ServerPlayEffectPacket
		 * ? ServerPlaySoundPacket
		 * ServerUpdateSignPacket
		 * ? ServerUpdateTileEntityPacket
		 * ServerChatPacket
		 * 
		 * 
		 * @formatter:on
		 */
		addHandler(ServerBlockBreakAnimPacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.BLOCK_BREAK_ANIMATION).data("entityId", p.getBreakerEntityId())
					.data("stage", p.getStage().ordinal() + 1).data("location", p.getPosition()));
		});

		addHandler(ServerSpawnGlobalEntityPacket.class, (p, c) -> {
			if (p.getType() == GlobalEntityType.LIGHTNING_BOLT) {
				c.addAction(new ActionData(ActionType.LIGHTNING_STRIKE).data("location",
						new Position(p.getX(), p.getY(), p.getZ())));
			} else {
				throw new IllegalArgumentException("Unknown global entity type " + p.getType());
			}
		});

		addHandler(ServerAnimationPacket.class, (p, c) -> {
			if (p.getAnimation() == Animation.DAMAGE) {
				c.addAction(new ActionData(ActionType.ENTITY_DAMAGE).data("entityId", p.getEntityId()));
			} else if (p.getAnimation() == Animation.SWING_ARM) {
				c.addAction(new ActionData(ActionType.PLAYER_ANIMATION).data("entityId", p.getEntityId()).data("type",
						"ARM_SWING"));
			}
		});

		addHandler(ServerCollectItemPacket.class, (p, c) -> {
			if (c.getTrackedEntities().containsKey(p.getCollectedEntityId())
					&& c.getTrackedEntities().containsKey(p.getCollectorEntityId())) {
				c.addAction(new ActionData(ActionType.ENTITY_PICKUP).data("entityId", p.getCollectedEntityId())
						.data("playerId", p.getCollectorEntityId()));
			}
		});

		addHandler(ServerDestroyEntitiesPacket.class, (p, c) -> {
			for (int entityId : p.getEntityIds()) {
				if (c.getTrackedEntities().containsKey(entityId)) {
					c.addAction(new ActionData(ActionType.DESPAWN_ENTITY).data("entityId", entityId));
					c.getTrackedEntities().remove(entityId);
				}
			}
		});

		addHandler(ServerEntityEffectPacket.class, (p, c) -> {
			if (c.getTrackedEntities().containsKey(p.getEntityId())) {
				HashMap<String, Object> potion = new HashMap<>();
				potion.put("effect", p.getEffect().ordinal() + 1);
				potion.put("duration", p.getDuration());
				potion.put("amplifier", p.getAmplifier());
				potion.put("has-particles", !p.getHideParticles());
				c.addAction(new ActionData(ActionType.UPDATE_ENTITY).data("entityId", p.getEntityId()).data("potions",
						Arrays.asList(potion)));
			}
		});

		addHandler(ServerBlockChangePacket.class, (p, c) -> {
			BlockChangeRecord record = p.getRecord();
			if (record.getId() == 0) {
				c.addAction(new ActionData(ActionType.BLOCK_BREAK).data("location", record.getPosition()));
			} else {
				c.addAction(new ActionData(ActionType.BLOCK_PLACE).data("location", record.getPosition())
						.data("type", Material.getMaterial(record.getId())).data("data", record.getData()));
			}
		});

		addHandler(ServerMultiBlockChangePacket.class, (p, c) -> {
			for (BlockChangeRecord record : p.getRecords()) {
				if (record.getId() == 0) {
					c.addAction(new ActionData(ActionType.BLOCK_BREAK).data("location", record.getPosition()));
				} else {
					c.addAction(new ActionData(ActionType.BLOCK_PLACE).data("location", record.getPosition())
							.data("type", Material.getMaterial(record.getId())).data("data", record.getData()));
				}
			}
		});

		addHandler(ServerExplosionPacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.EXPLOSION).data("location", new Vector(p.getX(), p.getY(), p.getZ()))
					.data("blocks", p.getExploded()));
		});

		addHandler(ServerSpawnParticlePacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.PARTICLE).data("location", new Vector(p.getX(), p.getY(), p.getZ()))
					.data("particleId", p.getParticle().ordinal() + 1).data("longDis", p.isLongDistance())
					.data("offset", new Vector(p.getOffsetX(), p.getOffsetY(), p.getOffsetZ()))
					.data("data", p.getVelocityOffset()).data("count", p.getAmount()).data("dataArray", p.getData()));
		});

		addHandler(ServerPlayerListEntryPacket.class, (p, c) -> {
			if (p.getAction() == PlayerListEntryAction.ADD_PLAYER) {
				for (PlayerListEntry player : p.getEntries()) {
					c.getProfileCache().put(player.getProfile().getId(), player.getProfile());
				}
			} else if (p.getAction() == PlayerListEntryAction.REMOVE_PLAYER) {
				for (PlayerListEntry player : p.getEntries()) {
					c.getProfileCache().invalidate(player.getProfile().getId());
				}
			}
		});

		addHandler(ServerSpawnPlayerPacket.class, (p, c) -> {
			GameProfile profile = c.getProfileCache().getIfPresent(p.getUUID());
			Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
			ActionData action = new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.PLAYER)
					.data("name", profile.getName()).data("entityId", p.getEntityId()).data("location", location)
					.data("textures", profile.getProperty("textures"));
			c.getTrackedEntities().put(p.getEntityId(), new EntityData(p.getEntityId(), EntityType.PLAYER, location));
			handleMetadata(action, EntityType.PLAYER, p.getMetadata());
			c.addAction(action);
		});

		addHandler(ServerSpawnMobPacket.class, (p, c) -> {
			EntityType type = Utils.getEntityType(p.getType());
			Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
			ActionData action = new ActionData(ActionType.SPAWN_ENTITY).data("type", type)
					.data("entityId", p.getEntityId()).data("location", location)
					.data("velocity", new Vector(p.getMotionX(), p.getMotionY(), p.getMotionZ()));
			c.getTrackedEntities().put(p.getEntityId(), new EntityData(p.getEntityId(), type, location));

			// TODO head yaw
			handleMetadata(action, type, p.getMetadata());
			c.addAction(action);
		});

		addHandler(ServerSpawnExpOrbPacket.class, (p, c) -> {
			Location location = new Location(p.getX(), p.getY(), p.getZ());
			c.addAction(new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.EXPERIENCE_ORB)
					.data("entityId", p.getEntityId()).data("location", location));
			c.getTrackedEntities().put(p.getEntityId(),
					new EntityData(p.getEntityId(), EntityType.EXPERIENCE_ORB, location));
		});

		addHandler(ServerSpawnObjectPacket.class, (p, c) -> {
			EntityType type = Utils.getEntityType(p.getType());
			Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
			ActionData action = new ActionData(ActionType.SPAWN_ENTITY).data("type", type)
					.data("entityId", p.getEntityId()).data("location", location)
					.data("velocity", new Vector(p.getMotionX(), p.getMotionY(), p.getMotionZ()));
			if (p.getData() instanceof ProjectileData) {
				action.setType(ActionType.SHOOT_PROJECTILE);
				action.data("shooterId", ((ProjectileData) p.getData()).getOwnerId());
			} else if (p.getData() instanceof FallingBlockData) {
				FallingBlockData data = (FallingBlockData) p.getData();
				action.data("blockType", Material.getMaterial(data.getId())).data("data", data.getMetadata());
			} else if (p.getData() instanceof HangingDirection) {
				action.data("attachedFace", BlockFace.valueOf(((HangingDirection) p.getData()).name()));
			}
			// TODO the rest of the data types
			c.getTrackedEntities().put(p.getEntityId(), new EntityData(p.getEntityId(), type, location));

			c.addAction(action);
		});

		addHandler(ServerSpawnPaintingPacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.PAINTING)
					.data("entityId", p.getEntityId()).data("location", p.getPosition())
					.data("attachedFace", BlockFace.valueOf(p.getDirection().name()))
					.data("art", Art.valueOf(p.getArt().name())));
			c.getTrackedEntities().put(p.getEntityId(),
					new EntityData(p.getEntityId(), EntityType.PAINTING, new Location(p.getPosition())));
		});

		addHandler(ServerEntityMetadataPacket.class, (p, c) -> {
			if (c.getTrackedEntities().containsKey(p.getEntityId())) {
				ActionData action = new ActionData(ActionType.UPDATE_ENTITY).data("entityId", p.getEntityId());
				handleMetadata(action, c.getTrackedEntities().get(p.getEntityId()).getType(), p.getMetadata());
				c.addAction(action);
			}
		});

		addMultipleHandlers((p, c) -> {
			if (c.getTrackedEntities().containsKey(p.getEntityId())) {
				Location location = c.getTrackedEntities().get(p.getEntityId()).getLocation();
				location = location.add(p.getMovementX(), p.getMovementY(), p.getMovementZ());
				if (p instanceof ServerEntityPositionRotationPacket || p instanceof ServerEntityRotationPacket) {
					location = location.changeLook(p.getYaw(), p.getPitch());
				}
				c.addAction(new ActionData(ActionType.ENTITY_MOVE).data("entityId", p.getEntityId()).data("location",
						location));
				c.getTrackedEntities().get(p.getEntityId()).setLocation(location);
			}
		}, ServerEntityMovementPacket.class, ServerEntityPositionPacket.class, ServerEntityPositionRotationPacket.class,
				ServerEntityRotationPacket.class);

		addHandler(ServerEntityTeleportPacket.class, (p, c) -> {
			if (c.getTrackedEntities().containsKey(p.getEntityId())) {
				Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
				c.addAction(new ActionData(ActionType.ENTITY_MOVE).data("entityId", p.getEntityId()).data("location",
						location));
				c.getTrackedEntities().get(p.getEntityId()).setLocation(location);
			}
		});

		addHandler(Packet.class, (p, c) -> {
		});
	}

	@SuppressWarnings("deprecation")
	private static void handleMetadata(ActionData action, EntityType type, EntityMetadata[] metadata) {
		EntityMetadata b_ = Utils.getMetadataById(metadata, 0);
		if (b_ != null) {
			byte b = (byte) b_.getValue();
			boolean onFire = (b & 1 << 1) != 0;
			action.data("fireTicks", onFire ? Integer.MAX_VALUE : 0);
			if (type == EntityType.PLAYER) {
				boolean sneak = (b & 1 << 2) != 0;
				boolean sprint = (b & 1 << 3) != 0;
				action.data("sneaking", sneak).data("sprinting", sprint);
			}
		}
		if (LivingEntity.class.isAssignableFrom(type.getEntityClass())) {
			EntityMetadata nameTagData = Utils.getMetadataById(metadata, 2);
			if (nameTagData != null) {
				action.data("customName", nameTagData.getValue());
			}
			EntityMetadata nameVisibleData = Utils.getMetadataById(metadata, 3);
			if (nameVisibleData != null) {
				action.data("customNameVisble", ((byte) nameVisibleData.getValue()) != 0);
			}
			if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
				EntityMetadata ageData = Utils.getMetadataById(metadata, 12);
				if (ageData != null) {
					action.data("age", ageData.getValue());
				}
				if (Pig.class.isAssignableFrom(type.getEntityClass())) {
					EntityMetadata saddleData = Utils.getMetadataById(metadata, 16);
					if (saddleData != null) {
						action.data("saddle", ((byte) saddleData.getValue()) != 0);

					}
				}
				if (Sheep.class.isAssignableFrom(type.getEntityClass())) {
					EntityMetadata sheepData = Utils.getMetadataById(metadata, 16);
					if (sheepData != null) {
						byte sheep = (byte) sheepData.getValue();
						byte color = (byte) (sheep & 1 << 1);
						boolean sheared = (byte) (sheep & 1 << 2) != 0;
						;
						action.data("color", DyeColor.getByData(color)).data("sheared", sheared);
					}
				}
				if (Zombie.class.isAssignableFrom(type.getEntityClass())) {
					EntityMetadata babyData = Utils.getMetadataById(metadata, 12);
					if (babyData != null) {
						action.data("baby", ((byte) babyData.getValue()) != 0);
					}
				}
				if (Creeper.class.isAssignableFrom(type.getEntityClass())) {
					EntityMetadata poweredData = Utils.getMetadataById(metadata, 17);
					action.data("powered", ((byte) poweredData.getValue()) != 0);
				}
				if (Slime.class.isAssignableFrom(type.getEntityClass())) {
					EntityMetadata sizeData = Utils.getMetadataById(metadata, 16);
					if (sizeData != null) {
						action.data("size", sizeData.getValue());
					}
				}
			}
			/*
			 * @formatter:off
			 * TODO
			 * armor stand
			 * horse
			 * Ocelot
			 * Tameable
			 * Wolf
			 * Rabbit
			 * Villager
			 * Enderman
			 * Skeleton
			 * Wither
			 * Guardian
			 * Minecart
			 * Furnace Minecart
			 * Item
			 * Firework
			 * Item Frame
			 * 
			 * 
			 * @formatter:on
			 */
		}
	}

	private static <T extends Packet> void addHandler(Class<T> packet, BiConsumer<T, ReplayConverter> handler) {
		HANDLERS.put(packet, handler);
	}

	@SafeVarargs
	private static <T extends Packet> void addMultipleHandlers(BiConsumer<T, ReplayConverter> handler,
			Class<? extends T>... packets) {
		for (Class<? extends T> packet : packets) {
			HANDLERS.put(packet, handler);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Packet> void handle(T packet) {
		if (HANDLERS.containsKey(packet.getClass())) {
			BiConsumer<T, ReplayConverter> handler = (BiConsumer<T, ReplayConverter>) HANDLERS.get(packet.getClass());
			handler.accept(packet, converter);
		}
	}

	public static HashMap<Class<? extends Packet>, BiConsumer<? extends Packet, ReplayConverter>> getHandlers() {
		return HANDLERS;
	}

}
