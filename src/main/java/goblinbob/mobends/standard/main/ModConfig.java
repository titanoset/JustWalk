package goblinbob.mobends.standard.main;

import goblinbob.mobends.standard.AttackActionType;
import goblinbob.mobends.standard.UseActionType;
import net.minecraft.world.item.Item;

/**
 * Built-in gameplay defaults for the player-only build.
 */
public class ModConfig
{
    public static final boolean showArrowTrails = true;
    public static final boolean showSwordTrail = true;
    public static final boolean performSpinAttack = true;

    public static UseActionType getItemUseAction(Item item)
    {
        return null;
    }

    public static AttackActionType getItemAttackAction(Item item)
    {
        return null;
    }

    public static boolean shouldKeepArmorAsVanilla(Item item)
    {
        return false;
    }
}
