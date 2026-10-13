package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.entity.CriticEntity;

public class CriticAnimationSetUpProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Direction direction = Direction.NORTH;
		client = entity;
		if (client.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Walk, true);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Sit, false);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Idle, false);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Eat, false);
		} else if (client.isPassenger()) {
			if ((client.getPersistentData().getString("state")).equals("food_eat")) {
				direction = getDirectionFromBlockState(
						(world.getBlockState(BlockPos.containing(Math.floor(client.getPersistentData().getDouble("DestX")), client.getPersistentData().getDouble("DestY"), Math.floor(client.getPersistentData().getDouble("DestZ"))))));
				if (direction == Direction.SOUTH) {
					{
						Entity _ent = client;
						_ent.setYRot(0);
						_ent.setXRot(0);
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (direction == Direction.WEST) {
					{
						Entity _ent = client;
						_ent.setYRot(90);
						_ent.setXRot(0);
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else if (direction == Direction.NORTH) {
					{
						Entity _ent = client;
						_ent.setYRot(180);
						_ent.setXRot(0);
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				} else {
					{
						Entity _ent = client;
						_ent.setYRot(-90);
						_ent.setXRot(0);
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				}
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Walk, false);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Sit, false);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Idle, false);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Eat, true);
			} else {
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Walk, false);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Sit, true);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Idle, false);
				if (client instanceof CriticEntity _datEntSetL)
					_datEntSetL.getEntityData().set(CriticEntity.DATA_Eat, false);
			}
		} else {
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Walk, false);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Sit, false);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Idle, true);
			if (client instanceof CriticEntity _datEntSetL)
				_datEntSetL.getEntityData().set(CriticEntity.DATA_Eat, false);
		}
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}