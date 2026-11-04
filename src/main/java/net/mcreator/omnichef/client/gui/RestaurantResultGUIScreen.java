package net.mcreator.omnichef.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.RestaurantResultGUIMenu;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class RestaurantResultGUIScreen extends AbstractContainerScreen<RestaurantResultGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/restaurant_result_gui.png");

	public RestaurantResultGUIScreen(RestaurantResultGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 166;
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
			String guiTools$sizedLabelText0 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_day_18_results", "DAY 18 RESULTS");
			if (true && mouseX >= this.leftPos + -110 && mouseX < this.leftPos + 10 && mouseY >= this.topPos + -8 && mouseY < this.topPos + 2 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText0, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText0), mouseX, mouseY);
			String guiTools$sizedLabelText1 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_customers_20", "CUSTOMERS: 20");
			if (true && mouseX >= this.leftPos + -125 && mouseX < this.leftPos + -5 && mouseY >= this.topPos + 12 && mouseY < this.topPos + 25 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText1, 120, 1.25F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText1), mouseX, mouseY);
			String guiTools$sizedLabelText2 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfaction", "SATISFACTION");
			if (true && mouseX >= this.leftPos + -149 && mouseX < this.leftPos + -29 && mouseY >= this.topPos + 103 && mouseY < this.topPos + 113 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText2, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText2), mouseX, mouseY);
			String guiTools$sizedLabelText3 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_coins_earned", "COINS EARNED");
			if (true && mouseX >= this.leftPos + -126 && mouseX < this.leftPos + -6 && mouseY >= this.topPos + 123 && mouseY < this.topPos + 133 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText3, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText3), mouseX, mouseY);
			String guiTools$sizedLabelText4 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_coins_earned_copy", "REPUTATION EARNED");
			if (true && mouseX >= this.leftPos + -126 && mouseX < this.leftPos + -6 && mouseY >= this.topPos + 143 && mouseY < this.topPos + 153 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText4, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText4), mouseX, mouseY);
			String guiTools$sizedLabelText5 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_perfect", "PERFECT");
			if (true && mouseX >= this.leftPos + -126 && mouseX < this.leftPos + -6 && mouseY >= this.topPos + 30 && mouseY < this.topPos + 40 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText5, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText5), mouseX, mouseY);
			String guiTools$sizedLabelText6 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfied", "SATISFIED");
			if (true && mouseX >= this.leftPos + -126 && mouseX < this.leftPos + -6 && mouseY >= this.topPos + 50 && mouseY < this.topPos + 60 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText6, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText6), mouseX, mouseY);
			String guiTools$sizedLabelText7 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_lost", "LOST");
			if (true && mouseX >= this.leftPos + -126 && mouseX < this.leftPos + -6 && mouseY >= this.topPos + 70 && mouseY < this.topPos + 80 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText7, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText7), mouseX, mouseY);
			String guiTools$sizedLabelText8 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_day_18_results_copy", "SERVICE NOTES");
			if (true && mouseX >= this.leftPos + 53 && mouseX < this.leftPos + 173 && mouseY >= this.topPos + -8 && mouseY < this.topPos + 2 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText8, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText8), mouseX, mouseY);
			String guiTools$sizedLabelText9 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_customers_20_copy", "Orders with mistakes");
			if (true && mouseX >= this.leftPos + 37 && mouseX < this.leftPos + 165 && mouseY >= this.topPos + 14 && mouseY < this.topPos + 24 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText9, 128, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText9), mouseX, mouseY);
			String guiTools$sizedLabelText10 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_perfect_copy", "Missing food");
			if (true && mouseX >= this.leftPos + 31 && mouseX < this.leftPos + 151 && mouseY >= this.topPos + 30 && mouseY < this.topPos + 40 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText10, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText10), mouseX, mouseY);
			String guiTools$sizedLabelText11 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfied_copy", "Extra food");
			if (true && mouseX >= this.leftPos + 31 && mouseX < this.leftPos + 151 && mouseY >= this.topPos + 50 && mouseY < this.topPos + 60 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText11, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText11), mouseX, mouseY);
			String guiTools$sizedLabelText12 = menu.getMenuState(0, "gui_tools:sized:sized_text_label_lost_copy", "Wrong food");
			if (true && mouseX >= this.leftPos + 31 && mouseX < this.leftPos + 151 && mouseY >= this.topPos + 70 && mouseY < this.topPos + 80 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText12, 120, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText12), mouseX, mouseY);
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
				int guiTools$visibleWidth = 183;
				int guiTools$visibleHeight = 198;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/result_gui.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -164 + guiTools$xOffset, this.topPos + -23 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 183, 198);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 93;
				int guiTools$visibleHeight = 21;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/ribbon_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -119 + guiTools$xOffset, this.topPos + -12 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 93, 21);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 16;
				int guiTools$visibleHeight = 16;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/facehappy_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -149 + guiTools$xOffset, this.topPos + 27 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 16, 16);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 16;
				int guiTools$visibleHeight = 16;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/facemedium_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -149 + guiTools$xOffset, this.topPos + 47 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 16, 16);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 16;
				int guiTools$visibleHeight = 16;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/faceangry_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -149 + guiTools$xOffset, this.topPos + 67 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 16, 16);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 133;
				int guiTools$visibleHeight = 11;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/separator_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -139 + guiTools$xOffset, this.topPos + 85 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 133, 11);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 16;
				int guiTools$visibleHeight = 16;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/coin_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -150 + guiTools$xOffset, this.topPos + 120 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 16, 16);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 16;
				int guiTools$visibleHeight = 16;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/reputation_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + -150 + guiTools$xOffset, this.topPos + 140 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 16, 16);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 138;
				int guiTools$visibleHeight = 197;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/result_gui.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 19 + guiTools$xOffset, this.topPos + -23 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 138, 197);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 93;
				int guiTools$visibleHeight = 21;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/ribbon_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 44 + guiTools$xOffset, this.topPos + -12 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 93, 21);
			}
			if (true) {
				int guiTools$xOffset = 0;
				int guiTools$yOffset = 0;
				int guiTools$visibleWidth = 120;
				int guiTools$visibleHeight = 11;
				net.minecraft.resources.ResourceLocation guiTools$image = guiTools$dynamicTexture("", net.minecraft.resources.ResourceLocation.parse("omnichef:textures/screens/separator_icon.png"));
				if (guiTools$image != null && guiTools$visibleWidth > 0 && guiTools$visibleHeight > 0)
					guiTools$alphaBlit(guiGraphics, guiTools$image, this.leftPos + 28 + guiTools$xOffset, this.topPos + 107 + guiTools$yOffset, 0, 0, guiTools$visibleWidth, guiTools$visibleHeight, 120, 11);
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
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_day_18_results", "DAY 18 RESULTS"), -110, -8, 120, -1, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_customers_20", "CUSTOMERS: 20"), -125, 12, 120, -12829636, false, 1.25F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfaction", "SATISFACTION"), -149, 103, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_coins_earned", "COINS EARNED"), -126, 123, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_coins_earned_copy", "REPUTATION EARNED"), -126, 143, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_perfect", "PERFECT"), -126, 30, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfied", "SATISFIED"), -126, 50, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_lost", "LOST"), -126, 70, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_day_18_results_copy", "SERVICE NOTES"), 53, -8, 120, -1, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_customers_20_copy", "Orders with mistakes"), 37, 14, 128, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_perfect_copy", "Missing food"), 31, 30, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_satisfied_copy", "Extra food"), 31, 50, 120, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, menu.getMenuState(0, "gui_tools:sized:sized_text_label_lost_copy", "Wrong food"), 31, 70, 120, -12829636, false, 1.00F, 2);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_1062", "10\n\n6\n\n2"), -126, 29, 120, 40, -12829636, false, 1.00F, 0, 1);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_1002010", "100%\n\n+20\n\n+10"), -126, 102, 120, 40, -12829636, false, 1.00F, 0, 1);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_1062_copy", "3\n\n1\n\n2"), 25, 29, 120, 40, -12829636, false, 1.00F, 0, 1);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_one_order_may_contain_several_mi", "One order may contain several mistakes."), 32, 85, 114, 40, -12829636, false, 1.00F, 0, 2);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_one_order_may_contain_several_mi_copy", "Check food types and amounts before serving."), 32, 125, 114, 40, -12829636, false, 1.00F, 0,
				2);
	}

	@Override
	public void init() {
		super.init();
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

	private final java.util.Map<String, java.util.List<java.util.List<String>>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode, int alignment) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int lineStep = this.font.lineHeight + 1;
		String cacheKey = text + "\u0000" + wrapWidth;
		java.util.List<java.util.List<String>> paragraphs = this.guiTools$multilineCache.computeIfAbsent(cacheKey,
				key -> java.util.Arrays.stream(text.replace("\r", "").split("\n", -1)).map(paragraph -> this.guiTools$wrapMultilineText(paragraph, wrapWidth)).toList());
		if (this.guiTools$multilineCache.size() > 64)
			this.guiTools$multilineCache.clear();
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(this.leftPos + x, this.topPos + y, this.leftPos + x + boxWidth, this.topPos + y + boxHeight);
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			int currentY = 0;
			for (java.util.List<String> lines : paragraphs) {
				for (int index = 0; index < lines.size(); index++) {
					String line = lines.get(index);
					int remaining = wrapWidth - this.font.width(line);
					if (alignment == 3 && index < lines.size() - 1 && remaining > 0 && line.contains(" ")) {
						String[] words = line.split(" ");
						int advance = 0;
						for (int word = 0; word < words.length; word++) {
							int currentX = advance + (int) Math.round((double) remaining * word / (words.length - 1));
							guiGraphics.drawString(this.font, words[word], currentX, currentY, color, shadow);
							advance += this.font.width(words[word] + " ");
						}
					} else {
						int currentX = alignment == 1 ? remaining : alignment == 2 ? remaining / 2 : 0;
						guiGraphics.drawString(this.font, line, currentX, currentY, color, shadow);
					}
					currentY += lineStep;
				}
			}
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private java.util.List<String> guiTools$wrapMultilineText(String text, int wrapWidth) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		for (String paragraph : text.replace("\r", "").split("\n", -1)) {
			if (paragraph.isEmpty()) {
				lines.add("");
				continue;
			}
			StringBuilder line = new StringBuilder();
			for (String word : paragraph.split("\s+")) {
				String candidate = line.isEmpty() ? word : line + " " + word;
				if (!line.isEmpty() && this.font.width(candidate) > wrapWidth) {
					lines.add(line.toString());
					line.setLength(0);
					line.append(word);
				} else {
					line.setLength(0);
					line.append(candidate);
				}
			}
			lines.add(line.toString());
		}
		return java.util.List.copyOf(lines);
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