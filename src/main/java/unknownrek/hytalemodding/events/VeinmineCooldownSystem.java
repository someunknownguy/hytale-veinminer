package unknownrek.hytalemodding.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VeinmineCooldownSystem extends DelayedEntitySystem<EntityStore> {
    public VeinmineCooldownSystem() {
        super(0.5f);
    }

    @Override
    public void tick(float v, int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        VeinmineAbleComponent comp = store.getComponent(ref, VeinmineAbleComponent.getComponentType());
        if(comp == null || !comp.isOnCooldown()) {
            return;
        }
        comp.decrementCooldown(v);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType(), Player.getComponentType(), VeinmineAbleComponent.getComponentType());
    }
}
