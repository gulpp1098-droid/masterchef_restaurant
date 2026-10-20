package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class CalculateTradeBoardAmountsProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject databaseObject, String inputID, String outputID) {
		if (databaseObject == null || inputID == null || outputID == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject result = new com.google.gson.JsonObject();
		ItemStack inputItem = ItemStack.EMPTY;
		ItemStack outputItem = ItemStack.EMPTY;
		double inputScore = 0;
		double outputScore = 0;
		double inputMax = 0;
		double outputMax = 0;
		double maxOutputCount = 0;
		double inputCount = 0;
		double outputCount = 0;
		double costMultiplier = 0;
		double outputLimit = 0;
		costMultiplier = 2.5;
		outputLimit = 16;
		inputItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((inputID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
		outputItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((outputID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
		if (inputItem.getItem() == Blocks.AIR.asItem() || inputItem.getItem() == outputItem.getItem() || outputItem.getItem() == Blocks.AIR.asItem()) {
			return result;
		}
		if (!IsFoodInDatabaseProcedure.execute(databaseObject, outputID) || !IsFoodInDatabaseProcedure.execute(databaseObject, inputID)) {
			return result;
		}
		inputScore = GetFoodScoreByIDProcedure.execute(databaseObject, inputID);
		outputScore = GetFoodScoreByIDProcedure.execute(databaseObject, outputID);
		inputMax = inputItem.getMaxStackSize();
		outputMax = outputItem.getMaxStackSize();
		if (inputScore <= 0 || outputScore <= 0) {
			return result;
		}
		maxOutputCount = Math.floor(Math.min(outputLimit, Math.min(outputMax, (inputMax * inputScore) / (costMultiplier * outputScore))));
		if (maxOutputCount < 1) {
			return result;
		}
		outputCount = 1 + Math.floor(Math.random() * maxOutputCount);
		inputCount = Math.ceil((outputScore * outputCount * costMultiplier) / inputScore);
		if (inputCount < 1 || inputCount > inputMax) {
			return result;
		}
		result.addProperty("input_count", inputCount);
		result.addProperty("output_count", outputCount);
		return result;
	}
}