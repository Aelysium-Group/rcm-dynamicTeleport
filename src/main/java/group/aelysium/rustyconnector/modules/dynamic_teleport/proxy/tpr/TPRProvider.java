package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.modules.ModuleParticle;
import group.aelysium.rustyconnector.common.modules.ModuleTinder;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.Invitation;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TPRProvider implements ModuleParticle {
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

    public static class Tinder extends ModuleTinder<TPRProvider> {
        public Tinder() {
            super("TPRProvider", "Provides random teleportation capabilities which work trans-server.");
        }

        @NotNull
        @Override
        public TPRProvider ignite() throws Exception {
            return null;
        }
    }
}
