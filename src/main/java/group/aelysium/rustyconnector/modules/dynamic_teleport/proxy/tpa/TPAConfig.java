package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.tpa;

import group.aelysium.rustyconnector.proxy.player.Player;
import group.aelysium.rustyconnector.shaded.group.aelysium.declarative_yaml.DeclarativeYAML;
import group.aelysium.rustyconnector.shaded.group.aelysium.declarative_yaml.annotations.*;

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
public class TPAConfig {
    public boolean enabled = false;
    public boolean friendsOnly = false;
    public List<List<String>> enabledFamilies = List.of(List.of("lobby"),List.of("pvp1", "pvp2"));
    public Player.Connection.Power teleportStrength = Player.Connection.Power.MODERATE;

    public static TPAConfig New() {
        return DeclarativeYAML.From(TPAConfig.class);
    }
}