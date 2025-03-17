package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
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
        if(!this.status.get().equals(Status.PENDING)) return new JoinAttempt(false, "Your invitation to that party is expired.");
        if(this.expired()) return new JoinAttempt(false, "That party no-longer exists.");
        this.status.set(Status.ACCEPTED);
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