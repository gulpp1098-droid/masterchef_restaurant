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

import net.mcreator.omnichef.world.inventory.ExpLevelGuideGUIMenu;
import net.mcreator.omnichef.network.ExpLevelGuideGUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ExpLevelGuideGUIScreen extends AbstractContainerScreen<ExpLevelGuideGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private ImageButton imagebutton_next_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/exp_level_guide_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/coin_icon.png");

	public ExpLevelGuideGUIScreen(ExpLevelGuideGUIMenu container, Inventory inventory, Component text) {
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
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
		guiGraphics.blit(IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
		guiGraphics.blit(IMAGE_2, this.leftPos + -34, this.topPos + -102, 0, 0, 15, 17, 15, 17);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.exp_level_guide_gui.label_overview_wip"), -145, -98, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.exp_level_guide_gui.label_as_well_as_claim_area"), -145, 6, -65485, false);
		this.guiTools$renderMultilineLabel(guiGraphics, "After customers finish eating, they will leave money and give reputation to your restaurant. Money is left on the Service Table. To collect it, simply right-click the table.", -145, -84, 132,
				84, -12829636, false, 1.00F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics, "A Service Table with uncollected payment will not accept new customers.", -145, 18, 131, 48, -12829636, false, 1.00F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics,
				"Serving customers earns reputation for your restaurant. To receive the full reward, complete the entire order. Every missing food reduces the reputation gained, and a badly incomplete order may even make you lose reputation.\nEarn enough reputation and your restaurant will level up!",
				5, -84, 124, 168, -12829636, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/base_icon.png"), ResourceLocation.parse("omnichef:textures/screens/base_icon.png")),
				e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(0, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_base_icon);
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/food_icon.png"), ResourceLocation.parse("omnichef:textures/screens/food_icon.png")),
				e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(1, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_food_icon);
		imagebutton_clients_icon = new ImageButton(this.leftPos + 149, this.topPos + -39, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/clients_icon.png"), ResourceLocation.parse("omnichef:textures/screens/clients_icon.png")), e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(2, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_clients_icon);
		imagebutton_appliences_icon = new ImageButton(this.leftPos + 148, this.topPos + -11, 18, 18,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png"), ResourceLocation.parse("omnichef:textures/screens/appliences_icon.png")), e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(3, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_appliences_icon);
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/stats_icon.png"), ResourceLocation.parse("omnichef:textures/screens/stats_icon.png")),
				e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(4, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_next_page_icon = new ImageButton(this.leftPos + 108, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/next_page_icon.png")), e -> {
					int x = ExpLevelGuideGUIScreen.this.x;
					int y = ExpLevelGuideGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ExpLevelGuideGUIButtonMessage(5, x, y, z));
						ExpLevelGuideGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_next_page_icon);
	}

	private final java.util.Map<String, java.util.List<java.util.List<String>>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode, int alignment) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		int lineStep = this.font.lineHeight + 1;
		int maxLines = contentHeight < this.font.lineHeight ? 0 : 1 + (contentHeight - this.font.lineHeight) / lineStep;
		String cacheKey = text + "\u0000" + wrapWidth + "\u0000" + maxLines + "\u0000" + overflowMode;
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

	private boolean guiTools$isMultilineTruncated(String text, int boxWidth, int boxHeight, float scale, int overflowMode) {
		if (text == null || overflowMode == 0 || scale <= 0.0F)
			return false;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		java.util.List<String> lines = this.guiTools$wrapMultilineText(text, wrapWidth);
		for (String line : lines)
			if (this.font.width(line) > wrapWidth)
				return true;
		return !lines.isEmpty() && this.font.lineHeight + (lines.size() - 1) * (this.font.lineHeight + 1) > contentHeight;
	}

	private java.util.List<String> guiTools$displayMultilineText(String text, int wrapWidth, int maxLines, int overflowMode) {
		java.util.List<String> wrapped = this.guiTools$wrapMultilineText(text, wrapWidth);
		if (overflowMode != 2)
			return wrapped;
		if (maxLines <= 0)
			return java.util.List.of();
		boolean verticalOverflow = wrapped.size() > maxLines;
		java.util.List<String> result = new java.util.ArrayList<>();
		for (int index = 0; index < Math.min(maxLines, wrapped.size()); index++) {
			result.add(this.guiTools$ellipsize(wrapped.get(index), wrapWidth, verticalOverflow && index == maxLines - 1));
		}
		return java.util.List.copyOf(result);
	}

	private String guiTools$ellipsize(String value, int maxWidth, boolean forceEllipsis) {
		if (!forceEllipsis && this.font.width(value) <= maxWidth)
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
}