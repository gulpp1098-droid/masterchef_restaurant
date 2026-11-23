package net.mcreator.omnichef.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.ChefsDiaryItemDetailsGUIMenu;
import net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryItemDetailsGUIScreen extends AbstractContainerScreen<ChefsDiaryItemDetailsGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_last_page_icon;
	private ImageButton imagebutton_next_page_icon;
	private ImageButton imagebutton_itemslot_icon;
	private ImageButton imagebutton_itemslot_icon_copy;
	private ImageButton imagebutton_itemslot_icon_copy_2;
	private ImageButton imagebutton_itemslot_icon_copy_3;
	private ImageButton imagebutton_itemslot_icon_copy_4;
	private ImageButton imagebutton_itemslot_icon_copy_5;
	private ImageButton imagebutton_itemslot_icon_copy_6;
	private ImageButton imagebutton_itemslot_icon_copy_7;
	private ImageButton imagebutton_itemslot_icon_copy_8;
	private ImageButton imagebutton_itemslot_icon_copy_9;
	private ImageButton imagebutton_itemslot_icon_copy_3_copy;
	private ImageButton imagebutton_itemslot_icon_copy_6_copy;
	private ImageButton imagebutton_itemslot_icon_copy_copy;
	private ImageButton imagebutton_itemslot_icon_copy_4_copy;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/chefs_diary_item_details_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");

	public ChefsDiaryItemDetailsGUIScreen(ChefsDiaryItemDetailsGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 0;
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
			String guiTools$sizedLabelText9 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock", "Need 1 to Unlock");
			if (true && mouseX >= this.leftPos + 36 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + -66 && mouseY < this.topPos + -56 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText9, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText9), mouseX, mouseY);
			String guiTools$sizedLabelText10 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy", "Need 1 to Unlock");
			if (true && mouseX >= this.leftPos + 36 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + -41 && mouseY < this.topPos + -31 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText10, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText10), mouseX, mouseY);
			String guiTools$sizedLabelText11 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy", "Need 1 to Unlock");
			if (true && mouseX >= this.leftPos + 36 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + -15 && mouseY < this.topPos + -5 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText11, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText11), mouseX, mouseY);
			String guiTools$sizedLabelText12 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy_2", "Need 1 to Unlock");
			if (true && mouseX >= this.leftPos + 36 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + 10 && mouseY < this.topPos + 20 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText12, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText12), mouseX, mouseY);
			String guiTools$sizedLabelText13 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy_3", "Need 1 to Unlock");
			if (true && mouseX >= this.leftPos + 36 && mouseX < this.leftPos + 156 && mouseY >= this.topPos + 35 && mouseY < this.topPos + 45 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText13, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText13), mouseX, mouseY);
			String guiTools$sizedLabelText14 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_page_1_1", "Page 1 / 1");
			if (true && mouseX >= this.leftPos + 32 && mouseX < this.leftPos + 152 && mouseY >= this.topPos + 60 && mouseY < this.topPos + 70 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText14, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText14), mouseX, mouseY);
		}
		guiTools$itemDisplayTooltips : {
			guiTools$itemDisplayTooltip0 : {
				if (!(true))
					break guiTools$itemDisplayTooltip0;
				if (mouseX < this.leftPos + -118 || mouseX >= this.leftPos + -102 || mouseY < this.topPos + -56 || mouseY >= this.topPos + -40)
					break guiTools$itemDisplayTooltip0;
				boolean guiTools$displayMasked0 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack0 == null || guiTools$tooltipStack0.isEmpty())
					break guiTools$itemDisplayTooltip0;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack0, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip1 : {
				if (!(true))
					break guiTools$itemDisplayTooltip1;
				if (mouseX < this.leftPos + -92 || mouseX >= this.leftPos + -76 || mouseY < this.topPos + -56 || mouseY >= this.topPos + -40)
					break guiTools$itemDisplayTooltip1;
				boolean guiTools$displayMasked1 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack1 = menu.getMenuState(3, "2", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack1 == null || guiTools$tooltipStack1.isEmpty())
					break guiTools$itemDisplayTooltip1;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack1, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip2 : {
				if (!(true))
					break guiTools$itemDisplayTooltip2;
				if (mouseX < this.leftPos + -66 || mouseX >= this.leftPos + -50 || mouseY < this.topPos + -56 || mouseY >= this.topPos + -40)
					break guiTools$itemDisplayTooltip2;
				boolean guiTools$displayMasked2 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack2 = menu.getMenuState(3, "3", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack2 == null || guiTools$tooltipStack2.isEmpty())
					break guiTools$itemDisplayTooltip2;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack2, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip3 : {
				if (!(true))
					break guiTools$itemDisplayTooltip3;
				if (mouseX < this.leftPos + -118 || mouseX >= this.leftPos + -102 || mouseY < this.topPos + -30 || mouseY >= this.topPos + -14)
					break guiTools$itemDisplayTooltip3;
				boolean guiTools$displayMasked3 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack3 = menu.getMenuState(3, "4", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack3 == null || guiTools$tooltipStack3.isEmpty())
					break guiTools$itemDisplayTooltip3;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack3, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip4 : {
				if (!(true))
					break guiTools$itemDisplayTooltip4;
				if (mouseX < this.leftPos + -92 || mouseX >= this.leftPos + -76 || mouseY < this.topPos + -30 || mouseY >= this.topPos + -14)
					break guiTools$itemDisplayTooltip4;
				boolean guiTools$displayMasked4 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack4 = menu.getMenuState(3, "5", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack4 == null || guiTools$tooltipStack4.isEmpty())
					break guiTools$itemDisplayTooltip4;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack4, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip5 : {
				if (!(true))
					break guiTools$itemDisplayTooltip5;
				if (mouseX < this.leftPos + -66 || mouseX >= this.leftPos + -50 || mouseY < this.topPos + -30 || mouseY >= this.topPos + -14)
					break guiTools$itemDisplayTooltip5;
				boolean guiTools$displayMasked5 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack5 == null || guiTools$tooltipStack5.isEmpty())
					break guiTools$itemDisplayTooltip5;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack5, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip6 : {
				if (!(true))
					break guiTools$itemDisplayTooltip6;
				if (mouseX < this.leftPos + -118 || mouseX >= this.leftPos + -102 || mouseY < this.topPos + -3 || mouseY >= this.topPos + 13)
					break guiTools$itemDisplayTooltip6;
				boolean guiTools$displayMasked6 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack6 = menu.getMenuState(3, "7", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack6 == null || guiTools$tooltipStack6.isEmpty())
					break guiTools$itemDisplayTooltip6;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack6, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip7 : {
				if (!(true))
					break guiTools$itemDisplayTooltip7;
				if (mouseX < this.leftPos + -92 || mouseX >= this.leftPos + -76 || mouseY < this.topPos + -3 || mouseY >= this.topPos + 13)
					break guiTools$itemDisplayTooltip7;
				boolean guiTools$displayMasked7 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack7 = menu.getMenuState(3, "8", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack7 == null || guiTools$tooltipStack7.isEmpty())
					break guiTools$itemDisplayTooltip7;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack7, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip8 : {
				if (!(true))
					break guiTools$itemDisplayTooltip8;
				if (mouseX < this.leftPos + -66 || mouseX >= this.leftPos + -50 || mouseY < this.topPos + -3 || mouseY >= this.topPos + 13)
					break guiTools$itemDisplayTooltip8;
				boolean guiTools$displayMasked8 = !(false);
				net.minecraft.world.item.ItemStack guiTools$tooltipStack8 = menu.getMenuState(3, "9", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack8 == null || guiTools$tooltipStack8.isEmpty())
					break guiTools$itemDisplayTooltip8;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack8, mouseX, mouseY);
			}
			guiTools$itemDisplayTooltip9 : {
				if (!(true))
					break guiTools$itemDisplayTooltip9;
				if (mouseX < this.leftPos + -140 || mouseX >= this.leftPos + -116 || mouseY < this.topPos + -98 || mouseY >= this.topPos + -74)
					break guiTools$itemDisplayTooltip9;
				boolean guiTools$displayMasked9 = false;
				if (guiTools$displayMasked9)
					break guiTools$itemDisplayTooltip9;
				net.minecraft.world.item.ItemStack guiTools$tooltipStack9 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$tooltipStack9 == null || guiTools$tooltipStack9.isEmpty())
					break guiTools$itemDisplayTooltip9;
				guiGraphics.renderTooltip(font, guiTools$tooltipStack9, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedLayers : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
			guiTools$itemDisplay0 : {
				if (!(true))
					break guiTools$itemDisplay0;
				net.minecraft.world.item.ItemStack guiTools$displayStack0 = menu.getMenuState(3, "1", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack0 == null || guiTools$displayStack0.isEmpty())
					break guiTools$itemDisplay0;
				boolean guiTools$displayMasked0 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -118), (1.0F - 1.0f) * (this.topPos + -56), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked0) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -118, this.topPos + -56);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack0, this.leftPos + -118, this.topPos + -56);
					}
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
				boolean guiTools$displayMasked1 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -92), (1.0F - 1.0f) * (this.topPos + -56), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked1) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -92, this.topPos + -56);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack1, this.leftPos + -92, this.topPos + -56);
					}
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
				boolean guiTools$displayMasked2 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -66), (1.0F - 1.0f) * (this.topPos + -56), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked2) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -66, this.topPos + -56);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack2, this.leftPos + -66, this.topPos + -56);
					}
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
				boolean guiTools$displayMasked3 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -118), (1.0F - 1.0f) * (this.topPos + -30), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked3) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + -118, this.topPos + -30);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack3, this.leftPos + -118, this.topPos + -30);
					}
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
				boolean guiTools$displayMasked4 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -92), (1.0F - 1.0f) * (this.topPos + -30), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked4) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + -92, this.topPos + -30);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack4, this.leftPos + -92, this.topPos + -30);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay5 : {
				if (!(true))
					break guiTools$itemDisplay5;
				net.minecraft.world.item.ItemStack guiTools$displayStack5 = menu.getMenuState(3, "6", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack5 == null || guiTools$displayStack5.isEmpty())
					break guiTools$itemDisplay5;
				boolean guiTools$displayMasked5 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -66), (1.0F - 1.0f) * (this.topPos + -30), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked5) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -66, this.topPos + -30);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack5, this.leftPos + -66, this.topPos + -30);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay6 : {
				if (!(true))
					break guiTools$itemDisplay6;
				net.minecraft.world.item.ItemStack guiTools$displayStack6 = menu.getMenuState(3, "7", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack6 == null || guiTools$displayStack6.isEmpty())
					break guiTools$itemDisplay6;
				boolean guiTools$displayMasked6 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -118), (1.0F - 1.0f) * (this.topPos + -3), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked6) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + -118, this.topPos + -3);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack6, this.leftPos + -118, this.topPos + -3);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay7 : {
				if (!(true))
					break guiTools$itemDisplay7;
				net.minecraft.world.item.ItemStack guiTools$displayStack7 = menu.getMenuState(3, "8", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack7 == null || guiTools$displayStack7.isEmpty())
					break guiTools$itemDisplay7;
				boolean guiTools$displayMasked7 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -92), (1.0F - 1.0f) * (this.topPos + -3), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked7) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack7, this.leftPos + -92, this.topPos + -3);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack7, this.leftPos + -92, this.topPos + -3);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay8 : {
				if (!(true))
					break guiTools$itemDisplay8;
				net.minecraft.world.item.ItemStack guiTools$displayStack8 = menu.getMenuState(3, "9", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack8 == null || guiTools$displayStack8.isEmpty())
					break guiTools$itemDisplay8;
				boolean guiTools$displayMasked8 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + -66), (1.0F - 1.0f) * (this.topPos + -3), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked8) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.8f, 0.0f, 0.0f, 0.5f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack8, this.leftPos + -66, this.topPos + -3);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack8, this.leftPos + -66, this.topPos + -3);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			if (red_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= red_bookmark.getX() && mouseY >= red_bookmark.getY() && mouseX < red_bookmark.getX() + 35 && mouseY < red_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, red_bookmark.getX(), red_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (yellow_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= yellow_bookmark.getX() && mouseY >= yellow_bookmark.getY() && mouseX < yellow_bookmark.getX() + 35 && mouseY < yellow_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, yellow_bookmark.getX(), yellow_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (green_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= green_bookmark.getX() && mouseY >= green_bookmark.getY() && mouseX < green_bookmark.getX() + 35 && mouseY < green_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, green_bookmark.getX(), green_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (gray_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= gray_bookmark.getX() && mouseY >= gray_bookmark.getY() && mouseX < gray_bookmark.getX() + 35 && mouseY < gray_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, gray_bookmark.getX(), gray_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (blue_bookmark.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= blue_bookmark.getX() && mouseY >= blue_bookmark.getY() && mouseX < blue_bookmark.getX() + 35 && mouseY < blue_bookmark.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, blue_bookmark.getX(), blue_bookmark.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/base_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 148 + guiTools$xOffset, this.topPos + -97 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/food_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -68 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/clients_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -39 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + -10 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 18;
				int guiTools$visibleHeight = 18;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/stats_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 149 + guiTools$xOffset, this.topPos + 19 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 18, 18);
			}
			if (red_bookmark_copy.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_purple.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= red_bookmark_copy.getX() && mouseY >= red_bookmark_copy.getY() && mouseX < red_bookmark_copy.getX() + 35 && mouseY < red_bookmark_copy.getY() + 26;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, red_bookmark_copy.getX(), red_bookmark_copy.getY(), 0, 0, 35, 26, 35, 26);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 15;
				int guiTools$visibleHeight = 15;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -177 + guiTools$xOffset, this.topPos + -96 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 15, 15);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 26;
				int guiTools$visibleHeight = 26;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -141 + guiTools$xOffset, this.topPos + -99 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 26, 26);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 80;
				int guiTools$visibleHeight = 80;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/crafting_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -124 + guiTools$xOffset, this.topPos + -62 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 80, 80);
			}
			guiTools$advancedImage0 : {
				if (button_small_arrow_right.visible) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 12;
					int guiTools$visibleHeight = 12;
					button_small_arrow_right.setX(this.leftPos + -37 + guiTools$xOffset);
					button_small_arrow_right.setY(this.topPos + -62 + guiTools$yOffset);
					button_small_arrow_right.setWidth(guiTools$visibleWidth);
					button_small_arrow_right.setHeight(guiTools$visibleHeight);
					net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_right.png");
					net.minecraft.resources.ResourceLocation guiTools$drawTexture = guiTools$normalTexture;
					net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
					net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
					boolean guiTools$over = mouseX >= button_small_arrow_right.getX() && mouseY >= button_small_arrow_right.getY() && mouseX < button_small_arrow_right.getX() + button_small_arrow_right.getWidth()
							&& mouseY < button_small_arrow_right.getY() + button_small_arrow_right.getHeight();
					boolean guiTools$down = guiTools$over && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
					guiTools$drawTexture = guiTools$down ? guiTools$pressedTexture : guiTools$over ? guiTools$hoveredTexture : guiTools$normalTexture;
					if (guiTools$drawTexture != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0) {
						guiTools$alphaBlit(guiGraphics, guiTools$drawTexture, this.leftPos + -37 + guiTools$xOffset, this.topPos + -62 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 12, 12);
					}
				}
			}
			guiTools$advancedImage1 : {
				if (button_small_arrow_left.visible) {
					int guiTools$xOffset = 0;
					int guiTools$yOffset = 0;
					int guiTools$visibleWidth = 12;
					int guiTools$visibleHeight = 12;
					button_small_arrow_left.setX(this.leftPos + -142 + guiTools$xOffset);
					button_small_arrow_left.setY(this.topPos + -62 + guiTools$yOffset);
					button_small_arrow_left.setWidth(guiTools$visibleWidth);
					button_small_arrow_left.setHeight(guiTools$visibleHeight);
					net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_left.png");
					net.minecraft.resources.ResourceLocation guiTools$drawTexture = guiTools$normalTexture;
					net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
					net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
					boolean guiTools$over = mouseX >= button_small_arrow_left.getX() && mouseY >= button_small_arrow_left.getY() && mouseX < button_small_arrow_left.getX() + button_small_arrow_left.getWidth()
							&& mouseY < button_small_arrow_left.getY() + button_small_arrow_left.getHeight();
					boolean guiTools$down = guiTools$over && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
					guiTools$drawTexture = guiTools$down ? guiTools$pressedTexture : guiTools$over ? guiTools$hoveredTexture : guiTools$normalTexture;
					if (guiTools$drawTexture != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0) {
						guiTools$alphaBlit(guiGraphics, guiTools$drawTexture, this.leftPos + -142 + guiTools$xOffset, this.topPos + -62 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 12, 12);
					}
				}
			}
			guiTools$itemDisplay9 : {
				if (!(true))
					break guiTools$itemDisplay9;
				net.minecraft.world.item.ItemStack guiTools$displayStack9 = menu.getMenuState(3, "0", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack9 == null || guiTools$displayStack9.isEmpty())
					break guiTools$itemDisplay9;
				boolean guiTools$displayMasked9 = false;
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.5f) * (this.leftPos + -140), (1.0F - 1.5f) * (this.topPos + -98), 0.0F);
					guiGraphics.pose().scale(1.5f, 1.5f, 1.0F);
					if (guiTools$displayMasked9) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack9, this.leftPos + -140, this.topPos + -98);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack9, this.leftPos + -140, this.topPos + -98);
					}
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			if (enhanced_image_button_bookmark_magenta.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_magenta.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_bookmark_magenta.getX() && mouseY >= enhanced_image_button_bookmark_magenta.getY() && mouseX < enhanced_image_button_bookmark_magenta.getX() + 21
						&& mouseY < enhanced_image_button_bookmark_magenta.getY() + 21;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_bookmark_magenta.getX(), enhanced_image_button_bookmark_magenta.getY(), 0, 0, 21, 21, 21, 21);
			}
			if (enhanced_image_button_bookmark_magenta_copy.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_orange.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_bookmark_magenta_copy.getX() && mouseY >= enhanced_image_button_bookmark_magenta_copy.getY() && mouseX < enhanced_image_button_bookmark_magenta_copy.getX() + 21
						&& mouseY < enhanced_image_button_bookmark_magenta_copy.getY() + 21;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_bookmark_magenta_copy.getX(), enhanced_image_button_bookmark_magenta_copy.getY(), 0, 0, 21, 21, 21, 21);
			}
			if (enhanced_image_button_bookmark_magenta_copy_copy.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blood.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_bookmark_magenta_copy_copy.getX() && mouseY >= enhanced_image_button_bookmark_magenta_copy_copy.getY()
						&& mouseX < enhanced_image_button_bookmark_magenta_copy_copy.getX() + 21 && mouseY < enhanced_image_button_bookmark_magenta_copy_copy.getY() + 21;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_bookmark_magenta_copy_copy.getX(), enhanced_image_button_bookmark_magenta_copy_copy.getY(), 0, 0, 21, 21, 21, 21);
			}
			if (enhanced_image_button_bookmark_magenta_copy_copy_copy.visible) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_white.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean guiTools$mouseOverButton = mouseX >= enhanced_image_button_bookmark_magenta_copy_copy_copy.getX() && mouseY >= enhanced_image_button_bookmark_magenta_copy_copy_copy.getY()
						&& mouseX < enhanced_image_button_bookmark_magenta_copy_copy_copy.getX() + 21 && mouseY < enhanced_image_button_bookmark_magenta_copy_copy_copy.getY() + 21;
				boolean guiTools$mousePressed = guiTools$mouseOverButton
						&& org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = guiTools$mousePressed ? guiTools$pressedTexture : guiTools$mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, enhanced_image_button_bookmark_magenta_copy_copy_copy.getX(), enhanced_image_button_bookmark_magenta_copy_copy_copy.getY(), 0, 0, 21, 21, 21, 21);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 13;
				int guiTools$visibleHeight = 13;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/filter_all.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 16 + guiTools$xOffset, this.topPos + 88 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 13, 13);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 13;
				int guiTools$visibleHeight = 13;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/filter_unlockable.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 39 + guiTools$xOffset, this.topPos + 88 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 13, 13);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 13;
				int guiTools$visibleHeight = 13;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/filter_locked.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 62 + guiTools$xOffset, this.topPos + 88 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 13, 13);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 13;
				int guiTools$visibleHeight = 13;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/filter_unlocked.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 85 + guiTools$xOffset, this.topPos + 88 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 13, 13);
			}
			guiTools$itemDisplay10 : {
				if (!(true))
					break guiTools$itemDisplay10;
				net.minecraft.world.item.ItemStack guiTools$displayStack10 = menu.getMenuState(3, "10", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack10 == null || guiTools$displayStack10.isEmpty())
					break guiTools$itemDisplay10;
				boolean guiTools$displayMasked10 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 13), (1.0F - 1.0f) * (this.topPos + -72), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked10) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack10, this.leftPos + 13, this.topPos + -72);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack10, this.leftPos + 13, this.topPos + -72);
					}
					if (!guiTools$displayMasked10)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack10, this.leftPos + 13, this.topPos + -72);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay11 : {
				if (!(true))
					break guiTools$itemDisplay11;
				net.minecraft.world.item.ItemStack guiTools$displayStack11 = menu.getMenuState(3, "11", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack11 == null || guiTools$displayStack11.isEmpty())
					break guiTools$itemDisplay11;
				boolean guiTools$displayMasked11 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 13), (1.0F - 1.0f) * (this.topPos + -46), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked11) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack11, this.leftPos + 13, this.topPos + -46);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack11, this.leftPos + 13, this.topPos + -46);
					}
					if (!guiTools$displayMasked11)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack11, this.leftPos + 13, this.topPos + -46);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay12 : {
				if (!(true))
					break guiTools$itemDisplay12;
				net.minecraft.world.item.ItemStack guiTools$displayStack12 = menu.getMenuState(3, "12", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack12 == null || guiTools$displayStack12.isEmpty())
					break guiTools$itemDisplay12;
				boolean guiTools$displayMasked12 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 13), (1.0F - 1.0f) * (this.topPos + -20), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked12) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack12, this.leftPos + 13, this.topPos + -20);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack12, this.leftPos + 13, this.topPos + -20);
					}
					if (!guiTools$displayMasked12)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack12, this.leftPos + 13, this.topPos + -20);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay13 : {
				if (!(true))
					break guiTools$itemDisplay13;
				net.minecraft.world.item.ItemStack guiTools$displayStack13 = menu.getMenuState(3, "13", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack13 == null || guiTools$displayStack13.isEmpty())
					break guiTools$itemDisplay13;
				boolean guiTools$displayMasked13 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 13), (1.0F - 1.0f) * (this.topPos + 5), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked13) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack13, this.leftPos + 13, this.topPos + 5);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack13, this.leftPos + 13, this.topPos + 5);
					}
					if (!guiTools$displayMasked13)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack13, this.leftPos + 13, this.topPos + 5);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
			guiTools$itemDisplay14 : {
				if (!(true))
					break guiTools$itemDisplay14;
				net.minecraft.world.item.ItemStack guiTools$displayStack14 = menu.getMenuState(3, "14", net.minecraft.world.item.ItemStack.EMPTY);
				if (guiTools$displayStack14 == null || guiTools$displayStack14.isEmpty())
					break guiTools$itemDisplay14;
				boolean guiTools$displayMasked14 = !(false);
				guiGraphics.pose().pushPose();
				try {
					guiGraphics.pose().translate((1.0F - 1.0f) * (this.leftPos + 13), (1.0F - 1.0f) * (this.topPos + 31), 0.0F);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0F);
					if (guiTools$displayMasked14) {
						com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0f);
						try {
							guiGraphics.renderFakeItem(guiTools$displayStack14, this.leftPos + 13, this.topPos + 31);
						} finally {
							com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
						}
					} else {
						guiGraphics.renderFakeItem(guiTools$displayStack14, this.leftPos + 13, this.topPos + 31);
					}
					if (!guiTools$displayMasked14)
						guiGraphics.renderItemDecorations(font, guiTools$displayStack14, this.leftPos + 13, this.topPos + 31);
				} finally {
					guiGraphics.pose().popPose();
				}
			}
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
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label", "Item name"), -113, -98, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy", "Tier and type"), -113, -89, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_2", "Recipe: 1 / 1"), -122, 21, 81, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy", "State"), -113, -80, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy_copy", "Requirements"), -141, -70, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy_copy_copy", "Cooking ingredient only"), -142, 33, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy_copy_copy_copy", "Used in 27 dishes"), 7, -98, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy_copy_copy_copy_2", "Unlocks 4 new recipes"), -142, 43, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_text_label_copy_copy_copy_copy_copy_copy", "Name of filter"), 7, -88, 120, -12829636, false, 1.00F, 0);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock", "Need 1 to Unlock"), 36, -66, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy", "Need 1 to Unlock"), 36, -41, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy", "Need 1 to Unlock"), 36, -15, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy_2", "Need 1 to Unlock"), 36, 10, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_need_1_to_unlock_copy_copy_3", "Need 1 to Unlock"), 36, 35, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_page_1_1", "Page 1 / 1"), 32, 60, 120, -12829636, false, 1.00F, 2);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -141, this.topPos + 57, 15, 15,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryItemDetailsGUIButtonMessage(0, x, y, z));
						ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 15, 15,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryItemDetailsGUIButtonMessage(1, x, y, z));
						ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
		imagebutton_itemslot_icon = new ImageButton(this.leftPos + -119, this.topPos + -57, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon);
		imagebutton_itemslot_icon_copy = new ImageButton(this.leftPos + -93, this.topPos + -57, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy);
		imagebutton_itemslot_icon_copy_2 = new ImageButton(this.leftPos + -67, this.topPos + -57, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_2);
		imagebutton_itemslot_icon_copy_3 = new ImageButton(this.leftPos + -119, this.topPos + -31, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_3);
		imagebutton_itemslot_icon_copy_4 = new ImageButton(this.leftPos + -93, this.topPos + -31, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_4);
		imagebutton_itemslot_icon_copy_5 = new ImageButton(this.leftPos + -67, this.topPos + -31, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_5);
		imagebutton_itemslot_icon_copy_6 = new ImageButton(this.leftPos + -119, this.topPos + -4, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_6);
		imagebutton_itemslot_icon_copy_7 = new ImageButton(this.leftPos + -93, this.topPos + -4, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_7);
		imagebutton_itemslot_icon_copy_8 = new ImageButton(this.leftPos + -67, this.topPos + -4, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_8);
		imagebutton_itemslot_icon_copy_9 = new ImageButton(this.leftPos + 12, this.topPos + -73, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_9);
		imagebutton_itemslot_icon_copy_3_copy = new ImageButton(this.leftPos + 12, this.topPos + -47, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_3_copy);
		imagebutton_itemslot_icon_copy_6_copy = new ImageButton(this.leftPos + 12, this.topPos + -21, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_6_copy);
		imagebutton_itemslot_icon_copy_copy = new ImageButton(this.leftPos + 12, this.topPos + 4, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_copy);
		imagebutton_itemslot_icon_copy_4_copy = new ImageButton(this.leftPos + 12, this.topPos + 30, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png"), ResourceLocation.parse("omnichef:textures/screens/itemslot_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_itemslot_icon_copy_4_copy);
		red_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -101, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_red.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(16, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 16, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(red_bookmark);
		yellow_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -72, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_yellow.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(17, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 17, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(yellow_bookmark);
		green_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -43, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_green.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(18, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 18, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(green_bookmark);
		gray_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + -14, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_gray.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(19, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 19, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(gray_bookmark);
		blue_bookmark = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 140, this.topPos + 15, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blue.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(20, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 20, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(blue_bookmark);
		red_bookmark_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -189, this.topPos + -101, 35, 26, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_purple.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_purple.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(21, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 21, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(red_bookmark_copy);
		enhanced_image_button_bookmark_magenta = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 12, this.topPos + 86, 21, 21, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_magenta.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_magenta.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_bookmark_magenta);
		enhanced_image_button_bookmark_magenta_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 35, this.topPos + 86, 21, 21, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_orange.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_orange.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_bookmark_magenta_copy);
		enhanced_image_button_bookmark_magenta_copy_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 58, this.topPos + 86, 21, 21, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blood.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_blood.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_bookmark_magenta_copy_copy);
		enhanced_image_button_bookmark_magenta_copy_copy_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 81, this.topPos + 86, 21, 21, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_white.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/bookmark_white.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				// Rendered manually in the ordered GUI layer block.
			}
		};
		this.addRenderableWidget(enhanced_image_button_bookmark_magenta_copy_copy_copy);
		button_small_arrow_right = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -37, this.topPos + -62, 12, 12, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_right.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_right.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(22, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 22, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
			}
		};
		this.addRenderableWidget(button_small_arrow_right);
		button_small_arrow_left = new net.minecraft.client.gui.components.ImageButton(this.leftPos + -142, this.topPos + -62, 12, 12, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_left.png"), net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/small_arrow_left.png")), e -> {
					int x = ChefsDiaryItemDetailsGUIScreen.this.x;
					int y = ChefsDiaryItemDetailsGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage(23, x, y, z));
						net.mcreator.omnichef.network.ChefsDiaryItemDetailsGUIButtonMessage.handleButtonAction(entity, 23, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
			}
		};
		this.addRenderableWidget(button_small_arrow_left);
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
	private net.minecraft.client.gui.components.ImageButton red_bookmark;
	private net.minecraft.client.gui.components.ImageButton yellow_bookmark;
	private net.minecraft.client.gui.components.ImageButton green_bookmark;
	private net.minecraft.client.gui.components.ImageButton gray_bookmark;
	private net.minecraft.client.gui.components.ImageButton blue_bookmark;
	private net.minecraft.client.gui.components.ImageButton red_bookmark_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_bookmark_magenta;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_bookmark_magenta_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_bookmark_magenta_copy_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_bookmark_magenta_copy_copy_copy;

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

	private static final boolean guiTools$advancedButtons = true;
	private net.minecraft.client.gui.components.ImageButton button_small_arrow_right;
	private net.minecraft.client.gui.components.ImageButton button_small_arrow_left;

	private static net.minecraft.resources.ResourceLocation guiTools$advancedTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
		if (value == null || value.isBlank())
			return fallback;
		try {
			String texture = value.trim().replace('\\', '/');
			if (texture.indexOf(':') >= 0)
				return net.minecraft.resources.ResourceLocation.parse(texture);
			while (texture.startsWith("/"))
				texture = texture.substring(1);
			if (texture.startsWith("textures/screens/"))
				texture = texture.substring(17);
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