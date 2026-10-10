package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.ClientOrderGUIMenu;
import net.mcreator.omnichef.procedures.PatianceReturnProcedure;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ClientOrderGUIScreen extends AbstractContainerScreen<ClientOrderGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/client_order_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/ordergui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/orderplace_icon.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/plate_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("omnichef:textures/screens/client_chuck_less_color.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("omnichef:textures/screens/menu_icon.png");
	private static final ResourceLocation SPRITE_0 = ResourceLocation.parse("omnichef:textures/screens/patiance_sprite.png");

	public ClientOrderGUIScreen(ClientOrderGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 234;
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
		guiTools$sizedTextLabelTooltips : {
			String guiTools$sizedLabelText0 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_1_name", "Food 1 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 23 && mouseY < this.topPos + 31 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText0, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText0), mouseX, mouseY);
			String guiTools$sizedLabelText1 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_2_name", "Food 2 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 40 && mouseY < this.topPos + 48 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText1, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText1), mouseX, mouseY);
			String guiTools$sizedLabelText2 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_3_name", "Food 3 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 58 && mouseY < this.topPos + 66 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText2, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText2), mouseX, mouseY);
			String guiTools$sizedLabelText3 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_4_name", "Food 4 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 75 && mouseY < this.topPos + 83 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText3, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText3), mouseX, mouseY);
			String guiTools$sizedLabelText4 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_5_name", "Food 5 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 93 && mouseY < this.topPos + 101 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText4, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText4), mouseX, mouseY);
			String guiTools$sizedLabelText5 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_6_name", "Food 6 Name");
			if (true && mouseX >= this.leftPos + 110 && mouseX < this.leftPos + 164 && mouseY >= this.topPos + 110 && mouseY < this.topPos + 118 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText5, 54, 0.75F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText5), mouseX, mouseY);
		}
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 14 || mouseY >= this.topPos + 30)
					break guiTools$itemDisplayTooltip0;
				boolean guiTools$displayMasked0 = false;
				if (guiTools$displayMasked0)
					break guiTools$itemDisplayTooltip0;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack0 == null || guiTools$tooltipStack0.isEmpty())
					break guiTools$itemDisplayTooltip0;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack0, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 32 || mouseY >= this.topPos + 48)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = false;
				if (guiTools$displayMasked1)
					break guiTools$itemDisplayTooltip1;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 49 || mouseY >= this.topPos + 65)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = false;
				if (guiTools$displayMasked2)
					break guiTools$itemDisplayTooltip2;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 67 || mouseY >= this.topPos + 83)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = false;
				if (guiTools$displayMasked3)
					break guiTools$itemDisplayTooltip3;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 84 || mouseY >= this.topPos + 100)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = false;
				if (guiTools$displayMasked4)
					break guiTools$itemDisplayTooltip4;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + 92 || mouseX >= this.leftPos + 108 || mouseY < this.topPos + 102 || mouseY >= this.topPos + 118)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = false;
				if (guiTools$displayMasked5)
					break guiTools$itemDisplayTooltip5;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedImages : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -1, this.topPos + 0, 0, 0, 176, 234, 176, 234);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 32, this.topPos + 87, 0, 0, 18, 18, 18, 18);
			guiTools$alphaBlit(guiGraphics, IMAGE_2, this.leftPos + 34, this.topPos + 94, 0, 0, 14, 8, 14, 8);
			guiTools$alphaBlit(guiGraphics, IMAGE_3, this.leftPos + 22, this.topPos + 31, 0, 0, 40, 54, 40, 54);
			guiTools$alphaBlit(guiGraphics, SPRITE_0, this.leftPos + 8, this.topPos + 41, 0, Mth.clamp((int) PatianceReturnProcedure.execute(entity) * 11, 0, 33), 11, 11, 11, 44);
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 14), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked0) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 92, this.topPos + 14);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + 92, this.topPos + 14);
					}
					if (!guiTools$displayMasked0)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack0, this.leftPos + 92, this.topPos + 14);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay1 : {
				if (!(true))
					break guiTools$itemDisplay1;
				net.minecraft.world.item.ItemStack guiTools$displayStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack1 == null || guiTools$displayStack1.isEmpty())
					break guiTools$itemDisplay1;
				boolean guiTools$displayMasked1 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 32), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked1) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 92, this.topPos + 32);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + 92, this.topPos + 32);
					}
					if (!guiTools$displayMasked1)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack1, this.leftPos + 92, this.topPos + 32);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay2 : {
				if (!(true))
					break guiTools$itemDisplay2;
				net.minecraft.world.item.ItemStack guiTools$displayStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack2 == null || guiTools$displayStack2.isEmpty())
					break guiTools$itemDisplay2;
				boolean guiTools$displayMasked2 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 49), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked2) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 92, this.topPos + 49);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + 92, this.topPos + 49);
					}
					if (!guiTools$displayMasked2)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack2, this.leftPos + 92, this.topPos + 49);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay3 : {
				if (!(true))
					break guiTools$itemDisplay3;
				net.minecraft.world.item.ItemStack guiTools$displayStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack3 == null || guiTools$displayStack3.isEmpty())
					break guiTools$itemDisplay3;
				boolean guiTools$displayMasked3 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 67), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked3) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 92, this.topPos + 67);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + 92, this.topPos + 67);
					}
					if (!guiTools$displayMasked3)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack3, this.leftPos + 92, this.topPos + 67);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay4 : {
				if (!(true))
					break guiTools$itemDisplay4;
				net.minecraft.world.item.ItemStack guiTools$displayStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack4 == null || guiTools$displayStack4.isEmpty())
					break guiTools$itemDisplay4;
				boolean guiTools$displayMasked4 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 84), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked4) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 92, this.topPos + 84);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + 92, this.topPos + 84);
					}
					if (!guiTools$displayMasked4)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack4, this.leftPos + 92, this.topPos + 84);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			if (this.enhanced_image_button_ribbonsmall_icon != null && this.enhanced_image_button_ribbonsmall_icon.visible) {
				this.enhanced_image_button_ribbonsmall_icon.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 92), (1.0F - 1.0f) * (this.topPos + 102), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked5) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 92, this.topPos + 102);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + 92, this.topPos + 102);
					}
					if (!guiTools$displayMasked5)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack5, this.leftPos + 92, this.topPos + 102);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$alphaBlit(guiGraphics, IMAGE_4, this.leftPos + 86, this.topPos + 8, 0, 0, 81, 116, 81, 116);
		}
		RenderSystem.disableBlend();
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.client_order_gui.label_serve_dish"), 17, 109, -16777216, false);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_1_name", "Food 1 Name"), 110, 23, 54, -12829636, false, 0.75F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_2_name", "Food 2 Name"), 110, 40, 54, -12829636, false, 0.75F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_3_name", "Food 3 Name"), 110, 58, 54, -12829636, false, 0.75F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_4_name", "Food 4 Name"), 110, 75, 54, -12829636, false, 0.75F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_5_name", "Food 5 Name"), 110, 93, 54, -12829636, false, 0.75F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_food_6_name", "Food 6 Name"), 110, 110, 54, -12829636, false, 0.75F, 2);
	}

	@Override
	public void init() {
		super.init();
		enhanced_image_button_ribbonsmall_icon = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 9, this.topPos + 10, 65, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/ribbonsmall_icon.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/ribbonsmall_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/ribbonsmall_icon.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Clients Order";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_ribbonsmall_icon);
	}

	private void guiTools$renderSizedTextLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int color, boolean shadow, float scale, int overflowMode) {
		if (text == null || scale <= 0.0F || boxWidth <= 0)
			return;
		int textWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		String displayed = overflowMode == 2 ? this.guiTools$ellipsizeSizedText(text, textWidth) : text;
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(this.leftPos + x, this.topPos + y, this.leftPos + x + boxWidth, this.topPos + y + Math.max(1, (int) Math.ceil(this.font.lineHeight * scale)));
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			guiGraphics.drawString(this.font, displayed, 0, 0, color, shadow);
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private boolean guiTools$isSizedTextTruncated(String text, int boxWidth, float scale) {
		return text != null && scale > 0.0F && this.font.width(text) > Math.max(1, (int) Math.floor(boxWidth / scale));
	}

	private String guiTools$ellipsizeSizedText(String value, int maxWidth) {
		if (this.font.width(value) <= maxWidth)
			return value;
		String ellipsis = "…";
		if (this.font.width(ellipsis) > maxWidth)
			return "";
		int low = 0, high = value.length();
		while (low < high) {
			int middle = (low + high + 1) >>> 1;
			if (this.font.width(value.substring(0, middle) + ellipsis) <= maxWidth)
				low = middle;
			else
				high = middle - 1;
		}
		return value.substring(0, low).stripTrailing() + ellipsis;
	}

	private static final boolean guiTools$enhancedImageButton = true;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_ribbonsmall_icon;

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