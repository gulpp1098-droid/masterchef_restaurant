package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModEntities;

import java.util.Arrays;
import java.util.ArrayList;

public class ShowingClaimedAreaOfRestaurantProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		com.google.gson.JsonArray locationsArray = new com.google.gson.JsonArray();
		ArrayList<Object> locationArray = new ArrayList<>();
		Entity entityArea = null;
		double index = 0;
		double PosX = 0;
		double PosZ = 0;
		double SectionX = 0;
		double SectionZ = 0;
		double PosY = 0;
		String right = "";
		String left = "";
		String up = "";
		String down = "";
		String locationString = "";
		if (!world.isClientSide()) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				locationsArray = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants", "locations",
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
				locationArray = string2ArrayList((((("" + locationsArray).replace("\"", "")).replace("[", "")).replace("]", "")), ",");
				PosY = y;
				if (locationsArray.size() > 0) {
					while (!world.getBlockState(BlockPos.containing(x, PosY, z)).canOcclude()) {
						PosY = PosY - 1;
					}
					index = 0;
					for (int _i1 = 0; _i1 < (int) locationsArray.size(); _i1++) {
						locationString = locationsArray.get((int) index).getAsString();
						SectionX = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(GetPartFromStringProcedure.execute(0, locationString));
						SectionZ = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(GetPartFromStringProcedure.execute(1, locationString));
						right = new java.text.DecimalFormat("0.0").format(SectionX + 1) + ":" + new java.text.DecimalFormat("0.0").format(SectionZ);
						left = new java.text.DecimalFormat("0.0").format(SectionX - 1) + ":" + new java.text.DecimalFormat("0.0").format(SectionZ);
						up = new java.text.DecimalFormat("0.0").format(SectionX) + ":" + new java.text.DecimalFormat("0.0").format(SectionZ + 1);
						down = new java.text.DecimalFormat("0.0").format(SectionX) + ":" + new java.text.DecimalFormat("0.0").format(SectionZ - 1);
						PosX = SectionX * 5 + 2.5;
						PosZ = SectionZ * 5 + 2.5;
						if (!locationArray.contains(right)) {
							(world instanceof ServerLevel _level7 ? MasterchefRestaurantModEntities.LOCATION_EDGE_RIGHT.get().spawn(_level7, BlockPos.containing(PosX, PosY, PosZ), MobSpawnType.MOB_SUMMONED) : null).getPersistentData()
									.putDouble("RestaurantID", entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						}
						if (!locationArray.contains(left)) {
							(world instanceof ServerLevel _level10 ? MasterchefRestaurantModEntities.LOCATION_EDGE_LEFT.get().spawn(_level10, BlockPos.containing(PosX, PosY, PosZ), MobSpawnType.MOB_SUMMONED) : null).getPersistentData()
									.putDouble("RestaurantID", entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						}
						if (!locationArray.contains(up)) {
							(world instanceof ServerLevel _level13 ? MasterchefRestaurantModEntities.LOCATION_EDGE.get().spawn(_level13, BlockPos.containing(PosX, PosY, PosZ), MobSpawnType.MOB_SUMMONED) : null).getPersistentData()
									.putDouble("RestaurantID", entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						}
						if (!locationArray.contains(down)) {
							(world instanceof ServerLevel _level16 ? MasterchefRestaurantModEntities.LOCATION_EDGE_DOWN.get().spawn(_level16, BlockPos.containing(PosX, PosY, PosZ), MobSpawnType.MOB_SUMMONED) : null).getPersistentData()
									.putDouble("RestaurantID", entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						}
						index = index + 1;
					}
				}
				{
					final String _tagName = "SpawnArea";
					final boolean _tagValue = false;
					CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean(_tagName, _tagValue));
				}
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Create Restaurant first!"), false);
			}
		}
	}

	private static ArrayList<Object> string2ArrayList(String text, String separator) {
		return new ArrayList<>(Arrays.asList(text.split(separator)));
	}
}