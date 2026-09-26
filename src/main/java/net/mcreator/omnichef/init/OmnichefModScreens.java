/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.gui.*;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(OmnichefModMenus.CLIENT_ORDER_GUI.get(), ClientOrderGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_GUI.get(), ChefsDiaryGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_FOOD_GUI.get(), ChefsDiaryFoodGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_CLIENTS_GUI.get(), ChefsDiaryClientsGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_APPLIENCE_GUI.get(), ChefsDiaryApplienceGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_STATS_GUI.get(), ChefsDiaryStatsGUIScreen::new);
		event.register(OmnichefModMenus.RESTAURANT_MANAGEMENT_GUI.get(), RestaurantManagementGUIScreen::new);
		event.register(OmnichefModMenus.CREATE_RESTAURANT_GUI.get(), CreateRestaurantGUIScreen::new);
		event.register(OmnichefModMenus.SPATULA_GUIDE_GUI.get(), SpatulaGuideGUIScreen::new);
		event.register(OmnichefModMenus.BLOCKS_GUIDE_GUI.get(), BlocksGuideGUIScreen::new);
		event.register(OmnichefModMenus.BLOCKS_GUIDE_P_2_GUI.get(), BlocksGuideP2GUIScreen::new);
		event.register(OmnichefModMenus.LOCATION_GUIDE_GUI.get(), LocationGuideGUIScreen::new);
		event.register(OmnichefModMenus.SET_UP_GUIDE_GUI.get(), SetUpGuideGUIScreen::new);
		event.register(OmnichefModMenus.OPEN_GUIDE_GUI.get(), OpenGuideGUIScreen::new);
		event.register(OmnichefModMenus.SERVE_GUIDE_GUI.get(), ServeGuideGUIScreen::new);
		event.register(OmnichefModMenus.EXP_LEVEL_GUIDE_GUI.get(), ExpLevelGuideGUIScreen::new);
		event.register(OmnichefModMenus.SERVE_GUIDE_P_2_GUI.get(), ServeGuideP2GUIScreen::new);
		event.register(OmnichefModMenus.MENU_GUI.get(), MenuGUIScreen::new);
		event.register(OmnichefModMenus.EXP_LEVEL_GUIDE_P_2_GUI.get(), ExpLevelGuideP2GUIScreen::new);
		event.register(OmnichefModMenus.LOCATION_GUIDE_P_2_GUI.get(), LocationGuideP2GUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_FOOD_TIER_GUI.get(), ChefsDiaryFoodTierGUIScreen::new);
		event.register(OmnichefModMenus.BLOCKS_GUIDE_P_3_GUI.get(), BlocksGuideP3GUIScreen::new);
		event.register(OmnichefModMenus.BLOCKS_GUIDE_P_4_GUI.get(), BlocksGuideP4GUIScreen::new);
		event.register(OmnichefModMenus.BLOCKS_GUIDE_P_5_GUI.get(), BlocksGuideP5GUIScreen::new);
		event.register(OmnichefModMenus.RELOCATE_CONFIRMATION_GUI.get(), RelocateConfirmationGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_MENU_GUI.get(), ChefsDiaryMenuGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_TIPS_GUI.get(), ChefsDiaryTipsGUIScreen::new);
		event.register(OmnichefModMenus.CHEFS_DIARY_TIPS_P_2_GUI.get(), ChefsDiaryTipsP2GUIScreen::new);
		event.register(OmnichefModMenus.CARDS_PICK_GUI.get(), CardsPickGUIScreen::new);
	}

	public interface ScreenAccessor {
		void updateMenuState(int elementType, String name, Object elementState);
	}
}