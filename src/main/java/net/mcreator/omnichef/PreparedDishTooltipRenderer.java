package net.mcreator.omnichef;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import net.mcreator.omnichef.init.OmnichefModItems;
import net.mcreator.omnichef.procedures.GetPreparedDishAmountArrayProcedure;
import net.mcreator.omnichef.procedures.GetPreparedDishFoodArrayProcedure;

@EventBusSubscriber(modid = "omnichef", value = Dist.CLIENT)
public final class PreparedDishTooltipRenderer {
    @SubscribeEvent
    public static void registerComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(IngredientLine.class, line -> line);
    }

    @SubscribeEvent
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack dish = event.getItemStack();
        if (!dish.is(OmnichefModItems.PREPARED_DISH.get())) {
            return;
        }

        JsonArray foods;
        JsonArray amounts;
        try {
            foods = GetPreparedDishFoodArrayProcedure.execute(dish);
            amounts = GetPreparedDishAmountArrayProcedure.execute(dish);
        } catch (RuntimeException invalidData) {
            // Leave the base tooltip visible when dish data cannot be read.
            return;
        }

        var tooltip = event.getTooltipElements();
        int headerIndex = tooltip.size();
        int ingredientCount = Math.min(foods.size(), amounts.size());

        for (int index = 0; index < ingredientCount; index++) {
            JsonElement food = foods.get(index);
            JsonElement amount = amounts.get(index);
            if (!(food instanceof JsonPrimitive foodValue) || !foodValue.isString()
                    || !(amount instanceof JsonPrimitive amountValue) || !amountValue.isNumber()) {
                continue;
            }

            double quantity = amountValue.getAsDouble();
            if (!Double.isFinite(quantity) || quantity <= 0) {
                continue;
            }

            String foodId = foodValue.getAsString();
            ResourceLocation id = ResourceLocation.tryParse(foodId.toLowerCase(Locale.ROOT));
            ItemStack ingredient = id == null ? ItemStack.EMPTY
                    : BuiltInRegistries.ITEM.getOptional(id).map(ItemStack::new).orElse(ItemStack.EMPTY);
            String name = ingredient.isEmpty() ? foodId : ingredient.getHoverName().getString();
            tooltip.add(Either.right(new IngredientLine(ingredient, name + " x" + Math.round(quantity))));
        }

        if (tooltip.size() > headerIndex) {
            tooltip.add(headerIndex, Either.left(Component.literal("Contents:")));
        }
    }

    private record IngredientLine(ItemStack ingredient, String label)
            implements TooltipComponent, ClientTooltipComponent {
        private static final int TEXT_OFFSET = 19;
        private static final int HEIGHT = 18;

        @Override
        public int getHeight() {
            return HEIGHT;
        }

        @Override
        public int getWidth(Font font) {
            return TEXT_OFFSET + font.width(label);
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
            if (!ingredient.isEmpty()) {
                graphics.renderItem(ingredient, x, y);
            }
            int textY = y + (HEIGHT - font.lineHeight) / 2;
            graphics.drawString(font, label, x + TEXT_OFFSET, textY, 0xFFFFFF, true);
        }
    }
}