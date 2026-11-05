package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.ArrayList;

public class ConfirmCloseRestaurantProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity owner = null;
		double index = 0;
		double arrayIndex = 0;
		ArrayList<Object> openedRestaurants = new ArrayList<>();
		if (!world.isClientSide()) {
			owner = entity;
			index = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			if (index >= 0) {
				if (GetRestaurantLogicParameterProcedure.execute(index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
					openedRestaurants = OmnichefModVariables.MapVariables.get(world).RestaurantsOpen;
					arrayIndex = 0;
					for (int _i1 = 0; _i1 < (int) OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.size(); _i1++) {
						if ((openedRestaurants.get((int) arrayIndex) instanceof Double _doub2 ? _doub2 : 0.0D) == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
							openedRestaurants.remove((int) arrayIndex);
							MarkRestaurantsOpenDirtyProcedure.execute(world);
							break;
						}
						arrayIndex = arrayIndex + 1;
					}
					ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open");
					if (AreAllRestaurantGroupsSpawnedProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
						ModifyRestaurantLogicParameterProcedure.execute(true, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"menu_advance_ready");
					} else {
						ModifyRestaurantLogicParameterProcedure.execute(false, index, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"menu_advance_ready");
					}
					CreateOverlayDataTransferProcedure.execute(world, entity);
					CreateGUIDataTransferProcedure.execute(world, entity);
					FillSlotsMenuProcedure.execute(world, entity);
					if (owner instanceof Player _player6 && !_player6.level().isClientSide())
						_player6.displayClientMessage(Component.literal("Restaurant closed.").withStyle(ChatFormatting.WHITE), true);
				}
			}
		}
	}
}