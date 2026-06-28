package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.network.NetworkConfiguration;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldJoinHandler
{

    @SubscribeEvent
    public void onPlayerJoinedServer(EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof AbstractClientPlayer)
        {
            NetworkConfiguration.instance.onWorldJoin();
        }
    }

}
