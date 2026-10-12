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

import net.mcreator.omnichef.world.inventory.LocationGuideP3GUIMenu;
import net.mcreator.omnichef.network.LocationGuideP3GUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class LocationGuideP3GUIScreen extends AbstractContainerScreen<LocationGuideP3GUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private ImageButton imagebutton_last_page_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/location_guide_p_3_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/location_icon.png");

	public LocationGuideP3GUIScreen(LocationGuideP3GUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(IMAGE_2, this.leftPos + -30, this.topPos + -102, 0, 0, 15, 17, 15, 17);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.location_guide_p_3_gui.label_overview_wip"), -145, -98, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics,
				menu.getMenuState(0, "gui_tools:multiline:multiline_label_before_placing_down_your_blocks",
						"If I ever decide that my first location was a terrible idea, the Golden Spatula can help me move my restaurant\u2019s official grounds.\nI can only do this while the restaurant is closed, with no customers still inside."),
				-145, -84, 130, 167, -12829636, false, 1.00F, 0, 0);
		this.guiTools$renderMultilineLabel(guiGraphics, menu.getMenuState(0, "gui_tools:multiline:multiline_label_after_that_you_can_claim_3_more",
				"After I select a new base area with the Golden Spatula, my previous claimed areas are replaced.\nThe old building and furniture will stay where they are, but I will need to place a new Reception and claim the rest of my new space. Buildings do not move just because I changed my mind."),
				5, -97, 124, 149, -12829636, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/base_icon.png"), ResourceLocation.parse("omnichef:textures/screens/base_icon.png")),
				e -> {
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(0, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
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
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(1, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(2, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(3, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(4, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		imagebutton_last_page_icon = new ImageButton(this.leftPos + -141, this.topPos + 57, 16, 16,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png"), ResourceLocation.parse("omnichef:textures/screens/last_page_icon.png")), e -> {
					int x = LocationGuideP3GUIScreen.this.x;
					int y = LocationGuideP3GUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new LocationGuideP3GUIButtonMessage(5, x, y, z));
						LocationGuideP3GUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_last_page_icon);
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
}