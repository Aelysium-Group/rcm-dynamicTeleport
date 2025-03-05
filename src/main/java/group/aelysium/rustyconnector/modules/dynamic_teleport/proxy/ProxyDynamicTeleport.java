package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy;

import group.aelysium.rustyconnector.common.modules.ExternalModuleBuilder;
import group.aelysium.rustyconnector.common.modules.Module;
import group.aelysium.rustyconnector.common.modules.ModuleCollection;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAProvider;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr.TPRConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.warp.WarpConfig;
import group.aelysium.rustyconnector.proxy.ProxyKernel;
import group.aelysium.rustyconnector.shaded.group.aelysium.ara.Flux;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ProxyDynamicTeleport implements Module {
    protected ModuleCollection<Module> modules = new ModuleCollection<>();

    public ProxyDynamicTeleport(
            @NotNull TPAConfig config
    ) throws Exception {
        {
            modules.registerModule(TPAConfig.New().tinder());
            modules.registerModule(TPRConfig.New().tinder());
            modules.registerModule(WarpConfig.New().tinder());
        }
    }

    public Flux<TPAProvider> tpa() {
        return this.modules.fetchModule("TPAProvider");
    }

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() throws Exception {
        this.modules.close();
    }

    public static class Tinder extends ExternalModuleBuilder<ProxyDynamicTeleport> {
        public void bind(@NotNull ProxyKernel kernel, @NotNull ProxyDynamicTeleport instance) {
        }

        @NotNull
        @Override
        public ProxyDynamicTeleport onStart(@NotNull Path dataDirectory) throws Exception {
            return new ProxyDynamicTeleport(TPAConfig.New());
        }
    }
}