/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

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

import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public class OmnichefModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OmnichefMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MASTER_CHEF = REGISTRY.register("master_chef",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.omnichef.master_chef")).icon(() -> new ItemStack(OmnichefModItems.SPATULA_GOLDEN.get())).displayItems((parameters, tabData) -> {
				tabData.accept(OmnichefModItems.SPATULA_GOLDEN.get());
				tabData.accept(OmnichefModBlocks.SERVICE_TABLE.get().asItem());
				tabData.accept(OmnichefModBlocks.CHAIR.get().asItem());
				tabData.accept(OmnichefModBlocks.RECEPTION.get().asItem());
				tabData.accept(OmnichefModItems.CLIENT_SPAWN_EGG.get());
				tabData.accept(OmnichefModBlocks.RUG.get().asItem());
				tabData.accept(OmnichefModBlocks.RUG_QUEUE.get().asItem());
				tabData.accept(OmnichefModItems.CHEFS_DIARY.get());
				tabData.accept(OmnichefModItems.GOLD_COIN.get());
				tabData.accept(OmnichefModItems.SILVER_COIN.get());
				tabData.accept(OmnichefModItems.COPPER_COIN.get());
				tabData.accept(OmnichefModItems.CRITIC_SPAWN_EGG.get());
			}).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(OmnichefModItems.CLIENT_SPAWN_EGG.get());
			tabData.accept(OmnichefModItems.CRITIC_SPAWN_EGG.get());
		}
	}
}