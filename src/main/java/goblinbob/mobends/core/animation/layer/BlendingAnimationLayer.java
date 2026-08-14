package goblinbob.mobends.core.animation.layer;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.math.Quaternion;
import goblinbob.mobends.core.math.SmoothOrientation;
import goblinbob.mobends.core.math.vector.SmoothVector3f;
import goblinbob.mobends.core.util.Tween;

import java.util.HashMap;
import java.util.Map;

/**
 * Animation layer that crossfades between procedural bits by snapshotting the
 * current pose on switch and lerping toward the new bit's targets.
 */
public class BlendingAnimationLayer<T extends EntityData<?>> extends AnimationLayer<T>
{
	private static final float DEFAULT_TRANSITION_DURATION = 6.0F;

	private static final String[] ORIENTATION_PARTS = new String[] {
			"head", "body",
			"leftArm", "rightArm", "leftLeg", "rightLeg",
			"leftForeArm", "rightForeArm", "leftForeLeg", "rightForeLeg",
			"renderRotation", "centerRotation",
			"renderRightItemRotation", "renderLeftItemRotation"
	};

	protected AnimationBit<T> performedBit;
	protected AnimationBit<T> previousBit;

	protected float transitionDuration = DEFAULT_TRANSITION_DURATION;
	protected float transitionProgress = 0F;
	protected boolean transitioning = false;

	private final Map<String, Quaternion> orientationSnapshot = new HashMap<>();
	private final float[] globalOffsetSnapshot = new float[3];
	private final float[] localOffsetSnapshot = new float[3];
	private boolean hasOffsetSnapshot = false;

	@SuppressWarnings("unchecked")
	public void playBit(AnimationBit<? extends T> bit, T entityData)
	{
		this.previousBit = this.performedBit;
		this.performedBit = (AnimationBit<T>) bit;
		this.performedBit.setupForPlay(this, entityData);

		if (this.previousBit != null && this.transitionDuration > 0F)
		{
			capturePose(entityData);
			this.transitionProgress = 0F;
			this.transitioning = true;
		}
		else
		{
			clearTransition();
		}
	}

	public void playOrContinueBit(AnimationBit<? extends T> bit, T entityData)
	{
		if (!this.isPlaying(bit))
			this.playBit(bit, entityData);
	}

	public void setTransitionDuration(float duration)
	{
		this.transitionDuration = Math.max(0F, duration);
	}

	@Override
	public void perform(T entityData)
	{
		if (performedBit == null)
			return;

		performedBit.perform(entityData);

		if (!transitioning)
			return;

		transitionProgress += DataUpdateHandler.ticksPerFrame;
		float rawT = transitionDuration <= 0F ? 1F : Math.min(1F, transitionProgress / transitionDuration);
		float t = (float) Tween.easeInOut(rawT, 2.0);

		applyBlend(entityData, t);

		if (rawT >= 1F)
		{
			clearTransition();
		}
	}

	public boolean isPlaying(AnimationBit<? extends T> bit)
	{
		return bit == this.performedBit;
	}

	public boolean isPlaying()
	{
		return this.performedBit != null;
	}

	public void clearAnimation()
	{
		this.performedBit = null;
		this.previousBit = null;
		clearTransition();
	}

	public AnimationBit<T> getPerformedBit()
	{
		return this.performedBit;
	}

	@Override
	public String[] getActions(T entityData)
	{
		if (this.isPlaying())
		{
			return this.getPerformedBit().getActions(entityData);
		}
		return new String[] {};
	}

	private void clearTransition()
	{
		this.transitioning = false;
		this.transitionProgress = 0F;
		this.orientationSnapshot.clear();
		this.hasOffsetSnapshot = false;
	}

	private void capturePose(T entityData)
	{
		orientationSnapshot.clear();

		for (String name : ORIENTATION_PARTS)
		{
			SmoothOrientation orientation = resolveOrientation(entityData, name);
			if (orientation != null)
			{
				Quaternion q = new Quaternion();
				q.set(orientation.getSmooth());
				orientationSnapshot.put(name, q);
			}
		}

		globalOffsetSnapshot[0] = entityData.globalOffset.getX();
		globalOffsetSnapshot[1] = entityData.globalOffset.getY();
		globalOffsetSnapshot[2] = entityData.globalOffset.getZ();
		localOffsetSnapshot[0] = entityData.localOffset.getX();
		localOffsetSnapshot[1] = entityData.localOffset.getY();
		localOffsetSnapshot[2] = entityData.localOffset.getZ();
		hasOffsetSnapshot = true;
	}

	private void applyBlend(T entityData, float t)
	{
		for (Map.Entry<String, Quaternion> entry : orientationSnapshot.entrySet())
		{
			SmoothOrientation orientation = resolveOrientation(entityData, entry.getKey());
			if (orientation == null)
				continue;

			Quaternion from = entry.getValue();
			Quaternion to = orientation.getEnd();
			float x = from.x + (to.x - from.x) * t;
			float y = from.y + (to.y - from.y) * t;
			float z = from.z + (to.z - from.z) * t;
			float w = from.w + (to.w - from.w) * t;
			orientation.set(x, y, z, w);
		}

		if (hasOffsetSnapshot)
		{
			blendVector(entityData.globalOffset, globalOffsetSnapshot, t);
			blendVector(entityData.localOffset, localOffsetSnapshot, t);
		}
	}

	private static void blendVector(SmoothVector3f target, float[] from, float t)
	{
		float x = from[0] + (target.end.x - from[0]) * t;
		float y = from[1] + (target.end.y - from[1]) * t;
		float z = from[2] + (target.end.z - from[2]) * t;
		target.set(x, y, z);
	}

	private static SmoothOrientation resolveOrientation(EntityData<?> data, String name)
	{
		Object part = data.getPartForName(name);
		if (part instanceof SmoothOrientation)
			return (SmoothOrientation) part;
		if (part instanceof IModelPart)
			return ((IModelPart) part).getRotation();
		return null;
	}
}
