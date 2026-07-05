package goblinbob.mobends.core.client;

import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Thread-local context for passing mutation state during rendering.
 * Used by mixins to determine if custom rendering should be applied.
 */
@OnlyIn(Dist.CLIENT)
public class MoBendsRenderContext {

    private static final ThreadLocal<BipedMutator<?, ?, ?>> currentBipedMutator = new ThreadLocal<>();

    /**
     * Flag indicating we're rendering the main entity model (not layers like armor).
     */
    private static final ThreadLocal<Boolean> inMainModelRender = ThreadLocal.withInitial(() -> false);

    private static final ThreadLocal<Map<ModelPart, BendsModelPart>> partOverrides = new ThreadLocal<>();

    public static void beginMainModelRender() {
        inMainModelRender.set(true);
    }

    public static void endMainModelRender() {
        inMainModelRender.set(false);
    }

    public static boolean isInMainModelRender() {
        return inMainModelRender.get();
    }

    public static void setCurrentBipedMutator(BipedMutator<?, ?, ?> mutator) {
        currentBipedMutator.set(mutator);
    }

    public static BipedMutator<?, ?, ?> getCurrentBipedMutator() {
        return currentBipedMutator.get();
    }

    public static void beginPartOverrides() {
        partOverrides.set(new IdentityHashMap<>());
    }

    public static void registerPartOverride(ModelPart vanillaPart, BendsModelPart mobendsPart) {
        if (vanillaPart == null || mobendsPart == null) {
            return;
        }

        Map<ModelPart, BendsModelPart> overrides = partOverrides.get();
        if (overrides != null) {
            overrides.put(vanillaPart, mobendsPart);
        }
    }

    public static BendsModelPart getPartOverride(ModelPart vanillaPart) {
        Map<ModelPart, BendsModelPart> overrides = partOverrides.get();
        return overrides != null ? overrides.get(vanillaPart) : null;
    }

    public static void clear() {
        currentBipedMutator.remove();
        inMainModelRender.remove();
        partOverrides.remove();
    }
}
