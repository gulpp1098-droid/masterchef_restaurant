package net.mcreator.masterchefrestaurant.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.util.function.Supplier;
import java.util.ArrayList;

@EventBusSubscriber
public class MasterchefRestaurantModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MasterchefRestaurantMod.MODID);
	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new PlayerVariables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		MasterchefRestaurantMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
		MasterchefRestaurantMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@SubscribeEvent
	public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			for (Entity entityiterator : player.level().players())
				if (entityiterator != player && entityiterator instanceof ServerPlayer playeriterator)
					PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(playeriterator.getData(PLAYER_VARIABLES), playeriterator.getId()));
			PacketDistributor.sendToPlayersInDimension((ServerLevel) player.level(), new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES), player.getId()));
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			for (Entity entityiterator : player.level().players())
				if (entityiterator != player && entityiterator instanceof ServerPlayer playeriterator)
					PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(playeriterator.getData(PLAYER_VARIABLES), playeriterator.getId()));
			PacketDistributor.sendToPlayersInDimension((ServerLevel) player.level(), new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES), player.getId()));
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			for (Entity entityiterator : player.level().players())
				if (entityiterator != player && entityiterator instanceof ServerPlayer playeriterator)
					PacketDistributor.sendToPlayer(player, new PlayerVariablesSyncMessage(playeriterator.getData(PLAYER_VARIABLES), playeriterator.getId()));
			PacketDistributor.sendToPlayersInDimension((ServerLevel) player.level(), new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES), player.getId()));
		}
	}

	@SubscribeEvent
	public static void onPlayerTickUpdateSyncPlayerVariables(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player && player.getData(PLAYER_VARIABLES)._syncDirty) {
			PacketDistributor.sendToPlayersInDimension((ServerLevel) player.level(), new PlayerVariablesSyncMessage(player.getData(PLAYER_VARIABLES), player.getId()));
			player.getData(PLAYER_VARIABLES)._syncDirty = false;
		}
	}

	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event) {
		PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
		PlayerVariables clone = new PlayerVariables();
		clone.PreviewUUID = original.PreviewUUID;
		clone.Restaurant_ID = original.Restaurant_ID;
		clone.CurrentClientUUID = original.CurrentClientUUID;
		clone.currentNBT = original.currentNBT;
		clone.TableNumber = original.TableNumber;
		clone.OrderSlot0Item = original.OrderSlot0Item;
		clone.Debug = original.Debug;
		clone.owner = original.owner;
		clone.ClientPatiance = original.ClientPatiance;
		clone.GUIstring = original.GUIstring;
		clone.CurrentClientFoodDelivered = original.CurrentClientFoodDelivered;
		if (!event.isWasDeath()) {
		}
		event.getEntity().setData(PLAYER_VARIABLES, clone);
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			SavedData mapdata = MapVariables.get(event.getEntity().level());
			SavedData worlddata = WorldVariables.get(event.getEntity().level());
			if (mapdata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata));
			if (worlddata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			SavedData worlddata = WorldVariables.get(event.getEntity().level());
			if (worlddata != null)
				PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
		}
	}

	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel level) {
			WorldVariables worldVariables = WorldVariables.get(level);
			if (worldVariables._syncDirty) {
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, worldVariables));
				worldVariables._syncDirty = false;
			}
			MapVariables mapVariables = MapVariables.get(level);
			if (mapVariables._syncDirty) {
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, mapVariables));
				mapVariables._syncDirty = false;
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "masterchef_restaurant_worldvars";
		boolean _syncDirty = false;

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			WorldVariables data = new WorldVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			return nbt;
		}

		public void markSyncDirty() {
			this.setDirty();
			this._syncDirty = true;
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "masterchef_restaurant_mapvars";
		boolean _syncDirty = false;
		public String Restaurant_Info_Path = "\"\"";
		public String Restaurant_File_Name = "ListOfRestaurants.json";
		public double RestaurantID = 1.0;
		public double MaxRestaurantLevel = 100.0;
		public String FoodDatabase_File_Name = "FoodDatabase.json";
		public double LastProcessedDay = -1.0;
		public double ClosingRestaurantDay = -1.0;
		public double Time_control_tick = 0;
		public ArrayList<Object> RestaurantsOpen = new ArrayList<>();
		public String ClientsDatabase_File_Name = "ClientsDatabase.json";
		public double LastClientsDatabaseResetDay = 0;

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			MapVariables data = new MapVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			Restaurant_Info_Path = nbt.getString("Restaurant_Info_Path");
			Restaurant_File_Name = nbt.getString("Restaurant_File_Name");
			RestaurantID = nbt.getDouble("RestaurantID");
			MaxRestaurantLevel = nbt.getDouble("MaxRestaurantLevel");
			FoodDatabase_File_Name = nbt.getString("FoodDatabase_File_Name");
			LastProcessedDay = nbt.getDouble("LastProcessedDay");
			ClosingRestaurantDay = nbt.getDouble("ClosingRestaurantDay");
			Time_control_tick = nbt.getDouble("Time_control_tick");
			RestaurantsOpen = NbtArrayLists.loadGlobalMap(nbt.getList("RestaurantsOpen", Tag.TAG_COMPOUND), lookupProvider);
			ClientsDatabase_File_Name = nbt.getString("ClientsDatabase_File_Name");
			LastClientsDatabaseResetDay = nbt.getDouble("LastClientsDatabaseResetDay");
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			nbt.putString("Restaurant_Info_Path", Restaurant_Info_Path);
			nbt.putString("Restaurant_File_Name", Restaurant_File_Name);
			nbt.putDouble("RestaurantID", RestaurantID);
			nbt.putDouble("MaxRestaurantLevel", MaxRestaurantLevel);
			nbt.putString("FoodDatabase_File_Name", FoodDatabase_File_Name);
			nbt.putDouble("LastProcessedDay", LastProcessedDay);
			nbt.putDouble("ClosingRestaurantDay", ClosingRestaurantDay);
			nbt.putDouble("Time_control_tick", Time_control_tick);
			nbt.put("RestaurantsOpen", NbtArrayLists.saveGlobalMap(RestaurantsOpen, lookupProvider));
			nbt.putString("ClientsDatabase_File_Name", ClientsDatabase_File_Name);
			nbt.putDouble("LastClientsDatabaseResetDay", LastClientsDatabaseResetDay);
			return nbt;
		}

		public void markSyncDirty() {
			this.setDirty();
			_syncDirty = true;
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					if (message.dataType == 0)
						MapVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
					else
						WorldVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}

	public static class PlayerVariables implements INBTSerializable<CompoundTag> {
		boolean _syncDirty = false;
		public String PreviewUUID = "\"\"";
		public double Restaurant_ID = -1.0;
		public String CurrentClientUUID = "\"\"";
		public String currentNBT = "\"\"";
		public double TableNumber = 0;
		public ItemStack OrderSlot0Item = ItemStack.EMPTY;
		public String Debug = "\"\"";
		public String owner = "\"\"";
		public double ClientPatiance = -1.0;
		public String GUIstring = "";
		public String CurrentClientFoodDelivered = "\"\"";

		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("PreviewUUID", PreviewUUID);
			nbt.putDouble("Restaurant_ID", Restaurant_ID);
			nbt.putString("CurrentClientUUID", CurrentClientUUID);
			nbt.putString("currentNBT", currentNBT);
			nbt.putDouble("TableNumber", TableNumber);
			nbt.put("OrderSlot0Item", OrderSlot0Item.saveOptional(lookupProvider));
			nbt.putString("Debug", Debug);
			nbt.putString("owner", owner);
			nbt.putDouble("ClientPatiance", ClientPatiance);
			nbt.putString("GUIstring", GUIstring);
			nbt.putString("CurrentClientFoodDelivered", CurrentClientFoodDelivered);
			return nbt;
		}

		@Override
		public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
			PreviewUUID = nbt.getString("PreviewUUID");
			Restaurant_ID = nbt.getDouble("Restaurant_ID");
			CurrentClientUUID = nbt.getString("CurrentClientUUID");
			currentNBT = nbt.getString("currentNBT");
			TableNumber = nbt.getDouble("TableNumber");
			OrderSlot0Item = ItemStack.parseOptional(lookupProvider, nbt.getCompound("OrderSlot0Item"));
			Debug = nbt.getString("Debug");
			owner = nbt.getString("owner");
			ClientPatiance = nbt.getDouble("ClientPatiance");
			GUIstring = nbt.getString("GUIstring");
			CurrentClientFoodDelivered = nbt.getString("CurrentClientFoodDelivered");
		}

		public void markSyncDirty() {
			_syncDirty = true;
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data, int player) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> {
			buffer.writeInt(message.player());
			buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables(), buffer.readInt());
			message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
			return message;
		});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					Entity player = context.player().level().getEntity(message.player);
					if (player == null)
						return;
					player.getData(PLAYER_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()));
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}