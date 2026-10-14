package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.Arrays;
import java.util.ArrayList;

public class ShowingClaimedAreaOfRestaurantProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
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
		String BoundaryData = "";
		if (!world.isClientSide()) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				locationsArray = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants", "locations",
						OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				locationArray = string2ArrayList((((("" + locationsArray).replace("\"", "")).replace("[", "")).replace("]", "")), ",");
				if (locationsArray.size() > 0) {
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
							BoundaryData = BoundaryData + "" + (PosX + 2.5) + ":" + PosZ + ":90;";
						}
						if (!locationArray.contains(left)) {
							BoundaryData = BoundaryData + "" + (PosX - 2.5) + ":" + PosZ + ":90;";
						}
						if (!locationArray.contains(up)) {
							BoundaryData = BoundaryData + "" + PosX + ":" + (PosZ + 2.5) + ":0;";
						}
						if (!locationArray.contains(down)) {
							BoundaryData = BoundaryData + "" + PosX + ":" + (PosZ - 2.5) + ":0;";
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
				if (entity instanceof Player _player13 && !_player13.level().isClientSide())
					_player13.displayClientMessage(Component.literal("You need to create a restaurant first.").withStyle(ChatFormatting.RED), true);
			}
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.RestaurantBoundaryData = BoundaryData;
				_vars.markSyncDirty();
			}
		}
	}

	private static ArrayList<Object> string2ArrayList(String text, String separator) {
		return new ArrayList<>(Arrays.asList(text.split(separator)));
	}
}