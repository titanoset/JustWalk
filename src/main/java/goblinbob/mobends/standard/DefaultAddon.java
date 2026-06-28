package goblinbob.mobends.standard;

import goblinbob.mobends.core.addon.AddonAnimationRegistry;
import goblinbob.mobends.core.addon.IAddon;
import goblinbob.mobends.standard.client.model.armor.ArmorModelFactory;
import goblinbob.mobends.standard.client.renderer.entity.ArrowTrailManager;
import goblinbob.mobends.standard.main.ModConfig;

public class DefaultAddon implements IAddon
{
	@Override
	public void registerContent(AddonAnimationRegistry registry)
	{
		registry.registerEntity(new PlayerBender());
	}

	@Override
	public void onRenderTick(float partialTicks)
	{
		if (ModConfig.showArrowTrails)
			ArrowTrailManager.onRenderTick();
	}

	@Override
	public void onClientTick()
	{
	}

	@Override
	public void onRefresh()
	{
		ArmorModelFactory.refresh();
	}

	@Override
	public String getDisplayName()
	{
		return "Default";
	}
}
