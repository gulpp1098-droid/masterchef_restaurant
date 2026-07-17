package net.mcreator.masterchefrestaurant.item;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.masterchefrestaurant.world.inventory.RestaurantManagementGUIMenu;
import net.mcreator.masterchefrestaurant.procedures.SpatulaInHandOnTickProcedure;
import net.mcreator.masterchefrestaurant.procedures.SetRestaurantLocalizationWithSpatulaProcedure;
import net.mcreator.masterchefrestaurant.procedures.IsSpatulaInSelectingLocationStateProcedure;

import java.util.List;

import io.netty.buffer.Unpooled;

public class SpatulaGoldenItem extends Item {
	public SpatulaGoldenItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)
				.attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
						.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.masterchef_restaurant.spatula_golden.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		if (entity instanceof ServerPlayer serverPlayer) {
			if (IsSpatulaInSelectingLocationStateProcedure.execute(ar.getObject())) {
				serverPlayer.openMenu(new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("Spatula Golden");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
						packetBuffer.writeBlockPos(entity.blockPosition());
						packetBuffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
						return new RestaurantManagementGUIMenu(id, inventory, packetBuffer);
					}
				}, buf -> {
					buf.writeBlockPos(entity.blockPosition());
					buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
				});
			}
		}
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		SetRestaurantLocalizationWithSpatulaProcedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			SpatulaInHandOnTickProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
	}
}