package net.mcreator.omnichef.client;

import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.ArrayList;

@EventBusSubscriber(Dist.CLIENT)
public class InventoryRenderer {
	private static Minecraft mc = Minecraft.getInstance();
	private static EffectRenderingInventoryScreen screen = null;
	private static final List<AbstractWidget> buttons = new ArrayList<>();

	@SubscribeEvent
	public static void openScreen(ScreenEvent.Init.Pre event) {
		if (event.getScreen() instanceof EffectRenderingInventoryScreen inventory) {
			if (mc == null)
				mc = Minecraft.getInstance();
			screen = inventory;
			initButtons(event);
		}
	}

	@SubscribeEvent
	public static void closeScreen(ScreenEvent.Closing event) {
		if (mc == null)
			mc = Minecraft.getInstance();
		if (mc != null) {
			if (mc.gameMode == null) {
				buttons.clear();
				return;
			}
		}
		if (event.getScreen() instanceof InventoryScreen && !mc.gameMode.hasInfiniteItems())
			buttons.clear();
		if (event.getScreen() instanceof CreativeModeInventoryScreen)
			buttons.clear();
	}

	@SubscribeEvent
	public static void renderBackground(ContainerScreenEvent.Render.Background event) {
		if (!(event.getContainerScreen() instanceof EffectRenderingInventoryScreen))
			return;
		if (mc == null)
			mc = Minecraft.getInstance();
		if (screen == null)
			screen = (EffectRenderingInventoryScreen) event.getContainerScreen();
		boolean flag = getFlag();
		if (!buttons.isEmpty()) {
			buttons.get(0).visible = flag;
		}

	}

	@SubscribeEvent
	public static void renderForeground(ContainerScreenEvent.Render.Foreground event) {
		if (mc == null)
			mc = Minecraft.getInstance();
		if (!(event.getContainerScreen() instanceof EffectRenderingInventoryScreen))
			return;
		if (screen == null)
			screen = (EffectRenderingInventoryScreen) event.getContainerScreen();

	}

	private static void initButtons(ScreenEvent.Init.Pre event) {
		buttons.add(new ImageButton(screen.getGuiLeft() + 308, screen.getGuiTop() + 37, 26, 26,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/result_icon.png"), ResourceLocation.parse("omnichef:textures/screens/result_icon.png")), e -> {
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				this.setX(screen.getGuiLeft() + 183);
				this.setY(screen.getGuiTop() + 0);
				if (getFlag())
					guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		});
		if (screen instanceof InventoryScreen)
			event.addListener(buttons.get(0));
	}

	private static boolean getFlag() {
		if (screen instanceof InventoryScreen inventory)
			return true;
		if (screen instanceof CreativeModeInventoryScreen inventory)
			return inventory.isInventoryOpen();
		return false;
	}
}