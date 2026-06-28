package goblinbob.mobends.core.client;

import goblinbob.mobends.standard.mutators.BipedMutator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    public static void clear() {
        currentBipedMutator.remove();
        inMainModelRender.remove();
    }
}
