/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.masterchefrestaurant.block.ServiceTableBlock;
import net.mcreator.masterchefrestaurant.block.RugQueueBlock;
import net.mcreator.masterchefrestaurant.block.RugBlock;
import net.mcreator.masterchefrestaurant.block.ReceptionBlock;
import net.mcreator.masterchefrestaurant.block.ChairBlock;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class MasterchefRestaurantModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MasterchefRestaurantMod.MODID);
	public static final DeferredBlock<Block> SERVICE_TABLE;
	public static final DeferredBlock<Block> CHAIR;
	public static final DeferredBlock<Block> RECEPTION;
	public static final DeferredBlock<Block> RUG;
	public static final DeferredBlock<Block> RUG_QUEUE;
	static {
		SERVICE_TABLE = REGISTRY.register("service_table", ServiceTableBlock::new);
		CHAIR = REGISTRY.register("chair", ChairBlock::new);
		RECEPTION = REGISTRY.register("reception", ReceptionBlock::new);
		RUG = REGISTRY.register("rug", RugBlock::new);
		RUG_QUEUE = REGISTRY.register("rug_queue", RugQueueBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}