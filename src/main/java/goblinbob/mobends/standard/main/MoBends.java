package goblinbob.mobends.standard.main;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.AddonHelper;
import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.pack.PackDataProvider;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.standard.DefaultAddon;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

/**
 * Client-only entry point for JustWalk.
 */
@Mod(ModStatics.MODID)
public class MoBends
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Logger LOG = LOGGER;
    public static MoBends instance;

    public MoBends()
    {
        instance = this;

        // Allow joining servers that do not have this mod installed.
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remoteVersion, isServer) -> true));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        LOGGER.info("JustWalk {} initializing...", ModStatics.VERSION);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        Core.createAsClient();
        Core.getInstance().onClientSetup();

        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());
        Core.getInstance().applyConfigurationToEntityBenders();

        LOGGER.info("JustWalk client setup complete");
    }

    public static void refreshSystems()
    {
        AnimationLoader.clearCache();
        GsonResources.clearCache();
        PackDataProvider.INSTANCE.clearCache();
        EntityDatabase.instance.refresh();
        EntityBenderRegistry.instance.refreshMutators();
        Addons.onRefresh();

        Core.getInstance().refreshModules();
    }
}
