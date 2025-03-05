package group.aelysium.rustyconnector.modules.dynamic_teleport.proxy.warp;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.haze.HazeDatabase;
import group.aelysium.rustyconnector.common.modules.Module;
import group.aelysium.rustyconnector.proxy.family.Family;
import group.aelysium.rustyconnector.proxy.family.Server;
import group.aelysium.rustyconnector.proxy.player.Player;
import group.aelysium.rustyconnector.shaded.group.aelysium.ara.Flux;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.Database;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.lib.DataHolder;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.lib.Filterable;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.lib.Type;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.query.CreateRequest;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.query.DeleteRequest;
import group.aelysium.rustyconnector.shaded.group.aelysium.haze.query.ReadRequest;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WarpProvider implements Module {
    protected static final String WARP_TABLE = "dynamicTeleport_warps";

    protected final WarpConfig config;
    protected final Flux<HazeDatabase> database;

    public WarpProvider(
            @NotNull WarpConfig config
    ) throws Exception {
        this.config = config;

        this.database = RC.P.Haze().fetchDatabase(this.config.database);
        if(this.database == null) throw new NoSuchElementException("No database exists on the haze provider with the name '"+this.config.database+"'.");
        Database db = this.database.get(1, TimeUnit.MINUTES);

        if(db.doesDataHolderExist(WARP_TABLE)) return;

        DataHolder table = new DataHolder(WARP_TABLE);
        Map<String, Type> columns = Map.of(
                "player_uuid", Type.STRING(36).nullable(false),
                "name", Type.STRING(16).nullable(false),
                "family_id", Type.STRING(16).nullable(false),
                "server_id", Type.STRING(64).nullable(false),
                // Use whatever the largest size for an integer field is because minecraft worlds are massive
                "x", Type.INTEGER(1000).nullable(false),
                "y", Type.INTEGER(1000).nullable(false),
                "z", Type.INTEGER(1000).nullable(false),
                "created_at", Type.DATETIME().nullable(false)
        );
        columns.forEach(table::addKey);
        db.createDataHolder(table);
    }

    public @NotNull Set<WarpDTO> fetchWarps(@NotNull UUID player) throws Exception {
        Database db = this.database.get(5, TimeUnit.SECONDS);
        ReadRequest sp = db.newReadRequest(WARP_TABLE);
        sp.filters().filterBy("player_uuid", new Filterable.FilterValue(player, Filterable.Qualifier.EQUALS));
        return sp.execute(WarpDTO.class);
    }

    public @NotNull Optional<WarpDTO> fetchWarp(@NotNull UUID player, @NotNull String name) throws Exception {
        Database db = this.database.get(5, TimeUnit.SECONDS);
        ReadRequest sp = db.newReadRequest(WARP_TABLE);
        sp.filters().filterBy("player_uuid", new Filterable.FilterValue(player, Filterable.Qualifier.EQUALS));
        sp.filters().filterBy("name", new Filterable.FilterValue(name, Filterable.Qualifier.EQUALS));
        return sp.execute(WarpDTO.class).stream().findAny();
    }

    public void deleteWarp(@NotNull UUID player, @NotNull String name) throws Exception {
        Database db = this.database.get(5, TimeUnit.SECONDS);
        DeleteRequest sp = db.newDeleteRequest(WARP_TABLE);
        sp.filters().filterBy("player_uuid", new Filterable.FilterValue(player, Filterable.Qualifier.EQUALS));
        sp.filters().filterBy("name", new Filterable.FilterValue(name, Filterable.Qualifier.EQUALS));
        sp.execute();
    }
    public long createWarp(@NotNull WarpDTO warp) throws Exception {
        Database db = this.database.get(5, TimeUnit.SECONDS);
        CreateRequest sp = db.newCreateRequest(WARP_TABLE);
        sp.parameter("player_uuid", warp.player().toString());
        sp.parameter("name", warp.name());
        sp.parameter("server_id", warp.server());
        sp.parameter("family_id", warp.family());
        sp.parameter("x", warp.x());
        sp.parameter("y", warp.y());
        sp.parameter("z", warp.z());
        sp.parameter("created_at", LocalDateTime.now());
        return sp.execute();
    }

    @Override
    public @Nullable Component details() {
        return null;
    }

    @Override
    public void close() throws Exception {
    }

    public record WarpDTO(
            @NotNull UUID player,
            @NotNull String name,
            @NotNull String server,
            @NotNull String family,
            long x,
            long y,
            long z,
            LocalDateTime createdAt
    ) {
        public @NotNull Optional<Player> resolvePlayer() {
            try {
                return RC.P.Player(this.player);
            } catch (Exception ignore) {}
            return Optional.empty();
        }
        public @NotNull Optional<? extends Server> resolveServer() {
            try {
                return RC.P.Server(this.server);
            } catch (Exception ignore) {}
            return Optional.empty();
        }
        public @NotNull Optional<? extends Family> resolveFamily() {
            try {
                return RC.P.Family(this.family);
            } catch (Exception ignore) {}
            return Optional.empty();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            WarpDTO warpDTO = (WarpDTO) o;
            return Objects.equals(player, warpDTO.player) && Objects.equals(name, warpDTO.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, name);
        }
    }
}
