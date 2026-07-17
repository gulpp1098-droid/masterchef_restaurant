/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public class MasterchefRestaurantModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MasterchefRestaurantMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MASTER_CHEF = REGISTRY.register("master_chef",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.masterchef_restaurant.master_chef")).icon(() -> new ItemStack(MasterchefRestaurantModItems.SPATULA_GOLDEN.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MasterchefRestaurantModItems.SPATULA_GOLDEN.get());
				tabData.accept(MasterchefRestaurantModBlocks.SERVICE_TABLE.get().asItem());
				tabData.accept(MasterchefRestaurantModBlocks.CHAIR.get().asItem());
				tabData.accept(MasterchefRestaurantModBlocks.RECEPTION.get().asItem());
				tabData.accept(MasterchefRestaurantModItems.CLIENT_SPAWN_EGG.get());
				tabData.accept(MasterchefRestaurantModBlocks.RUG.get().asItem());
				tabData.accept(MasterchefRestaurantModBlocks.RUG_QUEUE.get().asItem());
				tabData.accept(MasterchefRestaurantModItems.CHEFS_DIARY.get());
			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(MasterchefRestaurantModItems.CLIENT_SPAWN_EGG.get());
		}
	}
}