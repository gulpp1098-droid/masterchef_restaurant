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
				tabData.accept(OmnichefModBlocks.PREPARATION_STATION.get().asItem());
				tabData.accept(OmnichefModBlocks.TRADE_BOARD.get().asItem());
				tabData.accept(OmnichefModItems.CHEFS_SET_HELMET.get());
				tabData.accept(OmnichefModItems.CHEFS_SET_CHESTPLATE.get());
				tabData.accept(OmnichefModItems.CHEFS_SET_LEGGINGS.get());
				tabData.accept(OmnichefModItems.CHEFS_SET_BOOTS.get());
				tabData.accept(OmnichefModItems.KNIFE.get());
				tabData.accept(OmnichefModItems.MUG.get());
			}).build());
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OMNICHEFFOOD = REGISTRY.register("omnicheffood",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.omnichef.omnicheffood")).icon(() -> new ItemStack(OmnichefModItems.KNIFE.get())).displayItems((parameters, tabData) -> {
				tabData.accept(OmnichefModItems.BUTTER.get());
				tabData.accept(OmnichefModItems.CHEESE.get());
				tabData.accept(OmnichefModItems.DOUGH.get());
				tabData.accept(OmnichefModItems.FLOUR.get());
				tabData.accept(OmnichefModItems.FRIED_MUSHROOMS.get());
				tabData.accept(OmnichefModItems.MILKBOTTLE.get());
				tabData.accept(OmnichefModItems.NOODLES.get());
				tabData.accept(OmnichefModItems.SAUCE_MAYO.get());
				tabData.accept(OmnichefModItems.SAUCE_MUSHROOM.get());
				tabData.accept(OmnichefModItems.SAUCE_RED.get());
				tabData.accept(OmnichefModItems.BEER.get());
				tabData.accept(OmnichefModItems.BREAD_PUMPKIN.get());
				tabData.accept(OmnichefModItems.BURGER.get());
				tabData.accept(OmnichefModItems.CHEESEBURGER.get());
				tabData.accept(OmnichefModItems.BURGER_DELUXE.get());
				tabData.accept(OmnichefModItems.CANDY_CHORUS.get());
				tabData.accept(OmnichefModItems.CANDY_HONEYCOMB.get());
				tabData.accept(OmnichefModItems.COFFEE.get());
				tabData.accept(OmnichefModItems.COFFEE_LATTE.get());
				tabData.accept(OmnichefModItems.COFFEE_LATTE_PUMPKIN_SPICE.get());
				tabData.accept(OmnichefModItems.COOKIE_HONEY.get());
				tabData.accept(OmnichefModItems.CRISPY_SEAWEED.get());
				tabData.accept(OmnichefModItems.SOUP_CURRY.get());
				tabData.accept(OmnichefModItems.DONUT.get());
				tabData.accept(OmnichefModItems.DUMPLINGS.get());
				tabData.accept(OmnichefModItems.FRIED_EGG.get());
				tabData.accept(OmnichefModItems.FRIES.get());
				tabData.accept(OmnichefModItems.GLAZED_CHICKEN.get());
				tabData.accept(OmnichefModItems.HOT_CHOCO.get());
				tabData.accept(OmnichefModItems.HOTDOG.get());
				tabData.accept(OmnichefModItems.HUMMUS_BEETROOT.get());
				tabData.accept(OmnichefModItems.ICE_CREAM_BERRY.get());
				tabData.accept(OmnichefModItems.ICE_CREAM_CHOCO.get());
				tabData.accept(OmnichefModItems.ICE_CREAM_CHORUS.get());
				tabData.accept(OmnichefModItems.ICE_CREAM_MELON.get());
				tabData.accept(OmnichefModItems.JAM_BERRY.get());
				tabData.accept(OmnichefModItems.JELLY_CHORUS.get());
				tabData.accept(OmnichefModItems.JERKY.get());
				tabData.accept(OmnichefModItems.JUICE_APPLE.get());
				tabData.accept(OmnichefModItems.JUICE_CACTUS.get());
				tabData.accept(OmnichefModItems.JUICE_CARROT.get());
				tabData.accept(OmnichefModItems.KEBAB.get());
				tabData.accept(OmnichefModItems.MEATBALLS.get());
				tabData.accept(OmnichefModItems.MUFFIN.get());
				tabData.accept(OmnichefModItems.MUFFIN_CHORUS.get());
				tabData.accept(OmnichefModItems.MUFFIN_GLOW_BERRY.get());
				tabData.accept(OmnichefModItems.CHICKEN_NUGGETS.get());
				tabData.accept(OmnichefModItems.OIL_SUNFLOWER.get());
				tabData.accept(OmnichefModItems.OMELETTE.get());
				tabData.accept(OmnichefModItems.OMELETTE_MUSHROOM.get());
				tabData.accept(OmnichefModItems.OMELETTE_WARPED.get());
				tabData.accept(OmnichefModItems.PANCAKES.get());
				tabData.accept(OmnichefModItems.PASTA_MUSHROOM.get());
				tabData.accept(OmnichefModItems.PIE_APPLE.get());
				tabData.accept(OmnichefModItems.PIE_APPLE_SLICE.get());
				tabData.accept(OmnichefModItems.PIE_BERRY.get());
				tabData.accept(OmnichefModItems.CHEESECAKE.get());
				tabData.accept(OmnichefModItems.PIE_CHORUS.get());
				tabData.accept(OmnichefModItems.PIE_FLORAL.get());
				tabData.accept(OmnichefModItems.PIE_MUSHROOM.get());
				tabData.accept(OmnichefModItems.PIE_BERRY_SLICE.get());
				tabData.accept(OmnichefModItems.CHEESECAKE_SLICE.get());
				tabData.accept(OmnichefModItems.PIE_CHORUS_SLICE.get());
				tabData.accept(OmnichefModItems.PIE_FLORAL_SLICE.get());
				tabData.accept(OmnichefModItems.PIE_MUSHROOM_SLICE.get());
				tabData.accept(OmnichefModItems.PIE_NETHER.get());
				tabData.accept(OmnichefModItems.PIE_NETHER_SLICE.get());
				tabData.accept(OmnichefModItems.PIZZA.get());
				tabData.accept(OmnichefModItems.PIZZA_SLICE.get());
				tabData.accept(OmnichefModItems.PREPARED_PUFFERFISH.get());
				tabData.accept(OmnichefModItems.SALAD.get());
				tabData.accept(OmnichefModItems.SALAD_ALGEA.get());
				tabData.accept(OmnichefModItems.SALAD_CHICKEN.get());
				tabData.accept(OmnichefModItems.SALAD_FLOWER.get());
				tabData.accept(OmnichefModItems.SALAD_OCEAN.get());
				tabData.accept(OmnichefModItems.SANDWICH.get());
				tabData.accept(OmnichefModItems.SHAKE_BERRY.get());
				tabData.accept(OmnichefModItems.SKEWER.get());
				tabData.accept(OmnichefModItems.SKEWER_RABBIT.get());
				tabData.accept(OmnichefModItems.SKEWER_TROPICAL_FISH.get());
				tabData.accept(OmnichefModItems.SOUP_BAMBOO_SHOOTS.get());
				tabData.accept(OmnichefModItems.SOUP_CHICKEN.get());
				tabData.accept(OmnichefModItems.SOUP_NETHER.get());
				tabData.accept(OmnichefModItems.SOUP_RAMEN.get());
				tabData.accept(OmnichefModItems.SOUP_TROPICAL_FISH.get());
				tabData.accept(OmnichefModItems.SOUP_PUFFERFISH.get());
				tabData.accept(OmnichefModItems.STUFFED_MUSHROOM.get());
				tabData.accept(OmnichefModItems.STUFFED_NAUTILUS.get());
				tabData.accept(OmnichefModItems.STUFFED_PUMPKIN.get());
				tabData.accept(OmnichefModItems.SUSHI.get());
				tabData.accept(OmnichefModItems.TACO.get());
				tabData.accept(OmnichefModItems.TEA.get());
				tabData.accept(OmnichefModItems.TEA_AZALEA.get());
				tabData.accept(OmnichefModItems.TEA_FLOWER.get());
				tabData.accept(OmnichefModItems.TOAST.get());
				tabData.accept(OmnichefModItems.TEMPURA_SEA_PICKLE.get());
				tabData.accept(OmnichefModItems.VODKA.get());
				tabData.accept(OmnichefModItems.WAFFLE.get());
				tabData.accept(OmnichefModItems.WINE.get());
				tabData.accept(OmnichefModItems.WRAP.get());
				tabData.accept(OmnichefModItems.WRAP_ALGEA.get());
				tabData.accept(OmnichefModItems.BATTER.get());
			}).withSearchBar().withTabsBefore(MASTER_CHEF.getId()).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			tabData.accept(OmnichefModItems.CLIENT_SPAWN_EGG.get());
			tabData.accept(OmnichefModItems.CRITIC_SPAWN_EGG.get());
		}
	}
}