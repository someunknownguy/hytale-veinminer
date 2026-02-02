package unknownrek.hytalemodding.components;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class VeinmineAbleComponent implements Component<EntityStore> {

    private static final float COOLDOWN = 0.5f;

    private float currentCooldown = 0f;
    private boolean isEnabled = false;

    private static ComponentType<EntityStore, VeinmineAbleComponent> type;

    public static ComponentType<EntityStore, VeinmineAbleComponent> getComponentType() {
        return type;
    }

    public static void setComponentType(ComponentType<EntityStore, VeinmineAbleComponent> type1) {
        type = type1;
    }

    public static final BuilderCodec<VeinmineAbleComponent> CODEC = BuilderCodec
            .builder(VeinmineAbleComponent.class, VeinmineAbleComponent::new)
            .build();

    public VeinmineAbleComponent() {}

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new VeinmineAbleComponent();
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void decrementCooldown(float byAmount) {
        currentCooldown = Math.clamp(getCurrentCooldown() - byAmount, 0.0f, COOLDOWN);
    }

    public void applyCooldown() {
        currentCooldown = COOLDOWN;
    }
    public boolean isOnCooldown() {
        return currentCooldown > 0.0f;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }
}
