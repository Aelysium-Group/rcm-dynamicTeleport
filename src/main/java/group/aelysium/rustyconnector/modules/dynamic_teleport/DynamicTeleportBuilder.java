package group.aelysium.rustyconnector.modules.dynamic_teleport;

import group.aelysium.rustyconnector.common.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.common.modules.ExternalModuleBuilder;
import group.aelysium.rustyconnector.modules.dynamic_teleport.common.TeleportDemandPacket;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.ProxyDynamicTeleport;
import group.aelysium.rustyconnector.modules.dynamic_teleport.server.ServerDynamicTeleport;
import group.aelysium.rustyconnector.server.ServerKernel;
import org.jetbrains.annotations.NotNull;
import group.aelysium.rustyconnector.common.modules.Module;

public class DynamicTeleportBuilder extends ExternalModuleBuilder<Module> {
    public void bind(@NotNull ServerKernel kernel, @NotNull ServerDynamicTeleport instance) {
        kernel.fetchModule("MagicLink").onStart(m -> {
            ((MagicLinkCore) m).listen(new TeleportDemandPacket.Listener());
        });
    }

    @NotNull
    @Override
    public Module onStart(@NotNull ExternalModuleBuilder.Context context) throws Exception {
        if(context.currentEnvironment().equals("server")) return new ServerDynamicTeleport();
        if(context.currentEnvironment().equals("proxy")) return new ProxyDynamicTeleport();
        throw new IllegalStateException("DynamicTeleport only supports `proxy` or `server` environments! Current environment: "+context.currentEnvironment());
    }
}
