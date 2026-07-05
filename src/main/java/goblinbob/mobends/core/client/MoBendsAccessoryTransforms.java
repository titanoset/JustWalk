package goblinbob.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.standard.data.PlayerData;
import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.util.BenderHelper;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Shared transforms for equipment rendered outside MoBends layers
 * (Curios slots, Sophisticated Backpacks, etc.).
 */
@OnlyIn(Dist.CLIENT)
public final class MoBendsAccessoryTransforms
{
    private static final float SCALE = 0.0625F;
    private static final float CROUCH_Y_OFFSET = 2.0F;
    private static final float CROUCH_FLYING_Y_OFFSET = 1.5F;

    private MoBendsAccessoryTransforms()
    {
    }

    /**
     * Reverts the extra crouch offset applied by {@link goblinbob.mobends.standard.client.renderer.entity.mutated.PlayerRenderer}.
     */
    public static void applySneakCompensation(PoseStack poseStack, LivingEntity entity)
    {
        if (!entity.isCrouching())
        {
            return;
        }

        if (entity instanceof AbstractClientPlayer player && player.getAbilities().flying)
        {
            poseStack.translate(0.0F, CROUCH_FLYING_Y_OFFSET * SCALE, 0.0F);
        }
        else
        {
            poseStack.translate(0.0F, CROUCH_Y_OFFSET * SCALE, 0.0F);
        }
    }

    public static boolean shouldAdjustExternalLayer(LivingEntity entity, RenderLayer<?, ?> layer)
    {
        if (!(entity instanceof AbstractClientPlayer player))
        {
            return false;
        }

        if (!BenderHelper.isEntityAnimated(player))
        {
            return false;
        }

        if (PlayerAnimationLibCompat.hasActiveAnimation(player))
        {
            return false;
        }

        if (layer.getClass().getName().startsWith("goblinbob.mobends."))
        {
            return false;
        }

        return player.isCrouching();
    }

    /**
     * Applies the full MoBends body bind transform for back/chest accessories.
     */
    public static boolean applyBodyCharacterTransform(PoseStack poseStack, LivingEntity entity)
    {
        EntityData<?> entityData = EntityDatabase.instance.get(entity);
        if (!(entityData instanceof PlayerData data))
        {
            return false;
        }

        IModelPart body = data.body;
        if (body == null)
        {
            return false;
        }

        body.applyCharacterTransform(poseStack, SCALE);
        return true;
    }
}
