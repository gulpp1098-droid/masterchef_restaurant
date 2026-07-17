// Save this class in your mod and generate all required imports

/**
 * Made with Blockbench 5.1.4 Exported for Minecraft version 1.19 or later with
 * Mojang mappings
 * 
 * @author Author
 */
public class ClientNewAnimation {
	public static final AnimationDefinition Walk = AnimationDefinition.Builder.withLength(2.5F).looping()
			.addAnimation("Legs",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Legs",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Lefthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -4.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -20.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 20.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -4.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Head",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Head",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Righthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 4.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 20.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -20.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 4.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Rightleg",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -16.87F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.2917F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -16.87F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Rightleg",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(5.0F, 1.5F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.posVec(5.0F, 3.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.2917F, KeyframeAnimations.posVec(-3.0F, 4.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.posVec(-3.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.25F, KeyframeAnimations.posVec(5.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.posVec(5.0F, 1.5F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Leftleg",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -16.87F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 15.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Leftleg",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(-3.0F, 4.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.5F, KeyframeAnimations.posVec(-3.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.0F, KeyframeAnimations.posVec(5.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.25F, KeyframeAnimations.posVec(5.0F, 1.5F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.posVec(5.0F, 3.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.posVec(-3.0F, 4.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("UpperBody",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.build();

	public static final AnimationDefinition Idle = AnimationDefinition.Builder.withLength(3.0F).looping()
			.addAnimation("Lefthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Head",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Righthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR)))
			.build();

	public static final AnimationDefinition Sit = AnimationDefinition.Builder.withLength(3.0F).looping()
			.addAnimation("Body",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -4.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Legs",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 90.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Legs",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(-8.0F, 10.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Lefthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Head",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Righthand",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 7.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Rightleg",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(12.5F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(12.5F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Leftleg",
					new AnimationChannel(AnimationChannel.Targets.ROTATION,
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(-12.5F, 5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(-12.5F, -5.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR)))
			.build();
}