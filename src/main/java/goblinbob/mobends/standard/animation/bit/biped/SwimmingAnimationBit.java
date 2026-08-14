package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.util.Tween;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class SwimmingAnimationBit extends AnimationBit<BipedEntityData<?>>
{
	private static final String[] ACTIONS = new String[] { "swimming", "swimming_surface" };
	private static final String[] ACTIONS_UNDERWATER = new String[] { "swimming", "swimming_deep" };
	private static final float PI = (float) Math.PI;
	private static final double STROKE_FORWARD_THRESHOLD = 0.005;
	private static final float STROKE_SPEED = 0.35F;

	private float transformTransition = 0F;
	private float transitionSpeed = 0.1F;

	@Override
	public String[] getActions(BipedEntityData<?> data)
	{
		if (data.isUnderwater() || data.getEntity().isSwimming())
			return ACTIONS_UNDERWATER;
		else
			return ACTIONS;
	}

	@Override
	public void onPlay(BipedEntityData<?> data)
	{
		transformTransition = 0F;
		transitionSpeed = .1F;
	}

	@Override
	public void perform(BipedEntityData<?> data)
	{
		float ticks = DataUpdateHandler.getTicks();

		LivingEntity entity = data.getEntity();
		boolean vanillaSwimming = entity.isSwimming();
		boolean useStrokePose = (vanillaSwimming
				|| (data.isUnderwater()
					&& !data.isStillHorizontally()
					&& Math.abs(data.getForwardMomentum()) > STROKE_FORWARD_THRESHOLD))
				&& !data.isDrawingBow()
				&& data.getTicksAfterAttack() >= 10;

		float t = (float) Tween.easeInOut(this.transformTransition, 3F);

		if (!useStrokePose)
		{
			if (this.transformTransition > 0F)
			{
				this.transformTransition -= DataUpdateHandler.ticksPerFrame * this.transitionSpeed;
				this.transformTransition = Math.max(0F, this.transformTransition);
			}

			float armSway = (Mth.cos(ticks * .0825F) + 1) / 2;
			float armSway2 = (-Mth.sin(ticks * .0825F) + 1) / 2;
			float legFlap = Mth.cos(ticks * .2625F);

			data.leftArm.rotation.setSmoothness(.3F).orientX(armSway2 * 30 - 15).rotateZ(-armSway * 30);
			data.rightArm.rotation.setSmoothness(.3F).orientX(armSway2 * 30 - 15).rotateZ(armSway * 30);
			data.leftForeArm.rotation.setSmoothness(.3F).orientX(armSway2 * -40);
			data.rightForeArm.rotation.setSmoothness(.3F).orientX(armSway2 * -40);
			data.leftLeg.rotation.setSmoothness(.3F).orientX(legFlap * 40);
			data.rightLeg.rotation.setSmoothness(.3F).orientX(-legFlap * 40);
			data.leftForeLeg.rotation.setSmoothness(.4F).orientX(5);
			data.rightForeLeg.rotation.setSmoothness(.4F).orientX(5);
			data.body.rotation.orientX(armSway * 10);
		}
		else
		{
			if (this.transformTransition < 1F)
			{
				this.transformTransition += DataUpdateHandler.ticksPerFrame * this.transitionSpeed;
				this.transformTransition = Math.min(this.transformTransition, 1F);
			}

			float leftPhase = ticks * STROKE_SPEED;
			float rightPhase = leftPhase + PI;

			float leftCycle = (Mth.cos(leftPhase) + 1F) * 0.5F;
			float rightCycle = (Mth.cos(rightPhase) + 1F) * 0.5F;
			float leftSin = Mth.sin(leftPhase);
			float rightSin = Mth.sin(rightPhase);

			// Alternating freestyle: one arm pulls while the other recovers.
			data.leftArm.rotation.setSmoothness(.3F)
					.orientX(-30F - leftCycle * 110F)
					.rotateY(-70F * t)
					.rotateZ(-15F + leftSin * 20F);
			data.rightArm.rotation.setSmoothness(.3F)
					.orientX(-30F - rightCycle * 110F)
					.rotateY(70F * t)
					.rotateZ(15F - rightSin * 20F);

			data.leftForeArm.rotation.setSmoothness(.3F).orientX(-20F - leftCycle * 50F);
			data.rightForeArm.rotation.setSmoothness(.3F).orientX(-20F - rightCycle * 50F);

			float leftLegKick = Mth.cos(leftPhase + PI);
			float rightLegKick = Mth.cos(rightPhase + PI);
			data.leftLeg.rotation.setSmoothness(.3F).orientX(leftLegKick * 35F);
			data.rightLeg.rotation.setSmoothness(.3F).orientX(rightLegKick * 35F);

			data.leftForeLeg.rotation.setSmoothness(.4F).orientX(10F + Math.max(0F, -leftLegKick) * 25F);
			data.rightForeLeg.rotation.setSmoothness(.4F).orientX(10F + Math.max(0F, -rightLegKick) * 25F);

			data.body.rotation.setSmoothness(.5F).orientX(20F + t * 55F);

			data.renderRightItemRotation.setSmoothness(.3F).orientX(rightCycle * 40F);
			data.renderLeftItemRotation.setSmoothness(.3F).orientX(leftCycle * 40F);
		}

		data.head.rotation.setSmoothness(1.0F).orientX(data.headPitch.get())
				.rotateY(data.headYaw.get());

		if (useStrokePose)
		{
			data.head.rotation.rotateX(-60F * t);
		}

		// Keep the entity render origin aligned with the hitbox; pose changes stay on limbs.
		data.renderRotation.setSmoothness(.7F).orientZero();
		data.globalOffset.slideToZero(0.7F);
		data.localOffset.slideToZero(0.3F);
	}
}
