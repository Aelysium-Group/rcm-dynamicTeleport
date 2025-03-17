package group.aelysium.rustyconnector.modules.dynamic_teleport.common;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.magic_link.packet.Packet;
import group.aelysium.rustyconnector.common.magic_link.packet.PacketListener;
import group.aelysium.rustyconnector.server.ServerKernel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TeleportDemandPacket extends Packet.Remote {
    protected String playerToTeleport = null;
    protected String targetPlayer = null;
    protected String world = null;
    protected Double x = null;
    protected Double y = null;
    protected Double z = null;
    protected Float pitch = null;
    protected Float yaw = null;

    protected TeleportDemandPacket(Packet packet) {
        super(packet);
        this.playerToTeleport = this.parameters().get("p").getAsString();

        try {
            this.targetPlayer = this.parameters().get("tp").getAsString();
            return;
        } catch (Exception ignore) {}

        Map<String, Parameter> params = this.parameters();
        if (params.containsKey("w")) this.world = params.get("w").getAsString();
        if (params.containsKey("x")) this.x = params.get("x").getAsDouble();
        if (params.containsKey("y")) this.y = params.get("y").getAsDouble();
        if (params.containsKey("z")) this.z = params.get("z").getAsDouble();
        if (params.containsKey("pitch")) this.pitch = params.get("pitch").getAsFloat();
        if (params.containsKey("yaw")) this.yaw = params.get("yaw").getAsFloat();

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
    public Optional<String> world() {
        return Optional.ofNullable(this.world);
    }
    public Optional<Double> x() {
        return Optional.ofNullable(this.x);
    }
    public Optional<Double> y() {
        return Optional.ofNullable(this.y);
    }
    public Optional<Double> z() {
        return Optional.ofNullable(this.z);
    }
    public Optional<Float> pitch() {
        return Optional.ofNullable(this.pitch);
    }
    public Optional<Float> yaw() {
        return Optional.ofNullable(this.yaw);
    }


    public static Packet.Local createAndSend(@NotNull SourceIdentifier target, @NotNull String playerToTeleport, @NotNull String targetPlayer) {
        return Packet.New()
                .identification(Packet.Type.from("RCM_DYNAMIC_TELEPORT", "TP"))
                .parameter("p", playerToTeleport)
                .parameter("tp", targetPlayer)
                .addressTo(target)
                .send();
    }

    public static Packet.Local createAndSend(
        @NotNull SourceIdentifier target,
        @NotNull String playerToTeleport,
        @Nullable Double x,
        @Nullable Double y,
        @Nullable Double z
    ) {
        return TeleportDemandPacket.createAndSend(target, playerToTeleport, null, x, y, z, null, null);
    }

    public static Packet.Local createAndSend(
        @NotNull SourceIdentifier target,
        @NotNull String playerToTeleport,
        @Nullable String world,
        @Nullable Double x,
        @Nullable Double y,
        @Nullable Double z
    ) {
        return TeleportDemandPacket.createAndSend(target, playerToTeleport, world, x, y, z, null, null);
    }

    public static Packet.Local createAndSend(
            @NotNull SourceIdentifier target,
            @NotNull String playerToTeleport,
            @Nullable String world,
            @Nullable Double x,
            @Nullable Double y,
            @Nullable Double z,
            @Nullable Double pitch,
            @Nullable Double yaw
    ) {
        if(world == null && x == null && y == null && z == null && pitch == null && yaw == null)
            throw new IllegalStateException("A TeleportDemandPacket must have at least one of world, x, y, z, pitch, or yaw defined.");

        Packet.Builder.PrepareForSending packet = Packet.New()
                .identification(Packet.Type.from("RCM_DYNAMIC_TELEPORT", "TP"))
                .parameter("p", playerToTeleport.toString());

        if(world != null) packet.parameter("w", new Parameter(world));
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
                        packet.world().orElse(null),
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
