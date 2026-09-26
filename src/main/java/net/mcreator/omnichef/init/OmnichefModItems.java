/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

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

import net.mcreator.omnichef.item.inventory.ChefsDiaryInventoryCapability;
import net.mcreator.omnichef.item.*;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public class OmnichefModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(OmnichefMod.MODID);
	public static final DeferredItem<Item> SPATULA_GOLDEN;
	public static final DeferredItem<Item> SERVICE_TABLE;
	public static final DeferredItem<Item> CHAIR;
	public static final DeferredItem<Item> RECEPTION;
	public static final DeferredItem<Item> CLIENT_SPAWN_EGG;
	public static final DeferredItem<Item> RUG;
	public static final DeferredItem<Item> RUG_QUEUE;
	public static final DeferredItem<Item> CHEFS_DIARY;
	public static final DeferredItem<Item> BELL;
	public static final DeferredItem<Item> GOLD_COIN;
	public static final DeferredItem<Item> SILVER_COIN;
	public static final DeferredItem<Item> COPPER_COIN;
	public static final DeferredItem<Item> CRITIC_SPAWN_EGG;
	static {
		SPATULA_GOLDEN = REGISTRY.register("spatula_golden", SpatulaGoldenItem::new);
		SERVICE_TABLE = block(OmnichefModBlocks.SERVICE_TABLE);
		CHAIR = block(OmnichefModBlocks.CHAIR);
		RECEPTION = block(OmnichefModBlocks.RECEPTION);
		CLIENT_SPAWN_EGG = REGISTRY.register("client_spawn_egg", () -> new DeferredSpawnEggItem(OmnichefModEntities.CLIENT, -1, -1, new Item.Properties()));
		RUG = block(OmnichefModBlocks.RUG);
		RUG_QUEUE = block(OmnichefModBlocks.RUG_QUEUE);
		CHEFS_DIARY = REGISTRY.register("chefs_diary", ChefsDiaryItem::new);
		BELL = REGISTRY.register("bell", BellItem::new);
		GOLD_COIN = REGISTRY.register("gold_coin", GoldCoinItem::new);
		SILVER_COIN = REGISTRY.register("silver_coin", SilverCoinItem::new);
		COPPER_COIN = REGISTRY.register("copper_coin", CopperCoinItem::new);
		CRITIC_SPAWN_EGG = REGISTRY.register("critic_spawn_egg", () -> new DeferredSpawnEggItem(OmnichefModEntities.CRITIC, -52327, -1, new Item.Properties()));
	}

	// Start of user code block custom items
	// End of user code block custom items
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new ChefsDiaryInventoryCapability(stack), CHEFS_DIARY.get());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return block(block, new Item.Properties());
	}

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}