package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.magic_link.packet.Packet;
import group.aelysium.rustyconnector.modules.dynamic_teleport.common.TeleportDemandPacket;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.ProxyDynamicTeleport;
import group.aelysium.rustyconnector.proxy.family.Family;
import group.aelysium.rustyconnector.proxy.family.FamilyRegistry;
import group.aelysium.rustyconnector.proxy.family.Server;
import group.aelysium.rustyconnector.proxy.player.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Invitation {
    private final TPAProvider provider;
    protected final UUID sender;
    protected final UUID target;
    protected final AtomicReference<Status> status = new AtomicReference<>(Status.PENDING);
    protected final Instant issuedAt = Instant.now();

    protected Invitation(
            @NotNull TPAProvider provider,
            @NotNull UUID sender,
            @NotNull UUID target
    ) {
        this.provider = provider;
        this.sender = sender;
        this.target = target;
    }

    public UUID sender() {
        return this.sender;
    }
    public UUID target() {
        return this.target;
    }
    public Status status() {
        return this.status.get();
    }
    public boolean expired() {
        return this.issuedAt.plusSeconds(60).isBefore(Instant.now());
    }

    public void accept() {
        Player sender = RC.P.Player(this.sender).orElseThrow(()->new NoSuchElementException("The player "+this.sender+" isn't online."));
        Player target = RC.P.Player(this.target).orElseThrow(()->new NoSuchElementException("The player "+this.target+" isn't online."));
        Server targetServer = target.server().orElseThrow(()->new NoSuchElementException("The target's server isn't available."));
        Server senderServer = sender.server().orElseThrow(()->new NoSuchElementException("The sender's server isn't available."));
        
        Family targetFamily = target.family().orElse(null);
        if(targetFamily == null) return;
        Family senderFamily = sender.family().orElse(null);
        if(senderFamily == null) return;
        
        // Make sure that the target and sender are within the proper family bounds.
        boolean validFamilyScope = false;
        for(List<String> l : this.provider.config.enabledFamilies) {
            if(!l.contains(senderFamily.id())) continue;
            
            validFamilyScope = l.contains(targetFamily.id());
            break;
        }
        if(!validFamilyScope) return;
        
        this.status.set(Status.ACCEPTED);
        TeleportDemandPacket.createAndSend(Packet.SourceIdentifier.server(targetServer.id()), sender.uuid(), target.uuid());
    }
    public void ignore() {
        if(!this.status.get().equals(Status.PENDING)) return;
        Invitation invitation = this.provider.invitations.get(this.target);

        if(!this.equals(invitation)) return;

        this.status.set(Status.IGNORED);
        this.provider.invitations.remove(this.target);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Invitation that = (Invitation) o;
        return Objects.equals(sender, that.sender) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, target);
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        IGNORED
    }
}