package goblinbob.mobends.core.configuration;

/**
 * In-memory client configuration for the player-only build.
 */
public class CoreClientConfig extends CoreConfig
{
    public CoreClientConfig()
    {
        load();
    }

    @Override
    public void save()
    {
    }

    @Override
    public void load()
    {
    }

    public String[] getAppliedPacks()
    {
        return new String[0];
    }

    public void setAppliedPacks(String[] packNames)
    {
    }

    public void setAppliedPacks(java.util.Collection<String> packNames)
    {
    }

    public boolean isEntityAnimated(String alterEntryKey)
    {
        return true;
    }

    public void setEntityAnimated(String alterEntryKey, boolean animated)
    {
    }
}
