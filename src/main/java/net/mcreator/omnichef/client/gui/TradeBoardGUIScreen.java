package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.TradeBoardGUIMenu;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class TradeBoardGUIScreen extends AbstractContainerScreen<TradeBoardGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/trade_board_gui.png");

	public TradeBoardGUIScreen(TradeBoardGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 235;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + -105 || mouseX >= this.leftPos + -73 || mouseY < this.topPos + 61 || mouseY >= this.topPos + 93)
					break guiTools$itemDisplayTooltip0;
				boolean guiTools$displayMasked0 = !(net.mcreator.omnichef.procedures.TradeBoardSelectedOfferUsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack0 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack0 == null || guiTools$tooltipStack0.isEmpty())
					break guiTools$itemDisplayTooltip0;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack0, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + -43 || mouseX >= this.leftPos + -11 || mouseY < this.topPos + 61 || mouseY >= this.topPos + 93)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = !(net.mcreator.omnichef.procedures.TradeBoardSelectedOfferUsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + 20 || mouseX >= this.leftPos + 36 || mouseY < this.topPos + 50 || mouseY >= this.topPos + 66)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = !(net.mcreator.omnichef.procedures.TradeBoardOffer1UsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + 80 || mouseX >= this.leftPos + 96 || mouseY < this.topPos + 53 || mouseY >= this.topPos + 69)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = !(net.mcreator.omnichef.procedures.TradeBoardOffer2UsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + 144 || mouseX >= this.leftPos + 160 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = !(net.mcreator.omnichef.procedures.TradeBoardOffer3UsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + 40 || mouseX >= this.leftPos + 56 || mouseY < this.topPos + 108 || mouseY >= this.topPos + 124)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = !(net.mcreator.omnichef.procedures.TradeBoardOffer4UsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip6 : {
				if (!(true))
					break guiTools$itemDisplayTooltip6;
				if (mouseX < this.leftPos + 114 || mouseX >= this.leftPos + 130 || mouseY < this.topPos + 108 || mouseY >= this.topPos + 124)
					break guiTools$itemDisplayTooltip6;
				boolean guiTools$displayMasked6 = !(net.mcreator.omnichef.procedures.TradeBoardOffer5UsedProcedure.execute(entity));
				net.minecraft.world.item.ItemStack guiTools$tooltipStack6 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack6 == null || guiTools$tooltipStack6.isEmpty())
					break guiTools$itemDisplayTooltip6;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack6, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
		guiTools$orderedLayers : {
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 203;
				int guiTools$visibleHeight = 155;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/trade_board_backgound.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -13 + guiTools$xOffset, this.topPos + -3 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 203, 155);
			}
			if (this.enhanced_image_button_trade_board_sign != null && this.enhanced_image_button_trade_board_sign.visible) {
				this.enhanced_image_button_trade_board_sign.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_note_1_icon != null && this.enhanced_image_button_note_1_icon.visible) {
				this.enhanced_image_button_note_1_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_note_2_icon != null && this.enhanced_image_button_note_2_icon.visible) {
				this.enhanced_image_button_note_2_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_note_3_icon != null && this.enhanced_image_button_note_3_icon.visible) {
				this.enhanced_image_button_note_3_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_note_4_icon != null && this.enhanced_image_button_note_4_icon.visible) {
				this.enhanced_image_button_note_4_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_note_5_icon != null && this.enhanced_image_button_note_5_icon.visible) {
				this.enhanced_image_button_note_5_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 128;
				int guiTools$visibleHeight = 164;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_background_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -126 + guiTools$xOffset, this.topPos + 13 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 128, 164);
			}
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = !(net.mcreator.omnichef.procedures.TradeBoardSelectedOfferUsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 2.0f) * (this.leftPos + -105), (1.0F - 2.0f) * (this.topPos + 61), 0.0F);
					guiGraphics.pose().scale(2.0f, 2.0f, 1.0F);
					if (guiTools$displayMasked0) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -105, this.topPos + 61);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -105, this.topPos + 61);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + -105, this.topPos + 61);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = !(net.mcreator.omnichef.procedures.TradeBoardSelectedOfferUsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 2.0f) * (this.leftPos + -43), (1.0F - 2.0f) * (this.topPos + 61), 0.0F);
					guiGraphics.pose().scale(2.0f, 2.0f, 1.0F);
					if (guiTools$displayMasked1) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -43, this.topPos + 61);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -43, this.topPos + 61);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + -43, this.topPos + 61);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 22;
				int guiTools$visibleHeight = 22;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/preparation_arrow_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -68 + guiTools$xOffset, this.topPos + 66 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 22, 22);
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = !(net.mcreator.omnichef.procedures.TradeBoardOffer1UsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 20), (1.0F - 1.0f) * (this.topPos + 50), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked2) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 20, this.topPos + 50);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 20, this.topPos + 50);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + 20, this.topPos + 50);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = !(net.mcreator.omnichef.procedures.TradeBoardOffer2UsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 80), (1.0F - 1.0f) * (this.topPos + 53), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked3) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 80, this.topPos + 53);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 80, this.topPos + 53);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack3, this.leftPos + 80, this.topPos + 53);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = !(net.mcreator.omnichef.procedures.TradeBoardOffer3UsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 144), (1.0F - 1.0f) * (this.topPos + 49), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked4) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 144, this.topPos + 49);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 144, this.topPos + 49);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack4, this.leftPos + 144, this.topPos + 49);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = !(net.mcreator.omnichef.procedures.TradeBoardOffer4UsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 40), (1.0F - 1.0f) * (this.topPos + 108), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked5) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 40, this.topPos + 108);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 40, this.topPos + 108);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack5, this.leftPos + 40, this.topPos + 108);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay6 : {
				if (!(true))
					break guiTools$itemDisplay6;
				net.minecraft.world.item.ItemStack guiTools$displayStack6 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack6 == null || guiTools$displayStack6.isEmpty())
					break guiTools$itemDisplay6;
				boolean guiTools$displayMasked6 = !(net.mcreator.omnichef.procedures.TradeBoardOffer5UsedProcedure.execute(entity));
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 114), (1.0F - 1.0f) * (this.topPos + 108), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked6) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + 114, this.topPos + 108);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + 114, this.topPos + 108);
					}
					guiGraphics.renderItemDecorations(font, guiTools$displayStack6, this.leftPos + 114, this.topPos + 108);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -99 + guiTools$xOffset, this.topPos + 110 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/plateslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -40 + guiTools$xOffset, this.topPos + 110 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	@Override
	public void init() {
		super.init();
		enhanced_image_button_trade_board_sign = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 34, this.topPos + 2, 110, 27, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/trade_board_sign.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/trade_board_sign.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/trade_board_sign.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Trade Board";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.25f, 1.25f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_trade_board_sign);
		enhanced_image_button_note_1_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 3, this.topPos + 35, 53, 45, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_1_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_1_icon.png")), e -> {
					int x = TradeBoardGUIScreen.this.x;
					int y = TradeBoardGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.TradeBoardGUIButtonMessage(1, x, y, z));
						net.mcreator.omnichef.network.TradeBoardGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_1_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addWidget(enhanced_image_button_note_1_icon);
		enhanced_image_button_note_2_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 62, this.topPos + 32, 58, 58, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_2_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_2_icon.png")), e -> {
					int x = TradeBoardGUIScreen.this.x;
					int y = TradeBoardGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.TradeBoardGUIButtonMessage(2, x, y, z));
						net.mcreator.omnichef.network.TradeBoardGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_2_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addWidget(enhanced_image_button_note_2_icon);
		enhanced_image_button_note_3_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 124, this.topPos + 33, 50, 41, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_3_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_3_icon.png")), e -> {
					int x = TradeBoardGUIScreen.this.x;
					int y = TradeBoardGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.TradeBoardGUIButtonMessage(3, x, y, z));
						net.mcreator.omnichef.network.TradeBoardGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_3_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addWidget(enhanced_image_button_note_3_icon);
		enhanced_image_button_note_4_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 21, this.topPos + 90, 55, 44, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_4_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_4_icon.png")), e -> {
					int x = TradeBoardGUIScreen.this.x;
					int y = TradeBoardGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.TradeBoardGUIButtonMessage(4, x, y, z));
						net.mcreator.omnichef.network.TradeBoardGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_4_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addWidget(enhanced_image_button_note_4_icon);
		enhanced_image_button_note_5_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 98, this.topPos + 88, 49, 46, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_5_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_5_icon.png")), e -> {
					int x = TradeBoardGUIScreen.this.x;
					int y = TradeBoardGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.TradeBoardGUIButtonMessage(5, x, y, z));
						net.mcreator.omnichef.network.TradeBoardGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/note_5_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addWidget(enhanced_image_button_note_5_icon);
	}

	private static final boolean guiTools$enhancedImageButton = true;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_trade_board_sign;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_note_1_icon;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_note_2_icon;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_note_3_icon;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_note_4_icon;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_note_5_icon;

	private static net.minecraft.resources.ResourceLocation guiTools$buttonTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
		if (value == null || value.isBlank())
			return fallback;
		try {
			String texture = value.trim().replace('\\', '/');
			if (texture.indexOf(':') >= 0)
				return net.minecraft.resources.ResourceLocation.parse(texture);
			while (texture.startsWith("/"))
				texture = texture.substring(1);
			if (texture.startsWith("textures/screens/"))
				texture = texture.substring("textures/screens/".length());
			if (!texture.endsWith(".png"))
				texture += ".png";
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("omnichef", "textures/screens/" + texture);
		} catch (RuntimeException ignored) {
			return fallback;
		}
	}

	private static net.minecraft.resources.ResourceLocation guiTools$dynamicTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
		if (value == null || value.isBlank())
			return fallback;
		try {
			String texture = value.trim().replace('\\', '/');
			if (texture.indexOf(':') >= 0)
				return net.minecraft.resources.ResourceLocation.parse(texture);
			while (texture.startsWith("/"))
				texture = texture.substring(1);
			if (texture.startsWith("textures/screens/"))
				texture = texture.substring("textures/screens/".length());
			if (!texture.endsWith(".png"))
				texture += ".png";
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("omnichef", "textures/screens/" + texture);
		} catch (RuntimeException ignored) {
			return fallback;
		}
	}

	private static void guiTools$alphaBlit(net.minecraft.client.gui.GuiGraphics graphics, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		boolean wasBlending = org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_BLEND);
		com.mojang.blaze3d.systems.RenderSystem.enableBlend();
		com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
		try {
			graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
		} finally {
			if (!wasBlending)
				com.mojang.blaze3d.systems.RenderSystem.disableBlend();
		}
	}
}