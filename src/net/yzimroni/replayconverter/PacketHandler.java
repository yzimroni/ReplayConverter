package net.yzimroni.replayconverter;

import java.util.HashMap;
import java.util.function.BiConsumer;

import org.spacehq.mc.protocol.data.game.Position;
import org.spacehq.mc.protocol.data.game.values.entity.GlobalEntityType;
import org.spacehq.mc.protocol.data.game.values.entity.player.Animation;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerAnimationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerCollectItemPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerDestroyEntitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import org.spacehq.packetlib.packet.Packet;

import net.yzimroni.bukkitanimations.data.action.ActionData;
import net.yzimroni.bukkitanimations.data.action.ActionType;

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

	private static void initHandlers() {
		/*
		 * @formatter:off
		 * 
		 * ServerSpawnExpOrbPacket
		 * ServerSpawnMobPacket
		 * ServerSpawnObjectPacket
		 * ServerSpawnPaintingPacket
		 * ServerSpawnPlayerPacket
		 * 
		 * 
		 * ServerEntityEffectPacket
		 * ServerEntityEquipmentPacket
		 * ServerEntityHeadLookPacket
		 * ServerEntityMetadataPacket
		 * ServerEntityMovementPacket
		 * ServerEntityNBTUpdatePacket
		 * ServerEntityPositionPacket
		 * ServerEntityPositionRotationPacket
		 * ? ServerEntityPropertiesPacket
		 * ServerEntityRemoveEffectPacket
		 * ServerEntityRotationPacket
		 * ? ServerEntityStatusPacket
		 * ServerEntityTeleportPacket
		 * ? ServerEntityVelocityPacket
		 * 
		 * ServerBlockChangePacket
		 * ? ServerBlockValuePacket
		 * ServerChunkDataPacket
		 * ServerExplosionPacket
		 * ? ServerMapDataPacket
		 * ServerMultiBlockChangePacket
		 * ServerMultiChunkDataPacket
		 * ServerPlayEffectPacket
		 * ? ServerPlaySoundPacket
		 * ServerSpawnParticlePacket
		 * ServerUpdateSignPacket
		 * ? ServerUpdateTileEntityPacket
		 * ServerChatPacket
		 * ServerPlayerListEntryPacket
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
			c.addAction(new ActionData(ActionType.ENTITY_PICKUP).data("entityId", p.getCollectedEntityId())
					.data("playerId", p.getCollectorEntityId()));
		});

		addHandler(ServerDestroyEntitiesPacket.class, (p, c) -> {
			for (int entityId : p.getEntityIds()) {
				c.addAction(new ActionData(ActionType.DESPAWN_ENTITY).data("entityId", entityId));
			}
		});

		addHandler(Packet.class, (p, c) -> {

		});
	}

	private static <T extends Packet> void addHandler(Class<T> packet, BiConsumer<T, ReplayConverter> handler) {
		HANDLERS.put(packet, handler);
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
