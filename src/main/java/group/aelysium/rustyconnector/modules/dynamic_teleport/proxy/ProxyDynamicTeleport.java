package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.errors.Error;
import group.aelysium.rustyconnector.common.modules.Module;
import group.aelysium.rustyconnector.common.modules.ModuleCollection;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAProvider;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr.TPRConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr.TPRProvider;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.warp.WarpConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.warp.WarpProvider;
import group.aelysium.rustyconnector.shaded.group.aelysium.ara.Flux;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class ProxyDynamicTeleport implements Module {
    protected ModuleCollection<Module> modules = new ModuleCollection<>();

    public ProxyDynamicTeleport() throws Exception {
        if(TPAConfig.New().enabled)
            modules.registerModule(new Module.Builder<TPAProvider>("", "") {
                @Override
                public TPAProvider get() {
                    try {
                        return new TPAProvider(TPAConfig.New());
                    } catch (Exception e) {
                        RC.Error(Error.from(e).whileAttempting("To startup the TPAProvider."));
                    }
                    return null;
                }
            });

        if(TPRConfig.New().enabled)
            modules.registerModule(new Module.Builder<TPRProvider>("", "") {
                @Override
                public TPRProvider get() {
                    try {
                        return new TPRProvider(TPRConfig.New());
                    } catch (Exception e) {
                        RC.Error(Error.from(e).whileAttempting("To startup the TPRProvider."));
                    }
                    return null;
                }
            });

        if(WarpConfig.New().enabled)
            modules.registerModule(new Module.Builder<WarpProvider>("", "") {
                @Override
                public WarpProvider get() {
                    try {
                        return new WarpProvider(WarpConfig.New());
                    } catch (Exception e) {
                        RC.Error(Error.from(e).whileAttempting("To startup the WarpProvider."));
                    }
                    return null;
                }
            });
    }

    public @Nullable Flux<TPAProvider> tpaProvider() {
        return this.modules.fetchModule("TPAProvider");
    }

    public @Nullable Flux<TPRProvider> tprProvider() {
        return this.modules.fetchModule("TPRProvider");
    }

    public @Nullable Flux<WarpProvider> warpProvider() {
        return this.modules.fetchModule("WarpProvider");
    }

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() throws Exception {
        this.modules.close();
    }
}