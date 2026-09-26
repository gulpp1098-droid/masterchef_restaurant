package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModItems;

public class ConfirmRestaurantRelocationProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity owner = null;
		double CandidateX = 0;
		double CandidateZ = 0;
		double restaurantIndex = 0;
		com.google.gson.JsonArray newLocations = new com.google.gson.JsonArray();
		owner = entity;
		if (!world.isClientSide()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				if ((owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == OmnichefModItems.SPATULA_GOLDEN.get()) {
					if ((owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("RelocatingRestaurant")
							&& (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("RelocationCandidateSet")) {
						if (CanRelocateRestaurantProcedure.execute(world, entity)) {
							CandidateX = (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("RelocationCandidateX");
							CandidateZ = (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("RelocationCandidateZ");
							restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
							if (CanClaimRestaurantLocationProcedure.execute(world, CandidateX, CandidateZ, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
								newLocations.add((CandidateX + ":" + CandidateZ));
								ModifyRestaurantWholeArrayParameterProcedure.execute(newLocations, restaurantIndex, "restaurants", "locations", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
										OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
								DetachRestaurantReceptionProcedure.execute(world, entity);
								ScanningRestaurantAreasProcedure.execute(world, entity, "Tables");
								{
									final String _tagName = "RelocatingRestaurant";
									final boolean _tagValue = false;
									CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
								}
								{
									final String _tagName = "RelocationCandidateSet";
									final boolean _tagValue = false;
									CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
								}
								{
									final String _tagName = "SelectingRestaurantLocation";
									final boolean _tagValue = false;
									CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
								}
								{
									final String _tagName = "ShowRestaurantArea";
									final boolean _tagValue = true;
									CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
								}
								{
									final String _tagName = "SpawnArea";
									final boolean _tagValue = true;
									CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
								}
								if (entity instanceof Player _player)
									_player.closeContainer();
								if (owner instanceof Player _player28 && !_player28.level().isClientSide())
									_player28.displayClientMessage(Component.literal("Restaurant relocated successfully. Set up a new Reception before opening.").withStyle(ChatFormatting.GREEN), true);
							}
						}
					}
				}
			}
		}
	}
}