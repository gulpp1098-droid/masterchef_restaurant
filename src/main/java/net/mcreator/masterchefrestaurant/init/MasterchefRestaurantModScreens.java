/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.masterchefrestaurant.client.gui.*;

@EventBusSubscriber(Dist.CLIENT)
public class MasterchefRestaurantModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(MasterchefRestaurantModMenus.CLIENT_ORDER_GUI.get(), ClientOrderGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CHEFS_DIARY_GUI.get(), ChefsDiaryGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CHEFS_DIARY_FOOD_GUI.get(), ChefsDiaryFoodGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CHEFS_DIARY_CLIENTS_GUI.get(), ChefsDiaryClientsGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CHEFS_DIARY_APPLIENCE_GUI.get(), ChefsDiaryApplienceGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CHEFS_DIARY_STATS_GUI.get(), ChefsDiaryStatsGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.RESTAURANT_MANAGEMENT_GUI.get(), RestaurantManagementGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.CREATE_RESTAURANT_GUI.get(), CreateRestaurantGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.SPATULA_GUIDE_GUI.get(), SpatulaGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.BLOCKS_GUIDE_GUI.get(), BlocksGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.BLOCKS_GUIDE_P_2_GUI.get(), BlocksGuideP2GUIScreen::new);
		event.register(MasterchefRestaurantModMenus.BLOCKS_GUIDE_P_3_GUI.get(), BlocksGuideP3GUIScreen::new);
		event.register(MasterchefRestaurantModMenus.LOCATION_GUIDE_GUI.get(), LocationGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.SET_UP_GUIDE_GUI.get(), SetUpGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.OPEN_GUIDE_GUI.get(), OpenGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.SERVE_GUIDE_GUI.get(), ServeGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.EXP_LEVEL_GUIDE_GUI.get(), ExpLevelGuideGUIScreen::new);
		event.register(MasterchefRestaurantModMenus.SERVE_GUIDE_P_2_GUI.get(), ServeGuideP2GUIScreen::new);
	}

	public interface ScreenAccessor {
		void updateMenuState(int elementType, String name, Object elementState);
	}
}