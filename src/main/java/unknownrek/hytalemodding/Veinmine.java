package unknownrek.hytalemodding;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.permissions.PlayerPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import unknownrek.hytalemodding.commands.VeinMineToggleCommand;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;
import unknownrek.hytalemodding.config.VeinmineConfig;
import unknownrek.hytalemodding.events.EnableVeimineEvents;
import unknownrek.hytalemodding.events.VeinmineCooldownSystem;
import unknownrek.hytalemodding.events.VeinmineSystem;

import javax.annotation.Nonnull;

public class Veinmine extends JavaPlugin {

    private final Config<VeinmineConfig> config = this.withConfig("VeinmineConfig", VeinmineConfig.CODEC);

    public String PERM;

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Veinmine(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Loading Veinmine");
        PERM = this.getBasePermission() + ".allowed";
        config.save();

        setupCommands();
        setupEvents();
        setupEcs();

        LOGGER.atInfo().log("Finished loading Veinmine");
    }

    private void setupCommands() {
        var commandReg = this.getCommandRegistry();
        commandReg.registerCommand(new VeinMineToggleCommand(this));
    }

    private void setupEvents() {
        var eventReg = this.getEventRegistry();

        EnableVeimineEvents eventHandlers = new EnableVeimineEvents(this);

        eventReg.registerGlobal(PlayerReadyEvent.class, eventHandlers::onPlayerReady);
        eventReg.registerGlobal(PlayerPermissionChangeEvent.PermissionsAdded.class, eventHandlers::onPermissionAdded);
        eventReg.registerGlobal(PlayerPermissionChangeEvent.PermissionsRemoved.class, eventHandlers::onPermissionRemoved);
        // These appear to be dead events at the moment...
//        eventReg.registerGlobal(PlayerPermissionChangeEvent.GroupAdded.class, eventHandlers::onGroupAdded);
//        eventReg.registerGlobal(PlayerPermissionChangeEvent.GroupRemoved.class, eventHandlers::onGroupRemoved);
    }

    private void setupEcs() {
        var ecsEventReg = this.getEntityStoreRegistry();

        var veinMineComp = ecsEventReg.registerComponent(VeinmineAbleComponent.class, VeinmineAbleComponent.class.getName(), VeinmineAbleComponent.CODEC);
        VeinmineAbleComponent.setComponentType(veinMineComp);

        ecsEventReg.registerSystem(new VeinmineSystem(this));
        ecsEventReg.registerSystem(new VeinmineCooldownSystem());
    }


    public Config<VeinmineConfig> getVeinmineConfig() {
        return config;
    }
}