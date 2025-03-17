package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr;

import group.aelysium.rustyconnector.common.modules.Module;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TPRProvider implements Module {
    protected final TPRConfig config;

    public TPRProvider(
            @NotNull TPRConfig config
    ) throws Exception {
        this.config = config;
    }

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() throws Exception {
    }
}
