package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.modules.ModuleParticle;
import group.aelysium.rustyconnector.common.modules.ModuleTinder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TPAProvider implements ModuleParticle {
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    protected final Map<UUID, Invitation> invitations = new ConcurrentHashMap<>();
    protected final TPAConfig config;

    public TPAProvider (
            @NotNull TPAConfig config
    ) throws Exception {
        this.config = config;
    }

    public @NotNull Invitation sendNewInvitation(@NotNull UUID from, @NotNull UUID to) {
        Invitation invitation = new Invitation(this, from, to);
        this.invitations.put(to, invitation);
        return invitation;
    }
    public @NotNull Optional<Invitation> fetchInvitation(@NotNull UUID target) {
        return Optional.ofNullable(this.invitations.get(target));
    }

    private void clean() {
        try {
            Set<UUID> expired = new HashSet<>();
            this.invitations.forEach((u, i) -> {
                if(i.expired()) expired.add(u);
            });
            expired.forEach(this.invitations::remove);
        } catch (Exception e) {
            RC.Error(group.aelysium.rustyconnector.common.errors.Error.from(e).whileAttempting("To clear out expired tpa invitations."));
        }

        this.cleaner.schedule(this::clean, 20, TimeUnit.SECONDS);
    }

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() throws Exception {
        this.invitations.clear();
        this.cleaner.close();
    }

    public static class Tinder extends ModuleTinder<TPAProvider> {
        public Tinder() {
            super("TPAProvider", "Provides TPA actions for users so that they can teleport to each-other.");
        }

        @NotNull
        @Override
        public TPAProvider ignite() throws Exception {
            return null;
        }
    }
}
