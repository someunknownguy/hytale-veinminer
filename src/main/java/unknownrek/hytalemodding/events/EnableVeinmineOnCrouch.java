package unknownrek.hytalemodding.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import unknownrek.hytalemodding.Veinmine;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnableVeinmineOnCrouch extends DelayedEntitySystem<EntityStore> {

    private final Veinmine plugin;

    public EnableVeinmineOnCrouch(Veinmine plugin) {
        super(0.3f);
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType(), Player.getComponentType(), MovementStatesComponent.getComponentType());
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        if(!ref.isValid() || player == null || player.isWaitingForClientReady() || playerRef == null) {
            return;
        }
        var world = commandBuffer.getExternalData().getWorld();
        world.execute(() -> {
            var comp = store.getComponent(ref, VeinmineAbleComponent.getComponentType());
            if(plugin.getVeinmineConfig().get().getLockedBehindPerms() &&
                    !PermissionsModule.get().hasPermission(playerRef.getUuid(), plugin.PERM)) {
                if(comp != null) {
                    store.removeComponentIfExists(ref, VeinmineAbleComponent.getComponentType());
                    return;
                }
            }

            var movementStatesComponent = store.getComponent(ref, MovementStatesComponent.getComponentType());
            if(movementStatesComponent == null || movementStatesComponent.getMovementStates() == null) {
                return;
            }

            if(isCrouching(movementStatesComponent.getMovementStates())) {
                if(comp == null) {
                    store.addComponent(ref, VeinmineAbleComponent.getComponentType()).enable();
                    return;
                }
                comp.enable();
            } else if(comp != null) {
                comp.disable();
            }
        });
    }

    private boolean isCrouching(MovementStates states) {
        return states.crouching || states.forcedCrouching;
    }

}
