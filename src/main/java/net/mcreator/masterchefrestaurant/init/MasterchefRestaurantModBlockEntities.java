/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.masterchefrestaurant.block.entity.ServiceTableBlockEntity;
import net.mcreator.masterchefrestaurant.block.entity.RugQueueBlockEntity;
import net.mcreator.masterchefrestaurant.block.entity.RugBlockEntity;
import net.mcreator.masterchefrestaurant.block.entity.ReceptionBlockEntity;
import net.mcreator.masterchefrestaurant.block.entity.ChairBlockEntity;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public class MasterchefRestaurantModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MasterchefRestaurantMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ServiceTableBlockEntity>> SERVICE_TABLE = register("service_table", MasterchefRestaurantModBlocks.SERVICE_TABLE, ServiceTableBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChairBlockEntity>> CHAIR = register("chair", MasterchefRestaurantModBlocks.CHAIR, ChairBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReceptionBlockEntity>> RECEPTION = register("reception", MasterchefRestaurantModBlocks.RECEPTION, ReceptionBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RugBlockEntity>> RUG = register("rug", MasterchefRestaurantModBlocks.RUG, RugBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RugQueueBlockEntity>> RUG_QUEUE = register("rug_queue", MasterchefRestaurantModBlocks.RUG_QUEUE, RugQueueBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SERVICE_TABLE.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CHAIR.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RECEPTION.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RUG.get(), SidedInvWrapper::new);
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RUG_QUEUE.get(), SidedInvWrapper::new);
	}
}