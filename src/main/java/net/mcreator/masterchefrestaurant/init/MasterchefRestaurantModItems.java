/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.masterchefrestaurant.item.inventory.SpatulaGoldenInventoryCapability;
import net.mcreator.masterchefrestaurant.item.inventory.ChefsDiaryInventoryCapability;
import net.mcreator.masterchefrestaurant.item.SpatulaGoldenItem;
import net.mcreator.masterchefrestaurant.item.ChefsDiaryItem;
import net.mcreator.masterchefrestaurant.item.BellItem;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public class MasterchefRestaurantModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MasterchefRestaurantMod.MODID);
	public static final DeferredItem<Item> SPATULA_GOLDEN;
	public static final DeferredItem<Item> SERVICE_TABLE;
	public static final DeferredItem<Item> CHAIR;
	public static final DeferredItem<Item> RECEPTION;
	public static final DeferredItem<Item> CLIENT_SPAWN_EGG;
	public static final DeferredItem<Item> RUG;
	public static final DeferredItem<Item> RUG_QUEUE;
	public static final DeferredItem<Item> CHEFS_DIARY;
	public static final DeferredItem<Item> BELL;
	static {
		SPATULA_GOLDEN = REGISTRY.register("spatula_golden", SpatulaGoldenItem::new);
		SERVICE_TABLE = block(MasterchefRestaurantModBlocks.SERVICE_TABLE);
		CHAIR = block(MasterchefRestaurantModBlocks.CHAIR);
		RECEPTION = block(MasterchefRestaurantModBlocks.RECEPTION);
		CLIENT_SPAWN_EGG = REGISTRY.register("client_spawn_egg", () -> new DeferredSpawnEggItem(MasterchefRestaurantModEntities.CLIENT, -1, -1, new Item.Properties()));
		RUG = block(MasterchefRestaurantModBlocks.RUG);
		RUG_QUEUE = block(MasterchefRestaurantModBlocks.RUG_QUEUE);
		CHEFS_DIARY = REGISTRY.register("chefs_diary", ChefsDiaryItem::new);
		BELL = REGISTRY.register("bell", BellItem::new);
	}

	// Start of user code block custom items
	// End of user code block custom items
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new SpatulaGoldenInventoryCapability(stack), SPATULA_GOLDEN.get());
		event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new ChefsDiaryInventoryCapability(stack), CHEFS_DIARY.get());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}