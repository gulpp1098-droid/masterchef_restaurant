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

public class SelectRestaurantRelocationCandidateProcedure {
	public static void execute(LevelAccessor world, double x, double z, Entity entity) {
		if (entity == null)
			return;
		double CandidateX = 0;
		double CandidateZ = 0;
		double indexRestaurant = 0;
		double locationIndex = 0;
		double locX = 0;
		double locZ = 0;
		com.google.gson.JsonArray locations = new com.google.gson.JsonArray();
		Entity owner = null;
		String string = "";
		boolean fail = false;
		owner = entity;
		if (!world.isClientSide()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				if ((owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == OmnichefModItems.SPATULA_GOLDEN.get()) {
					if ((owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("RelocatingRestaurant")) {
						if (CanRelocateRestaurantProcedure.execute(world, entity)) {
							CandidateX = Math.floor(x / 5);
							CandidateZ = Math.floor(z / 5);
							indexRestaurant = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
							locations = GetRestaurantArrayParameterProcedure.execute(indexRestaurant, "restaurants", "locations", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
									OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
							locationIndex = 0;
							for (int _i1 = 0; _i1 < (int) locations.size(); _i1++) {
								string = locations.get((int) locationIndex).getAsString();
								locX = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(0, string));
								locZ = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(1, string));
								if (CandidateX == locX && CandidateZ == locZ) {
									if (owner instanceof Player _player12 && !_player12.level().isClientSide())
										_player12.displayClientMessage(Component.literal("This area already belongs to your restaurant.").withStyle(ChatFormatting.GREEN), true);
									fail = true;
									break;
								}
								locationIndex = locationIndex + 1;
							}
							if (!fail) {
								if (!CanClaimRestaurantLocationProcedure.execute(world, CandidateX, CandidateZ, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
									if (owner instanceof Player _player15 && !_player15.level().isClientSide())
										_player15.displayClientMessage(Component.literal("This area is too close to another restaurant.").withStyle(ChatFormatting.GREEN), true);
								} else {
									{
										final String _tagName = "RelocationCandidateX";
										final double _tagValue = CandidateX;
										CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putDouble(_tagName, _tagValue));
									}
									{
										final String _tagName = "RelocationCandidateZ";
										final double _tagValue = CandidateZ;
										CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putDouble(_tagName, _tagValue));
									}
									{
										final String _tagName = "RelocationCandidateSet";
										final boolean _tagValue = true;
										CustomData.update(DataComponents.CUSTOM_DATA, (owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
									}
								}
							}
						}
					}
				}
			}
		}
	}
}