package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.MoBendsAccessoryTransforms;
import goblinbob.mobends.core.client.MoBendsPoseSync;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Re-syncs MoBends poses after vanilla {@code setupAnim} overwrites them,
 * so external render layers can follow animated body parts.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
{
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void mobends$syncPosesAfterSetupAnim(T entity, float entityYaw, float partialTicks,
                                                 PoseStack poseStack, MultiBufferSource buffer,
                                                 int packedLight, CallbackInfo ci)
    {
        MoBendsPoseSync.syncAnimatedPlayerModel(entity, (LivingEntityRenderer<?, ?>) (Object) this);
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"
            )
    )
    private void mobends$renderLayerWithSneakFix(RenderLayer<T, M> layer, PoseStack poseStack,
                                                 MultiBufferSource buffer, int packedLight, Entity entity,
                                                 float limbSwing, float limbSwingAmount, float partialTicks,
                                                 float ageInTicks, float netHeadYaw, float headPitch)
    {
        @SuppressWarnings("unchecked")
        T typedEntity = (T) entity;

        if (entity instanceof LivingEntity living
                && MoBendsAccessoryTransforms.shouldAdjustExternalLayer(living, layer))
        {
            poseStack.pushPose();
            MoBendsAccessoryTransforms.applySneakCompensation(poseStack, living);
            layer.render(poseStack, buffer, packedLight, typedEntity, limbSwing, limbSwingAmount,
                    partialTicks, ageInTicks, netHeadYaw, headPitch);
            poseStack.popPose();
        }
        else
        {
            layer.render(poseStack, buffer, packedLight, typedEntity, limbSwing, limbSwingAmount,
                    partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
