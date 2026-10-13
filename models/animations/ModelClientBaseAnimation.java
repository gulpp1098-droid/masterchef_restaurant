// Save this class in your mod and generate all required imports

/**
 * Made with Blockbench 5.2.2 Exported for Minecraft version 1.19 or later with
 * Mojang mappings
 * 
 * @author Author
 */
public class ModelClientBaseAnimation {
	public static final AnimationDefinition Eat = AnimationDefinition.Builder.withLength(3.0F).looping()
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
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.75F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.0F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.25F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.5F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(1.75F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.0F, KeyframeAnimations.degreeVec(25.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.25F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 57.5F),
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
							new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 87.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.75F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.25F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 57.5F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 87.5F),
									AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("Righthand",
					new AnimationChannel(AnimationChannel.Targets.POSITION,
							new Keyframe(0.0F, KeyframeAnimations.posVec(4.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.75F, KeyframeAnimations.posVec(-1.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(2.25F, KeyframeAnimations.posVec(-1.0F, 0.0F, 0.0F),
									AnimationChannel.Interpolations.LINEAR),
							new Keyframe(3.0F, KeyframeAnimations.posVec(4.0F, 0.0F, 0.0F),
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