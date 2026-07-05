package goblinbob.mobends.core.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.MoBendsPoseSync;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.core.compat.PlayerAnimationLibCompat;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityRenderHandler
{
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void beforeLivingRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<?>> event)
    {
        final LivingEntity living = event.getEntity();
        final EntityBender<LivingEntity> entityBender = EntityBenderRegistry.instance.getForEntity(living);

        if (entityBender == null)
        {
            return;
        }

        final LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> renderer =
            (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) event.getRenderer();
        final float pt = event.getPartialTick();
        final PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();

        if (PlayerAnimationLibCompat.hasActiveAnimation(living))
        {
            entityBender.deapplyMutation(renderer, living);
            return;
        }

        if (entityBender.isAnimated())
        {
            if (entityBender.applyMutation(renderer, living, pt))
            {
                final Object rawMutator = entityBender.getMutator(renderer);
                final Mutator<?, LivingEntity, ?> mutator =
                    (Mutator<?, LivingEntity, ?>) rawMutator;
                final LivingEntityData<LivingEntity> data =
                    (LivingEntityData<LivingEntity>) mutator.getData(living);

                if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator)
                {
                    MoBendsRenderContext.setCurrentBipedMutator(bipedMutator);
                    MoBendsRenderContext.beginMainModelRender();

                    MoBendsPoseSync.syncAnimatedPlayerModel(living, renderer);
                }

                entityBender.beforeRender(data, living, pt, poseStack);
            }
        }
        else
        {
            entityBender.deapplyMutation(renderer, living);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void afterLivingRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<?>> event)
    {
        MoBendsRenderContext.clear();

        final EntityBender<LivingEntity> entityBender = EntityBenderRegistry.instance.getForEntity(event.getEntity());

        if (entityBender == null)
            return;

        entityBender.afterRender((LivingEntity) event.getEntity(), event.getPartialTick(), event.getPoseStack());

        event.getPoseStack().popPose();
    }
}
