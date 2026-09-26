package net.mcreator.omnichef.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.omnichef.world.inventory.ChefsDiaryStatsGUIMenu;
import net.mcreator.omnichef.procedures.ReturnRestaurantStarsProcedure;
import net.mcreator.omnichef.procedures.ReturnRestaurantReputationProcedure;
import net.mcreator.omnichef.procedures.ReturnRestaurantLevelProcedure;
import net.mcreator.omnichef.procedures.ReturnRestaurantCloseTimeProcedure;
import net.mcreator.omnichef.network.ChefsDiaryStatsGUIButtonMessage;
import net.mcreator.omnichef.init.OmnichefModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryStatsGUIScreen extends AbstractContainerScreen<ChefsDiaryStatsGUIMenu> implements OmnichefModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("omnichef:textures/screens/chefs_diary_stats_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("omnichef:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("omnichef:textures/screens/separator_icon.png");
	private static final ResourceLocation SPRITE_0 = ResourceLocation.parse("omnichef:textures/screens/stars_sprite_icon.png");

	public ChefsDiaryStatsGUIScreen(ChefsDiaryStatsGUIMenu container, Inventory inventory, Component text) {
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
			String guiTools$sizedLabelText0 = java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnRestaurantNameProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + -145 && mouseX < this.leftPos + -15 && mouseY >= this.topPos + -74 && mouseY < this.topPos + -64 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText0, 130, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText0), mouseX, mouseY);
			String guiTools$sizedLabelText1 = java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnRestaurantOwnerProcedure.execute(entity), "");
			if (true && mouseX >= this.leftPos + -145 && mouseX < this.leftPos + -15 && mouseY >= this.topPos + -61 && mouseY < this.topPos + -51 && this.guiTools$isSizedTextTruncated(guiTools$sizedLabelText1, 130, 1.00F))
				guiGraphics.renderTooltip(font, net.minecraft.network.chat.Component.literal(guiTools$sizedLabelText1), mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
		guiGraphics.blit(IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
		guiGraphics.blit(IMAGE_2, this.leftPos + -145, this.topPos + -89, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(SPRITE_0, this.leftPos + 28, this.topPos + -75, 0, Mth.clamp((int) ReturnRestaurantStarsProcedure.execute(entity) * 25, 0, 250), 72, 25, 72, 275);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_stats_gui.label_stats_wip"), -145, -98, -12829636, false);
		guiGraphics.drawString(this.font, ReturnRestaurantLevelProcedure.execute(entity), -145, -48, -12829636, false);
		guiGraphics.drawString(this.font, ReturnRestaurantReputationProcedure.execute(entity), -145, -35, -12829636, false);
		guiGraphics.drawString(this.font, ReturnRestaurantCloseTimeProcedure.execute(entity), -145, -22, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.omnichef.chefs_diary_stats_gui.label_restaurant_stars"), 5, -98, -12829636, false);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnRestaurantNameProcedure.execute(entity), ""), -145, -74, 130, -12829636, false, 1.00F, 2);
		if (true)
			this.guiTools$renderSizedTextLabel(guiGraphics, java.util.Objects.toString(net.mcreator.omnichef.procedures.ReturnRestaurantOwnerProcedure.execute(entity), ""), -145, -61, 130, -12829636, false, 1.00F, 2);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18, new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/base_icon.png"), ResourceLocation.parse("omnichef:textures/screens/base_icon.png")),
				e -> {
					int x = ChefsDiaryStatsGUIScreen.this.x;
					int y = ChefsDiaryStatsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryStatsGUIButtonMessage(0, x, y, z));
						ChefsDiaryStatsGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
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
					int x = ChefsDiaryStatsGUIScreen.this.x;
					int y = ChefsDiaryStatsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryStatsGUIButtonMessage(1, x, y, z));
						ChefsDiaryStatsGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
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
					int x = ChefsDiaryStatsGUIScreen.this.x;
					int y = ChefsDiaryStatsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryStatsGUIButtonMessage(2, x, y, z));
						ChefsDiaryStatsGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
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
					int x = ChefsDiaryStatsGUIScreen.this.x;
					int y = ChefsDiaryStatsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryStatsGUIButtonMessage(3, x, y, z));
						ChefsDiaryStatsGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
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
					int x = ChefsDiaryStatsGUIScreen.this.x;
					int y = ChefsDiaryStatsGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryStatsGUIButtonMessage(4, x, y, z));
						ChefsDiaryStatsGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
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
}