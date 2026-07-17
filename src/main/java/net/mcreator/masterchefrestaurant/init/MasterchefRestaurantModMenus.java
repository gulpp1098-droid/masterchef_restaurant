/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.Minecraft;

import net.mcreator.masterchefrestaurant.world.inventory.*;
import net.mcreator.masterchefrestaurant.network.MenuStateUpdateMessage;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.util.Map;

public class MasterchefRestaurantModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, MasterchefRestaurantMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<ClientOrderGUIMenu>> CLIENT_ORDER_GUI = REGISTRY.register("client_order_gui", () -> IMenuTypeExtension.create(ClientOrderGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ChefsDiaryGUIMenu>> CHEFS_DIARY_GUI = REGISTRY.register("chefs_diary_gui", () -> IMenuTypeExtension.create(ChefsDiaryGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ChefsDiaryFoodGUIMenu>> CHEFS_DIARY_FOOD_GUI = REGISTRY.register("chefs_diary_food_gui", () -> IMenuTypeExtension.create(ChefsDiaryFoodGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ChefsDiaryClientsGUIMenu>> CHEFS_DIARY_CLIENTS_GUI = REGISTRY.register("chefs_diary_clients_gui", () -> IMenuTypeExtension.create(ChefsDiaryClientsGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ChefsDiaryApplienceGUIMenu>> CHEFS_DIARY_APPLIENCE_GUI = REGISTRY.register("chefs_diary_applience_gui", () -> IMenuTypeExtension.create(ChefsDiaryApplienceGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ChefsDiaryStatsGUIMenu>> CHEFS_DIARY_STATS_GUI = REGISTRY.register("chefs_diary_stats_gui", () -> IMenuTypeExtension.create(ChefsDiaryStatsGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<RestaurantManagementGUIMenu>> RESTAURANT_MANAGEMENT_GUI = REGISTRY.register("restaurant_management_gui", () -> IMenuTypeExtension.create(RestaurantManagementGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CreateRestaurantGUIMenu>> CREATE_RESTAURANT_GUI = REGISTRY.register("create_restaurant_gui", () -> IMenuTypeExtension.create(CreateRestaurantGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SpatulaGuideGUIMenu>> SPATULA_GUIDE_GUI = REGISTRY.register("spatula_guide_gui", () -> IMenuTypeExtension.create(SpatulaGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BlocksGuideGUIMenu>> BLOCKS_GUIDE_GUI = REGISTRY.register("blocks_guide_gui", () -> IMenuTypeExtension.create(BlocksGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BlocksGuideP2GUIMenu>> BLOCKS_GUIDE_P_2_GUI = REGISTRY.register("blocks_guide_p_2_gui", () -> IMenuTypeExtension.create(BlocksGuideP2GUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<BlocksGuideP3GUIMenu>> BLOCKS_GUIDE_P_3_GUI = REGISTRY.register("blocks_guide_p_3_gui", () -> IMenuTypeExtension.create(BlocksGuideP3GUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<LocationGuideGUIMenu>> LOCATION_GUIDE_GUI = REGISTRY.register("location_guide_gui", () -> IMenuTypeExtension.create(LocationGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SetUpGuideGUIMenu>> SET_UP_GUIDE_GUI = REGISTRY.register("set_up_guide_gui", () -> IMenuTypeExtension.create(SetUpGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<OpenGuideGUIMenu>> OPEN_GUIDE_GUI = REGISTRY.register("open_guide_gui", () -> IMenuTypeExtension.create(OpenGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ServeGuideGUIMenu>> SERVE_GUIDE_GUI = REGISTRY.register("serve_guide_gui", () -> IMenuTypeExtension.create(ServeGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ExpLevelGuideGUIMenu>> EXP_LEVEL_GUIDE_GUI = REGISTRY.register("exp_level_guide_gui", () -> IMenuTypeExtension.create(ExpLevelGuideGUIMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ServeGuideP2GUIMenu>> SERVE_GUIDE_P_2_GUI = REGISTRY.register("serve_guide_p_2_gui", () -> IMenuTypeExtension.create(ServeGuideP2GUIMenu::new));

	public interface MenuAccessor {
		Map<String, Object> getMenuState();

		Map<Integer, Slot> getSlots();

		default void sendMenuStateUpdate(Player player, int elementType, String name, Object elementState, boolean needClientUpdate) {
			getMenuState().put(elementType + ":" + name, elementState);
			if (player instanceof ServerPlayer serverPlayer) {
				PacketDistributor.sendToPlayer(serverPlayer, new MenuStateUpdateMessage(elementType, name, elementState));
			} else if (player.level().isClientSide) {
				if (Minecraft.getInstance().screen instanceof MasterchefRestaurantModScreens.ScreenAccessor accessor && needClientUpdate)
					accessor.updateMenuState(elementType, name, elementState);
				PacketDistributor.sendToServer(new MenuStateUpdateMessage(elementType, name, elementState));
			}
		}

		default <T> T getMenuState(int elementType, String name, T defaultValue) {
			try {
				return (T) getMenuState().getOrDefault(elementType + ":" + name, defaultValue);
			} catch (ClassCastException e) {
				return defaultValue;
			}
		}
	}
}