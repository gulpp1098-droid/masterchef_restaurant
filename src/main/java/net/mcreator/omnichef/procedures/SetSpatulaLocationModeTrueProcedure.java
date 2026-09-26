package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SetSpatulaLocationModeTrueProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity Owner = null;
		Owner = entity;
		if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
			if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
					OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
				if (Owner instanceof Player _player)
					_player.closeContainer();
				{
					final String _tagName = "SelectingRestaurantLocation";
					final boolean _tagValue = true;
					CustomData.update(DataComponents.CUSTOM_DATA, (Owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
				}
				{
					final String _tagName = "ShowRestaurantArea";
					final boolean _tagValue = true;
					CustomData.update(DataComponents.CUSTOM_DATA, (Owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
				}
				{
					final String _tagName = "SpawnArea";
					final boolean _tagValue = true;
					CustomData.update(DataComponents.CUSTOM_DATA, (Owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
				}
			} else {
				if (Owner instanceof Player _player9 && !_player9.level().isClientSide())
					_player9.displayClientMessage(Component.literal("You cannot edit your restaurant while it is open.").withStyle(ChatFormatting.RED), true);
			}
		} else {
			if (Owner instanceof Player _player12 && !_player12.level().isClientSide())
				_player12.displayClientMessage(Component.literal("You need to create a restaurant first.").withStyle(ChatFormatting.RED), true);
		}
	}
}