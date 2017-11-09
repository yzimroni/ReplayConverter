package net.yzimroni.replayconverter.convert;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.data.game.EntityMetadata;
import org.spacehq.mc.protocol.data.game.ItemStack;
import org.spacehq.mc.protocol.data.game.Position;
import org.spacehq.mc.protocol.data.game.Rotation;
import org.spacehq.mc.protocol.data.game.values.MagicValues;
import org.spacehq.mc.protocol.data.game.values.PlayerListEntry;
import org.spacehq.mc.protocol.data.game.values.PlayerListEntryAction;
import org.spacehq.mc.protocol.data.game.values.entity.EntityStatus;
import org.spacehq.mc.protocol.data.game.values.entity.FallingBlockData;
import org.spacehq.mc.protocol.data.game.values.entity.GlobalEntityType;
import org.spacehq.mc.protocol.data.game.values.entity.HangingDirection;
import org.spacehq.mc.protocol.data.game.values.entity.ProjectileData;
import org.spacehq.mc.protocol.data.game.values.entity.player.Animation;
import org.spacehq.mc.protocol.data.game.values.scoreboard.TeamAction;
import org.spacehq.mc.protocol.data.game.values.world.CustomSound;
import org.spacehq.mc.protocol.data.game.values.world.GenericSound;
import org.spacehq.mc.protocol.data.game.values.world.block.BlockChangeRecord;
import org.spacehq.mc.protocol.data.game.values.world.block.UpdatedTileType;
import org.spacehq.mc.protocol.data.game.values.world.effect.BreakBlockEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.BreakPotionEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.HardLandingEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.ParticleEffect;
import org.spacehq.mc.protocol.data.game.values.world.effect.RecordEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.SmokeEffectData;
import org.spacehq.mc.protocol.data.game.values.world.effect.SoundEffect;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerAnimationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerCollectItemPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerDestroyEntitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerMultiChunkDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerPlaySoundPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateSignPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.opennbt.tag.builtin.ListTag;
import org.spacehq.opennbt.tag.builtin.Tag;
import org.spacehq.packetlib.packet.Packet;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import net.yzimroni.bukkitanimations.data.action.ActionData;
import net.yzimroni.bukkitanimations.data.action.ActionType;
import net.yzimroni.replayconverter.data.EntityData;
import net.yzimroni.replayconverter.data.FullChunk;
import net.yzimroni.replayconverter.data.Location;
import net.yzimroni.replayconverter.data.PlayerData;
import net.yzimroni.replayconverter.data.Team;
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
		 * 
		 * ? ServerEntityHeadLookPacket
		 * ? ServerEntityVelocityPacket
		 * ? ServerBlockValuePacket
		 * ? ServerMapDataPacket
		 * ServerChatPacket
		 * 
		 * @formatter:on
		 */
		addHandler(ServerBlockBreakAnimPacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.BLOCK_BREAK_ANIMATION).data("entityId", p.getBreakerEntityId())
					.data("stage", p.getStage().ordinal()).data("location", p.getPosition()));
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
			if (c.getTracker().getTrackedEntities().containsKey(p.getCollectedEntityId())
					&& c.getTracker().getTrackedEntities().containsKey(p.getCollectorEntityId())) {
				c.addAction(new ActionData(ActionType.ENTITY_PICKUP).data("entityId", p.getCollectedEntityId())
						.data("playerId", p.getCollectorEntityId()));
			}
		});

		addHandler(ServerDestroyEntitiesPacket.class, (p, c) -> {
			for (int entityId : p.getEntityIds()) {
				if (c.getTracker().getTrackedEntities().containsKey(entityId)) {
					c.addAction(new ActionData(ActionType.DESPAWN_ENTITY).data("entityId", entityId));
					c.getTracker().getTrackedEntities().remove(entityId);
				}
			}
		});

		addHandler(ServerEntityEffectPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
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
					.data("particleId", p.getParticle().ordinal()).data("longDis", p.isLongDistance())
					.data("offset", new Vector(p.getOffsetX(), p.getOffsetY(), p.getOffsetZ()))
					.data("data", p.getVelocityOffset()).data("count", p.getAmount()).data("dataArray", p.getData()));
		});

		addHandler(ServerPlayerListEntryPacket.class, (p, c) -> {
			if (p.getAction() == PlayerListEntryAction.ADD_PLAYER) {
				for (PlayerListEntry player : p.getEntries()) {
					c.getTracker().getProfiles().put(player.getProfile().getId(), player.getProfile());
				}
			} else if (p.getAction() == PlayerListEntryAction.REMOVE_PLAYER) {
				for (PlayerListEntry player : p.getEntries()) {
					c.getTracker().getProfiles().remove(player.getProfile().getId());
				}
			}
		});

		addHandler(ServerSpawnPlayerPacket.class, (p, c) -> {
			GameProfile profile = c.getTracker().getProfiles().get(p.getUUID());
			if (profile == null) {
				return;
			}
			// Preconditions.checkNotNull(profile,
			// "Tried to spawn a player but no GameProfile were found to this uuid: " + new
			// Gson().toJson(p));
			Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
			ActionData action = new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.PLAYER)
					.data("name", profile.getName()).data("entityId", p.getEntityId()).data("location", location)
					.data("textures", profile.getProperty("textures"));
			PlayerData playerData = new PlayerData(p.getEntityId(), location, profile.getId(), profile.getName());
			c.getTracker().getTrackedEntities().put(p.getEntityId(), playerData);
			handleMetadata(action, EntityType.PLAYER, p.getMetadata());
			String displayName = c.getTracker().getDisplayName(profile.getName());
			if (displayName != null) {
				System.out.println("On spawn changing " + profile.getName() + " to " + displayName);
				playerData.setLastCustomName(displayName);
				action.data("customNameVisble", true).data("customName", displayName);
			}
			c.addAction(action);
		});

		addHandler(ServerSpawnMobPacket.class, (p, c) -> {
			EntityType type = Utils.getEntityType(p.getType());
			Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
			ActionData action = new ActionData(ActionType.SPAWN_ENTITY).data("type", type)
					.data("entityId", p.getEntityId()).data("location", location)
					.data("velocity", new Vector(p.getMotionX(), p.getMotionY(), p.getMotionZ()));
			c.getTracker().getTrackedEntities().put(p.getEntityId(), new EntityData(p.getEntityId(), type, location));

			handleMetadata(action, type, p.getMetadata());
			c.addAction(action);
		});

		addHandler(ServerSpawnExpOrbPacket.class, (p, c) -> {
			Location location = new Location(p.getX(), p.getY(), p.getZ());
			c.addAction(new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.EXPERIENCE_ORB)
					.data("entityId", p.getEntityId()).data("location", location));
			c.getTracker().getTrackedEntities().put(p.getEntityId(),
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
			c.getTracker().getTrackedEntities().put(p.getEntityId(), new EntityData(p.getEntityId(), type, location));

			c.addAction(action);
		});

		addHandler(ServerSpawnPaintingPacket.class, (p, c) -> {
			c.addAction(new ActionData(ActionType.SPAWN_ENTITY).data("type", EntityType.PAINTING)
					.data("entityId", p.getEntityId()).data("location", p.getPosition())
					.data("attachedFace", BlockFace.valueOf(p.getDirection().name()))
					.data("art", Art.getByName(p.getArt().name())));
			c.getTracker().getTrackedEntities().put(p.getEntityId(),
					new EntityData(p.getEntityId(), EntityType.PAINTING, new Location(p.getPosition())));
		});

		addHandler(ServerEntityMetadataPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				ActionData action = new ActionData(ActionType.UPDATE_ENTITY).data("entityId", p.getEntityId());
				handleMetadata(action, c.getTracker().getTrackedEntities().get(p.getEntityId()).getType(),
						p.getMetadata());
				c.addAction(action);
			}
		});

		addMultipleHandlers((p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				Location location = c.getTracker().getTrackedEntities().get(p.getEntityId()).getLocation();
				location = location.add(p.getMovementX(), p.getMovementY(), p.getMovementZ());
				if (p instanceof ServerEntityPositionRotationPacket || p instanceof ServerEntityRotationPacket) {
					location = location.changeLook(p.getYaw(), p.getPitch());
				}
				c.addAction(new ActionData(ActionType.ENTITY_MOVE).data("entityId", p.getEntityId()).data("location",
						location));
				c.getTracker().getTrackedEntities().get(p.getEntityId()).setLocation(location);
			}
		}, ServerEntityMovementPacket.class, ServerEntityPositionPacket.class, ServerEntityPositionRotationPacket.class,
				ServerEntityRotationPacket.class);

		addHandler(ServerEntityTeleportPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				Location location = new Location(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
				c.addAction(new ActionData(ActionType.ENTITY_MOVE).data("entityId", p.getEntityId()).data("location",
						location));
				c.getTracker().getTrackedEntities().get(p.getEntityId()).setLocation(location);
			}
		});

		addHandler(ServerUpdateTileEntityPacket.class, (p, c) -> {
			ActionData action = new ActionData(ActionType.UPDATE_BLOCKSTATE).data("location", p.getPosition());
			boolean shouldAdd = true;
			if (p.getType() == UpdatedTileType.MOB_SPAWNER) {
				action.data("spawnedType", Utils.getEntityType(p.getNBT().get("EntityId").getValue().toString()));
			} else if (p.getType() == UpdatedTileType.SKULL) {
				if (p.getNBT().contains("Rot")) {
					action.data("rotation", Utils.getBlockFace((byte) p.getNBT().get("Rot").getValue()));
				}
				if (p.getNBT().contains("SkullType")) {
					action.data("skullType",
							SkullType.values()[((Number) p.getNBT().get("SkullType").getValue()).intValue()]);
				}
				if (p.getNBT().contains("Owner")) {
					CompoundTag owner = p.getNBT().get("Owner");
					HashMap<String, Object> profile = new HashMap<String, Object>();
					if (owner.contains("Id")) {
						profile.put("id", owner.get("Id").getValue());
					}
					if (owner.contains("Name")) {
						profile.put("name", owner.get("Name").getValue());
					}
					HashMap<String, Object> textures = new HashMap<String, Object>();
					textures.put("name", "textures");
					ListTag texturesTag = ((CompoundTag) owner.get("Properties")).get("textures");
					CompoundTag texturesCompound = texturesTag.get(0);
					for (Tag t : texturesCompound.values()) {
						if (t.getName().equals("Value")) {
							textures.put("value", t.getValue());
						}
						if (t.getName().equals("Signature")) {
							textures.put("signature", t.getValue());
						}
					}
					profile.put("properties", Arrays.asList(textures));
					action.data("profile", profile);
				}
			} else if (p.getType() == UpdatedTileType.FLOWER_POT) {
				action.data("contents", new MaterialData((int) p.getNBT().get("Item").getValue(),
						((Number) p.getNBT().get("Data").getValue()).byteValue()));
			} else if (p.getType() == UpdatedTileType.BANNER) {
				// TODO
			} else {
				shouldAdd = false;
			}
			if (shouldAdd) {
				c.addAction(action);
			}
		});

		addHandler(ServerUpdateSignPacket.class, (p, c) -> {
			ActionData action = new ActionData(ActionType.UPDATE_BLOCKSTATE).data("location", p.getPosition())
					.data("lines", Arrays.stream(p.getLines()).map(Message::getFullText).collect(Collectors.toList()));
			c.addAction(action);
		});

		addHandler(ServerPlayEffectPacket.class, (p, c) -> {
			int effectId = 0;
			if (p.getEffect() instanceof ParticleEffect) {
				effectId = ((Integer) MagicValues.value(Integer.class, (ParticleEffect) p.getEffect())).intValue();
			} else if (p.getEffect() instanceof SoundEffect) {
				effectId = ((Integer) MagicValues.value(Integer.class, (SoundEffect) p.getEffect())).intValue();
			} else if (p.getEffect() != null) {
				throw new IllegalArgumentException("Unknown world effect type: " + p.getEffect());
			}
			String effectName = effectId + "";
			Effect effect = Effect.getById(effectId);
			if (effect != null) {
				effectName = effect.name();
			}

			// TODO fix it, use the effect name?

			int data = 0;
			if (p.getData() instanceof RecordEffectData) {
				data = ((RecordEffectData) p.getData()).getRecordId();
			} else if (p.getData() instanceof SmokeEffectData) {
				data = ((Integer) MagicValues.value(Integer.class, (SmokeEffectData) p.getData())).intValue();
			} else if (p.getData() instanceof BreakBlockEffectData) {
				data = ((BreakBlockEffectData) p.getData()).getBlockId();
			} else if (p.getData() instanceof BreakPotionEffectData) {
				data = ((BreakPotionEffectData) p.getData()).getPotionId();
			} else if (p.getData() instanceof HardLandingEffectData) {
				data = ((HardLandingEffectData) p.getData()).getDamagingDistance();
			} else if (p.getData() != null) {
				throw new IllegalArgumentException("Unknown world effect data type: " + p.getData());
			}
			c.addAction(new ActionData(ActionType.WORLD_EFFECT).data("effect", effectName)
					.data("location", p.getPosition()).data("data", data).data("disableRel", p.getBroadcast()));

		});

		addHandler(ServerEntityEquipmentPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				String equipmentSlotName = "";
				switch (p.getSlot()) {
					case 0:
						equipmentSlotName = "itemInHand";
						break;
					case 4:
						equipmentSlotName = "helmet";
						break;
					case 3:
						equipmentSlotName = "chestplate";
						break;
					case 2:
						equipmentSlotName = "leggings";
						break;
					case 1:
						equipmentSlotName = "boots";
						break;
					default:
						throw new IllegalArgumentException("Unknown EquipmentSlot: " + p.getSlot());
				}
				c.addAction(new ActionData(ActionType.UPDATE_ENTITY).data("entityId", p.getEntityId())
						.data(equipmentSlotName, Utils.serializeItem(p.getItem())));
			}

		});

		addHandler(ServerEntityRemoveEffectPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				int effectId = ((Integer) MagicValues.value(Integer.class, p.getEffect())).intValue();
				String effect = Utils.getPotionEffectName(effectId);
				c.addAction(new ActionData(ActionType.REMOVE_EFFECT).data("entityId", p.getEntityId()).data("effect",
						effect));
			}
		});

		addHandler(ServerEntityStatusPacket.class, (p, c) -> {
			if (c.getTracker().getTrackedEntities().containsKey(p.getEntityId())) {
				ActionData action = new ActionData(null).data("entityId", p.getEntityId());
				if (p.getStatus() == EntityStatus.LIVING_HURT) {
					action.setType(ActionType.ENTITY_DAMAGE);
				} else if (p.getStatus() == EntityStatus.DEAD) {
					action.setType(ActionType.ENTITY_DEATH);
				}
				// TODO handle more entity statuses
				if (action.getType() != null) {
					c.addAction(action);
				}
			}
		});

		addHandler(ServerPlaySoundPacket.class, (p, c) -> {
			Location location = new Location(p.getX(), p.getY(), p.getZ());
			String sound = "";
			if (p.getSound() instanceof CustomSound) {
				sound = ((CustomSound) p.getSound()).getName();
			} else if (p.getSound() instanceof GenericSound) {
				sound = (String) MagicValues.value(String.class, (GenericSound) p.getSound());
			} else {
				throw new IllegalArgumentException("Unknown sound type: " + p.getSound());
			}
			c.addAction(new ActionData(ActionType.SOUND).data("location", location).data("sound", sound)
					.data("volume", p.getVolume()).data("pitch", p.getPitch()));
		});

		addHandler(ServerChunkDataPacket.class, (p, c) -> {
			FullChunk chunk = new FullChunk(p.getX(), p.getZ(), p.getChunks());
			if (chunk.isEmpty()) {
				return;
			}
			File schematic = Utils.saveSchematic(chunk);
			Location location = new Location(p.getX() * 16, chunk.getStartY(), p.getZ() * 16);
			String schematicName = p.getX() + "_" + p.getZ() + "-"
					+ c.getTracker().getSchematicNumber().incrementAndGet();
			c.addExtraFile("schematics/" + schematicName + ".schematic", schematic);
			c.addAction(new ActionData(ActionType.LOAD_SCHEMATIC).data("location", location).data("schematic",
					schematicName));
		});

		addHandler(ServerMultiChunkDataPacket.class, (p, c) -> {
			for (int i = 0; i < p.getColumns(); i++) {
				int x = p.getX(i);
				int z = p.getZ(i);
				FullChunk chunk = new FullChunk(x, z, p.getChunks(i));
				if (chunk.isEmpty()) {
					continue;
				}
				File schematic = Utils.saveSchematic(chunk);
				Location location = new Location(x * 16, chunk.getStartY(), z * 16);
				String schematicName = x + "_" + z + "-" + c.getTracker().getSchematicNumber().incrementAndGet();
				c.addExtraFile("schematics/" + schematicName + ".schematic", schematic);
				c.addAction(new ActionData(ActionType.LOAD_SCHEMATIC).data("location", location).data("schematic",
						schematicName));
			}
		});

		addHandler(ServerTeamPacket.class, (p, c) -> {
			Team team = null;
			if (p.getAction() == TeamAction.CREATE) {
				team = new Team(p.getTeamName(), p.getPrefix(), p.getSuffix(), p.getNameTagVisibility(), p.getColor());
				c.getTracker().getTeams().add(team);
			} else {
				team = c.getTracker().getTeam(p.getTeamName());
			}

			if (p.getAction() == TeamAction.UPDATE) {
				team.setPrefix(p.getPrefix());
				team.setSuffix(p.getSuffix());
				team.setNameTagVisible(p.getNameTagVisibility());
				team.setColor(p.getColor());
			}

			if (p.getPlayers() != null && p.getPlayers().length > 0) {
				boolean add = p.getAction() == TeamAction.CREATE || p.getAction() == TeamAction.ADD_PLAYER;
				for (String player : p.getPlayers()) {
					if (add) {
						team.addPlayer(player);
					} else {
						team.removePlayer(player);
					}
					PlayerData playerData = c.getTracker().getPlayer(player);
					if (playerData != null) {
						String name = c.getTracker().getDisplayName(playerData.getName());
						playerData.setLastCustomName(name);
						System.out.println(
								"On team player update changing player " + playerData.getName() + " name to " + name);
						c.updateEntityName(playerData.getEntityId(), name);
					}
				}
			}

			if (p.getAction() == TeamAction.REMOVE) {
				c.getTracker().getTeams().remove(team);
			}

			if (p.getAction() == TeamAction.REMOVE || p.getAction() == TeamAction.UPDATE) {
				for (String player : team.getPlayers()) {
					PlayerData playerData = c.getTracker().getPlayer(player);
					if (playerData != null) {
						String name = c.getTracker().getDisplayName(playerData.getName());
						if (!Objects.equal(name, playerData.getLastCustomName())) {
							playerData.setLastCustomName(name);
							System.out.println("On team " + p.getAction() + " changing player " + playerData.getName()
									+ " name to " + name);
							c.updateEntityName(playerData.getEntityId(), name);
						}
					}
				}
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
			boolean onFire = (b & 1 << 0) != 0;
			boolean usingItem = (b & 1 << 4) != 0; // TODO
			boolean invisible = (b & 1 << 5) != 0;

			action.data("fireTicks", onFire ? Integer.MAX_VALUE : 0).data("visible", !invisible);
			if (type == EntityType.PLAYER) {
				boolean sneak = (b & 1 << 1) != 0;
				boolean sprint = (b & 1 << 3) != 0;
				action.data("sneaking", sneak).data("sprinting", sprint);
			}
		}
		if (LivingEntity.class.isAssignableFrom(type.getEntityClass())) {
			EntityMetadata nameTagData = Utils.getMetadataById(metadata, 2);
			if (nameTagData != null && type != EntityType.PLAYER) {
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

			if (ArmorStand.class.isAssignableFrom(type.getEntityClass())) {
				EntityMetadata bitMaskData = Utils.getMetadataById(metadata, 10);
				if (bitMaskData != null) {
					byte b = (byte) bitMaskData.getValue();
					boolean small = (b & 1) != 0;
					boolean gravity = (b & 2) != 0;
					boolean arms = (b & 4) != 0;
					boolean basePlate = (b & 8) != 0;
					boolean marker = (b & 16) != 0;
					action.data("small", small).data("gravity", !gravity).data("arms", arms)
							.data("basePlate", basePlate).data("marker", marker);
				}
				Map<Integer, String> poses = ImmutableMap.<Integer, String>builder().put(11, "headPose")
						.put(12, "bodyPose").put(13, "leftArmPose").put(14, "rightArmPose").put(15, "leftLegPose")
						.put(16, "rightLegPose").build();
				for (Entry<Integer, String> pose : poses.entrySet()) {
					EntityMetadata poseData = Utils.getMetadataById(metadata, pose.getKey());
					if (poseData != null) {
						Rotation rot = (Rotation) poseData.getValue();
						Map<String, Float> serialize = ImmutableMap.<String, Float>builder().put("x", rot.getPitch())
								.put("y", rot.getYaw()).put("z", rot.getRoll()).build();
						action.data(pose.getValue(), serialize);
					}
				}
			}
		}

		if (Item.class.isAssignableFrom(type.getEntityClass())) {
			EntityMetadata itemData = Utils.getMetadataById(metadata, 10);
			if (itemData != null) {
				ItemStack item = (ItemStack) itemData.getValue();
				action.data("item", Utils.serializeItem(item));
			}
		}

		/*
			 * @formatter:off
			 * TODO
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
			 * Firework
			 * Item Frame
			 * 
			 * 
			 * @formatter:on
			 */

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
