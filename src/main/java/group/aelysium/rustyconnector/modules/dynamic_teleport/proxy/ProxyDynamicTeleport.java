package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.modules.ExternalModuleTinder;
import group.aelysium.rustyconnector.common.modules.ModuleCollection;
import group.aelysium.rustyconnector.common.modules.ModuleParticle;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.Invitation;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAProvider;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr.TPRConfig;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.warp.WarpConfig;
import group.aelysium.rustyconnector.proxy.ProxyKernel;
import group.aelysium.rustyconnector.shaded.group.aelysium.ara.Particle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProxyDynamicTeleport implements ModuleParticle {
    protected ModuleCollection modules = new ModuleCollection();

    public ProxyDynamicTeleport(
            @NotNull TPAConfig config
    ) throws Exception {
        {
            modules.registerModule(TPAConfig.New().tinder());
            modules.registerModule(TPRConfig.New().tinder());
            modules.registerModule(WarpConfig.New().tinder());
        }
    }

    public Flux<? extends TPAProvider> tpa() {
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

    public static class Tinder extends ExternalModuleTinder<ProxyDynamicTeleport> {
        public void bind(@NotNull ProxyKernel kernel, @NotNull Particle instance) {
        }

        @NotNull
        @Override
        public ProxyDynamicTeleport onStart() throws Exception {
            return new ProxyDynamicTeleport(TPAConfig.New());
        }
    }
}