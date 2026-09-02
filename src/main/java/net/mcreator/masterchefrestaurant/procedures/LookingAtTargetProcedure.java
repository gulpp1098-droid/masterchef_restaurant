package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

import net.mcreator.masterchefrestaurant.network.OverlayPacketMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import javax.annotation.Nullable;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Comparator;

@EventBusSubscriber
public class LookingAtTargetProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		return execute(null, world, x, y, z, entity);
	}

	private static boolean execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		double distance = 0;
		double index = 0;
		double checkX = 0;
		double checkY = 0;
		double checkZ = 0;
		Entity entityFound = null;
		MutableComponent resultCommand = Component.empty();
		boolean found = false;
		boolean blockFound = false;
		String textResult = "";
		String OverlayText = "";
		if (!world.isClientSide()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == MasterchefRestaurantModItems.SPATULA_GOLDEN.get() && (entity.getDisplayName().getString()).equals("Dev")) {
				found = false;
				blockFound = false;
				index = 1;
				for (int _i1 = 0; _i1 < 10; _i1++) {
					distance = index * 0.5;
					checkX = entity.getLookAngle().x * distance + x;
					checkY = entity.getLookAngle().y * distance + entity.getEyeHeight() + y;
					checkZ = entity.getLookAngle().z * distance + z;
					{
						final Vec3 _center = new Vec3(checkX, checkY, checkZ);
						for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(0.75 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
							if (entityiterator != null && !(entityiterator == entity)) {
								entityFound = entityiterator;
								found = true;
								break;
							}
						}
					}
					if (!world.isEmptyBlock(new BlockPos(
							entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX(),
							entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getY(),
							entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()))) {
						blockFound = true;
					}
					if (found) {
						if (getEntityCommandResult(entityFound, "data get entity @s NeoForgeData").getString().contains("{")) {
							textResult = getEntityCommandResult(entityFound, "data get entity @s NeoForgeData").getString().substring(
									(int) getEntityCommandResult(entityFound, "data get entity @s NeoForgeData").getString().indexOf("{") + "{".length(),
									(int) getEntityCommandResult(entityFound, "data get entity @s NeoForgeData").getString().lastIndexOf("}"));
							OverlayText = "";
							String _toSplit20 = textResult;
							String[] _array20 = _toSplit20.split(Pattern.quote(","));
							for (int _iter20 = 0; _iter20 < Math.max(1, _array20.length); _iter20++) {
								String stringiterator = _array20.length == 0 ? _toSplit20 : _array20[_iter20];
								OverlayText = OverlayText + "\n" + (stringiterator).strip();
							}
							if (entity instanceof ServerPlayer player21)
								PacketDistributor.sendToPlayer(player21, new OverlayPacketMessage(OverlayText));
							return true;
						}
					}
					if (blockFound) {
						if (getEntityCommandResult(entity, ("data get block "
								+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX() + " "
								+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getY() + " "
								+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()
								+ " NeoForgeData")).getString().contains("{")) {
							textResult = getEntityCommandResult(entity,
									("data get block " + entity
											.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()
											+ " "
											+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos()
													.getY()
											+ " "
											+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos()
													.getZ()
											+ " NeoForgeData"))
									.getString().substring(
											(int) getEntityCommandResult(entity,
													("data get block "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getX()
															+ " "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getY()
															+ " "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getZ()
															+ " NeoForgeData"))
													.getString().indexOf("{") + "{".length(),
											(int) getEntityCommandResult(entity,
													("data get block "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getX()
															+ " "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getY()
															+ " "
															+ entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity))
																	.getBlockPos().getZ()
															+ " NeoForgeData"))
													.getString().lastIndexOf("}"));
							OverlayText = "";
							String _toSplit34 = textResult;
							String[] _array34 = _toSplit34.split(Pattern.quote(","));
							for (int _iter34 = 0; _iter34 < Math.max(1, _array34.length); _iter34++) {
								String stringiterator = _array34.length == 0 ? _toSplit34 : _array34[_iter34];
								OverlayText = OverlayText + "\n" + (stringiterator).strip();
							}
							if (entity instanceof ServerPlayer player35)
								PacketDistributor.sendToPlayer(player35, new OverlayPacketMessage(OverlayText));
							return true;
						}
					}
					index = index + 1;
				}
			}
		}
		if (entity instanceof ServerPlayer player36)
			PacketDistributor.sendToPlayer(player36, new OverlayPacketMessage(""));
		return true;
	}

	private static MutableComponent getEntityCommandResult(Entity entity, String cmd) {
		MutableComponent result = Component.empty();
		if (!entity.level().isClientSide() && entity.getServer() != null)
			entity.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(customCommandSource(result), entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null, 4,
					entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity), cmd);
		List<Component> siblings = result.getSiblings();
		return !siblings.isEmpty() ? (MutableComponent) siblings.get(0) : result;
	}

	private static CommandSource customCommandSource(MutableComponent msgGetter) {
		return new CommandSource() {
			@Override
			public void sendSystemMessage(Component message) {
				msgGetter.append(message);
			}

			@Override
			public boolean acceptsSuccess() {
				return true;
			}

			@Override
			public boolean acceptsFailure() {
				return true;
			}

			@Override
			public boolean shouldInformAdmins() {
				return false;
			}
		};
	}
}