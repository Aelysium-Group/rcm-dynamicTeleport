package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpr;

import group.aelysium.declarative_yaml.DeclarativeYAML;
import group.aelysium.declarative_yaml.annotations.Comment;
import group.aelysium.declarative_yaml.annotations.Config;
import group.aelysium.declarative_yaml.annotations.Namespace;
import group.aelysium.declarative_yaml.lib.Printer;
import group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa.TPAProvider;
import group.aelysium.rustyconnector.proxy.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Namespace("rustyconnector-modules")
@Config("/rcm-friend/config.yml")
@Comment({
        "############################################################",
        "#||||||||||||||||||||||||||||||||||||||||||||||||||||||||||#",
        "#                           TPA                            #",
        "#                                                          #",
        "#               ---------------------------                #",
        "# | Let players teleport to eachother between servers      #",
        "# | inside a family!                                       #",
        "# | If player1 is in one server and player2 is in another  #",
        "# | server; player1 will connect to the new server and     #",
        "# | then teleport to player2's coordinates.                #",
        "#                                                          #",
        "# | If both players are in the same server, player1 will   #",
        "# | just automatically teleport to player1.                #",
        "#                                                          #",
        "#   NOTE: This command is player only!                     #",
        "#                                                          #",
        "#               ----------------------------               #",
        "#                        Permission:                       #",
        "#                rustyconnector.command.tpa                #",
        "#               ----------------------------               #",
        "#                          Usage:                          #",
        "#              /tpa <target players username>              #",
        "#               ----------------------------               #",
        "#                                                          #",
        "# | You can also make it so that specific players do not   #",
        "# | send a request when using /tpa and instead directly    #",
        "# | teleport to the target player using a permission.      #",
        "#                                                          #",
        "#               ----------------------------               #",
        "#                 Bypass Request Permission:               #",
        "#         rustyconnector.command.tpa.bypassRequest         #",
        "#               ----------------------------               #",
        "#                                                          #",
        "#||||||||||||||||||||||||||||||||||||||||||||||||||||||||||#",
        "############################################################"
})
public class TPRConfig {
    public boolean enabled = false;
    public Player.Connection.Power teleportStrength = Player.Connection.Power.MODERATE;
    public String command = "tpr";
    public List<TeleportPlatform> platforms = List.of(
            new TeleportPlatform("survival", false, 0L, 0L, 50000L, Type.SQUARE),
            new TeleportPlatform("survival", true, 1000L, -1000L, 255L, Type.CIRCLE)
    );

    public TPRProvider.Tinder tinder() {
        return new TPRProvider.Tinder();
    }

    public static TPRConfig New() {
        return DeclarativeYAML.From(TPRConfig.class, new Printer());
    }

    public record TeleportPlatform(
            @NotNull String family,
            @NotNull Boolean randomizeServer,
            @NotNull Long x,
            @NotNull Long z,
            @NotNull Long width,
            @NotNull Type type
    ) {}

    public enum Type {
        SQUARE,
        CIRCLE
    }
}