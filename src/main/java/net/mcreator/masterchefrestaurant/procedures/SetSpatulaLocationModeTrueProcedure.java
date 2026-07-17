package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class SetSpatulaLocationModeTrueProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity Owner = null;
		Owner = entity;
		if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
			if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
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
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("You can NOT edit restaurant when it is open!"), false);
			}
		}
	}
}