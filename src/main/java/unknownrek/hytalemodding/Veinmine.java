package unknownrek.hytalemodding;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import unknownrek.hytalemodding.commands.VeinMineToggleCommand;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;
import unknownrek.hytalemodding.config.VeinmineConfig;
import unknownrek.hytalemodding.events.EnableVeinmineOnCrouch;
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
        config.save();
        var commandReg = this.getCommandRegistry();
        var eventReg = this.getEventRegistry();
        var ecsEventReg = this.getEntityStoreRegistry();

        commandReg.registerCommand(new VeinMineToggleCommand(this));
        var veinMineComp = ecsEventReg.registerComponent(VeinmineAbleComponent.class, VeinmineAbleComponent.class.getName(), VeinmineAbleComponent.CODEC);
        VeinmineAbleComponent.setComponentType(veinMineComp);

        ecsEventReg.registerSystem(new VeinmineSystem(this));
        ecsEventReg.registerSystem(new EnableVeinmineOnCrouch(this));
        ecsEventReg.registerSystem(new VeinmineCooldownSystem());

        PERM = this.getBasePermission() + ".allowed";
        LOGGER.atInfo().log("Finished loading Veinmine");
    }


    public Config<VeinmineConfig> getVeinmineConfig() {
        return config;
    }
}