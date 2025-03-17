package group.aelysium.rustyconnector.modules.dynamic_teleport.common;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.magic_link.packet.Packet;
import group.aelysium.rustyconnector.common.magic_link.packet.PacketListener;
import group.aelysium.rustyconnector.server.ServerKernel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TeleportDemandPacket extends Packet.Remote {
    protected String playerToTeleport = null;
    protected String targetPlayer = null;
    protected Long x = null;
    protected Long y = null;
    protected Long z = null;
    protected Long pitch = null;
    protected Long yaw = null;

    protected TeleportDemandPacket(Packet packet) {
        super(packet);
        this.playerToTeleport = this.parameters().get("p").getAsString();

        try {
            this.targetPlayer = this.parameters().get("tp").getAsString();
            return;
        } catch (Exception ignore) {}

        try {
            this.x = this.parameters().get("x").getAsLong();
        } catch (Exception ignore) {}
        try {
            this.y = this.parameters().get("y").getAsLong();
        } catch (Exception ignore) {}
        try {
            this.z = this.parameters().get("x").getAsLong();
        } catch (Exception ignore) {}
        try {
            this.pitch = this.parameters().get("pitch").getAsLong();
        } catch (Exception ignore) {}
        try {
            this.yaw = this.parameters().get("yaw").getAsLong();
        } catch (Exception ignore) {}

        if(this.x == null && this.y == null && this.z == null && this.pitch == null && this.yaw == null)
            throw new IllegalStateException("A TeleportDemandPacket must have at least one of tp, x, y, z, pitch, or yaw defined.");
    }

    public boolean coordinateTeleport() {
        return this.targetPlayer != null;
    }

    public String playerToTeleport() {
        return this.playerToTeleport;
    }
    public Optional<String> targetPlayer() {
        return Optional.ofNullable(this.targetPlayer);
    }
    public Optional<Long> x() {
        return Optional.ofNullable(this.x);
    }
    public Optional<Long> y() {
        return Optional.ofNullable(this.y);
    }
    public Optional<Long> z() {
        return Optional.ofNullable(this.z);
    }
    public Optional<Long> pitch() {
        return Optional.ofNullable(this.pitch);
    }
    public Optional<Long> yaw() {
        return Optional.ofNullable(this.yaw);
    }


    public static Packet.Local createAndSend(@NotNull SourceIdentifier target, @NotNull UUID playerToTeleport, @NotNull UUID targetPlayer) {
        return Packet.New()
                .identification(Packet.Type.from("RCM_DYNAMIC_TELEPORT", "TP"))
                .parameter("p", playerToTeleport.toString())
                .parameter("tp", targetPlayer.toString())
                .addressTo(target)
                .send();
    }
    public static Packet.Local createAndSend(
            @NotNull SourceIdentifier target,
            @NotNull UUID playerToTeleport,
            @Nullable Long x,
            @Nullable Long y,
            @Nullable Long z
    ) {
        return TeleportDemandPacket.createAndSend(target, playerToTeleport, x, y, z, null, null);
    }
    public static Packet.Local createAndSend(
            @NotNull SourceIdentifier target,
            @NotNull UUID playerToTeleport,
            @Nullable Long x,
            @Nullable Long y,
            @Nullable Long z,
            @Nullable Long pitch,
            @Nullable Long yaw
    ) {
        if(x == null && y == null && z == null && pitch == null && yaw == null)
            throw new IllegalStateException("A TeleportDemandPacket must have at least one of x, y, z, pitch, or yaw defined.");

        Packet.Builder.PrepareForSending packet = Packet.New()
                .identification(Packet.Type.from("RCM_DYNAMIC_TELEPORT", "TP"))
                .parameter("p", playerToTeleport.toString());

        if(x != null) packet.parameter("x", new Parameter(x));
        if(y != null) packet.parameter("y", new Parameter(y));
        if(z != null) packet.parameter("z", new Parameter(z));
        if(pitch != null) packet.parameter("pitch", new Parameter(pitch));
        if(yaw != null) packet.parameter("yaw", new Parameter(yaw));

        return packet.addressTo(target).send();
    }

    public static class Listener {
        @PacketListener(TeleportDemandPacket.class)
        public PacketListener.Response handle(TeleportDemandPacket packet) {
            if(!(RC.Kernel() instanceof ServerKernel)) return PacketListener.Response.error("This TeleportDemandPacket.Listener can only be called on the server.");

            if(!RC.S.Adapter().isOnline(packet.playerToTeleport()))
                return PacketListener.Response.error("Unable to teleport "+packet.playerToTeleport()+" they are not online.");

            try {
                if (packet.targetPlayer().isPresent()) {
                    String targetPlayer = packet.targetPlayer().orElseThrow();
                    if(!RC.S.Adapter().isOnline(targetPlayer))
                        return PacketListener.Response.error("Unable to teleport "+targetPlayer+" they are not online.");

                    RC.S.Adapter().teleport(packet.playerToTeleport(), targetPlayer);
                    return PacketListener.Response.success("Teleport request successfully handled.");
                }

                RC.S.Adapter().teleport(
                        packet.playerToTeleport(),
                        packet.x().orElse(null),
                        packet.y().orElse(null),
                        packet.z().orElse(null),
                        packet.pitch().orElse(null),
                        packet.yaw().orElse(null)
                );
                return PacketListener.Response.success("Teleport request successfully handled.");
            } catch (Exception e) {
                return PacketListener.Response.error("There was an isue processing hte teleport request. "+e.getMessage());
            }
        }
    }
}
