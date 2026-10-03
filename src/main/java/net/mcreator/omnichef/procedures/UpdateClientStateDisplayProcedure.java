package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.omnichef.init.OmnichefModItems;

import javax.annotation.Nullable;

import java.util.UUID;

@EventBusSubscriber
public class UpdateClientStateDisplayProcedure {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		String clientState = "";
		Entity client = null;
		Entity displayItem = null;
		displayItem = entity;
		if (displayItem instanceof Display.ItemDisplay) {
			if (!(displayItem.getPersistentData().getString("clientUUID")).equals("")) {
				client = world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, (displayItem.getPersistentData().getString("clientUUID"))) : null;
				if (client != null) {
					clientState = client.getPersistentData().getString("state");
					if ((clientState).equals("order_wait")) {
						if (!(displayItem.getPersistentData().getString("state")).equals("order_wait")) {
							{
								Entity _ent = displayItem;
								double _tx = (client.getX());
								double _ty = (client.getY() + 2.5);
								double _tz = (client.getZ());
								_ent.teleportTo(_tx, _ty, _tz);
								if (_ent instanceof ServerPlayer _serverPlayer)
									_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
							}
							{
								Entity _ent = displayItem;
								if (!_ent.level().isClientSide() && _ent.getServer() != null) {
									_ent.getServer().getCommands()
											.performPrefixedCommand(
													new CommandSourceStack(CommandSource.NULL, _ent.position(), _ent.getRotationVector(), _ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4, _ent.getName().getString(),
															_ent.getDisplayName(), _ent.level().getServer(), _ent),
													("data modify entity @s item set value {id:\"" + "" + BuiltInRegistries.ITEM.getKey(OmnichefModItems.BELL.get()).toString() + "\",count:1}"));
								}
							}
							displayItem.getPersistentData().putString("state", "order_wait");
						}
					} else {
						if (!displayItem.level().isClientSide())
							displayItem.discard();
						client.getPersistentData().putBoolean("stateDisplayCreated", false);
					}
				} else {
					if (!displayItem.level().isClientSide())
						displayItem.discard();
					client.getPersistentData().putBoolean("stateDisplayCreated", false);
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}