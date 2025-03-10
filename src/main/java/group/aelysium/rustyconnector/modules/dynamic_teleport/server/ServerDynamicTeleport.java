package group.aelysium.rustyconnector.modules.dynamic_teleport.server;

import group.aelysium.rustyconnector.common.magic_link.MagicLinkCore;
import group.aelysium.rustyconnector.common.modules.ExternalModuleBuilder;
import group.aelysium.rustyconnector.common.modules.Module;
import group.aelysium.rustyconnector.modules.dynamic_teleport.common.TeleportDemandPacket;
import group.aelysium.rustyconnector.server.ServerKernel;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ServerDynamicTeleport implements Module {

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() {
    }

    public static class Tinder extends ExternalModuleBuilder<ServerDynamicTeleport> {
        public void bind(@NotNull ServerKernel kernel, @NotNull ServerDynamicTeleport instance) {
            kernel.fetchModule("MagicLink").onStart(m -> {
                ((MagicLinkCore) m).listen(new TeleportDemandPacket.Listener());
            });
        }

        @NotNull
        @Override
        public ServerDynamicTeleport onStart(@NotNull Path dataDirectory) throws Exception {
            return new ServerDynamicTeleport();
        }
    }
}