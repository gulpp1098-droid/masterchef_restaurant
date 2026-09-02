package net.mcreator.masterchefrestaurant.command;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;

import net.mcreator.masterchefrestaurant.procedures.DebugModifyNumberProcedure;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

@EventBusSubscriber
public class RestDebugModifyNumberCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("rest_debug_modify_number").requires(source -> source.hasPermission(4)).then(
				Commands.argument("restaurant_ID", DoubleArgumentType.doubleArg(0)).then(Commands.argument("parameter_name", StringArgumentType.word()).then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					DebugModifyNumberProcedure.execute(world, arguments);
					return 0;
				})))));
	}

}