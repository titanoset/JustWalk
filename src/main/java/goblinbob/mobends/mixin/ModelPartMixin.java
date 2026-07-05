package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.core.client.model.BendsModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Redirects vanilla {@link ModelPart#translateAndRotate} to MoBends limb transforms
 * for backpacks, Curios items, and other mods that attach to the player model.
 */
@Mixin(ModelPart.class)
public abstract class ModelPartMixin
{
    private static final float MODEL_SCALE = 0.0625F;

    @Inject(method = "translateAndRotate", at = @At("HEAD"), cancellable = true)
    private void mobends$redirectTranslateAndRotate(PoseStack poseStack, CallbackInfo ci)
    {
        BendsModelPart mobendsPart = MoBendsRenderContext.getPartOverride((ModelPart) (Object) this);
        if (mobendsPart != null)
        {
            mobendsPart.applyCharacterTransform(poseStack, MODEL_SCALE);
            ci.cancel();
        }
    }
}
