package goblinbob.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.data.EntityData;
import net.minecraft.client.player.AbstractClientPlayer;

/**
 * Renderer for player entities with Mo' Bends animations.
 * Updated for 1.20.1 to use PoseStack instead of GlStateManager.
 */
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer>
{
    // Net crouch compensation after vanilla setupRotations (+0.125Y) and sneak globalOffset
    private static final float CROUCH_Y_OFFSET = 2.0F;
    private static final float CROUCH_FLYING_Y_OFFSET = 1.5F;

    @Override
    protected void transformLocally(AbstractClientPlayer entity, EntityData<?> data, float partialTicks, PoseStack poseStack)
    {
        if (entity.isCrouching())
        {
            if (entity.getAbilities().flying)
            {
                poseStack.translate(0F, CROUCH_FLYING_Y_OFFSET * scale, 0F);
            }
            else
            {
                poseStack.translate(0F, CROUCH_Y_OFFSET * scale, 0F);
            }
        }
    }

}
