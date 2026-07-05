package goblinbob.mobends.core.client;

import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.util.BenderHelper;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Keeps vanilla {@link HumanoidModel} part rotations aligned with MoBends animation
 * so third-party render layers (Curios, backpack mods, etc.) can use
 * {@code model.body.translateAndRotate()} and follow body bends.
 */
@OnlyIn(Dist.CLIENT)
public final class MoBendsPoseSync
{
    private MoBendsPoseSync()
    {
    }

    public static void syncAnimatedPlayerModel(LivingEntity entity, LivingEntityRenderer<?, ?> renderer)
    {
        if (!BenderHelper.isEntityAnimated(entity))
        {
            return;
        }

        if (PlayerAnimationLibCompat.hasActiveAnimation(entity))
        {
            return;
        }

        EntityModel<?> model = renderer.getModel();
        if (!(model instanceof HumanoidModel<?> humanoidModel))
        {
            return;
        }

        @SuppressWarnings("unchecked")
        Class<LivingEntity> entityClass = (Class<LivingEntity>) entity.getClass();
        @SuppressWarnings("unchecked")
        LivingEntityRenderer<LivingEntity, ?> typedRenderer = (LivingEntityRenderer<LivingEntity, ?>) renderer;
        Mutator<?, ?, ?> mutator = BenderHelper.getMutatorForRenderer(entityClass, typedRenderer);
        if (mutator instanceof BipedMutator<?, ?, ?> bipedMutator && bipedMutator.shouldRenderCustom())
        {
            bipedMutator.syncPosesToVanillaModel(humanoidModel);
        }
    }
}
