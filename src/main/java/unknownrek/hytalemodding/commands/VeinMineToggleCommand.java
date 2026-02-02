package unknownrek.hytalemodding.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import unknownrek.hytalemodding.Veinmine;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;

import javax.annotation.Nonnull;

/*
 * Was useful for testing and mucking around - didn't have a place in final implementation though
 */
public class VeinMineToggleCommand extends AbstractPlayerCommand {

    public static final String NAME = "veinmine";
    public static final String DESCRIPTION =  "allows enabling and disabling veinmine ability";
    public enum Actions {
        ENABLE, DISABLE, TOGGLE
    }

    private final RequiredArg<Actions> actionArg;

    public VeinMineToggleCommand(Veinmine plugin) {
        super(NAME, DESCRIPTION);
        if(plugin.getVeinmineConfig().get().getLockedBehindPerms()) {
            requirePermission(plugin.PERM);
        }

        this.actionArg = withRequiredArg("action", "enable/disable/toggle", ArgTypes.forEnum("action", Actions.class));
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        Actions action = this.actionArg.get(commandContext);

        var comp = store.getComponent(ref, VeinmineAbleComponent.getComponentType());

        switch(action) {
            case ENABLE -> {
                if(comp == null) {
                    addComp(store, ref);
                }
            }
            case DISABLE -> {
                if(comp != null) {
                    removeComp(store, ref);
                }
            }
            case TOGGLE -> {
                if(comp == null) {
                    addComp(store, ref);
                }
                if(comp != null) {
                    removeComp(store, ref);
                }
            }
        }

    }

    public void addComp(Store<EntityStore> store, Ref<EntityStore> ref) {
        store.addComponent(ref, VeinmineAbleComponent.getComponentType(), new VeinmineAbleComponent());
    }

    public void removeComp(Store<EntityStore> store, Ref<EntityStore> ref) {
        store.removeComponent(ref, VeinmineAbleComponent.getComponentType());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}
