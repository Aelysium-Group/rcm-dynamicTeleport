package group.aelysium.rustyconnector.modules.dynamic_teleport.server;

import group.aelysium.rustyconnector.common.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.common.modules.ExternalModuleTinder;
import group.aelysium.rustyconnector.common.modules.ModuleParticle;
import group.aelysium.rustyconnector.modules.dynamic_teleport.common.TeleportDemandPacket;
import group.aelysium.rustyconnector.server.ServerKernel;
import group.aelysium.rustyconnector.shaded.group.aelysium.ara.Particle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerDynamicTeleport implements ModuleParticle {

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() {
    }

    public static class Tinder extends ExternalModuleTinder<ServerDynamicTeleport> {
        public void bind(@NotNull ServerKernel kernel, @NotNull Particle instance) {
            kernel.fetchModule("MagicLink").onStart(m -> {
                ((MagicLinkCore) m).listen(new TeleportDemandPacket.Listener());
            });
        }

        @NotNull
        @Override
        public ServerDynamicTeleport onStart() throws Exception {
            return new ServerDynamicTeleport();
        }
    }
}