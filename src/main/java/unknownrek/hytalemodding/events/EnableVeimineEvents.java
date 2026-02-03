package unknownrek.hytalemodding.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.permissions.PlayerPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import unknownrek.hytalemodding.Veinmine;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;

import java.util.Set;
import java.util.UUID;

public class EnableVeimineEvents {

    private final Veinmine plugin;

    public EnableVeimineEvents(Veinmine plugin){
        this.plugin = plugin;
    }

    /*
     * For retroactively giving the component to player, i.e if they get given perms offline or before plugin is added
     * Or if perms are disabled, when a player first joins, need to give them the component
     *
     * Similarly, if player lost perms while offline, remove it
     */
    public void onPlayerReady(PlayerReadyEvent event) {
        if(event.getPlayer().getWorld() == null) {
            Veinmine.LOGGER.atWarning().log("onPlayerReadyEvent fired without player existing in world yet");
            return;
        }

        if(hasPerm(event.getPlayer().getWorld(), event.getPlayerRef())) {
            giveComp(event.getPlayer().getWorld(), event.getPlayerRef());
        } else {
            removeComp(event.getPlayer().getWorld(), event.getPlayerRef());
        }
    }

    public void onPermissionAdded(PlayerPermissionChangeEvent.PermissionsAdded event) {
        if(!permEnabled()) {
            return;
        }

        if(checkPermissionInGroup(event.getAddedPermissions())){
            Veinmine.LOGGER.atFine().log("Found player " + event.getPlayerUuid() + " was given veinmine perm, attempting to apply component");
            PlayerRef playerRef = Universe.get().getPlayer(event.getPlayerUuid());
            if(playerRef == null || !playerRef.isValid() || playerRef.getReference() == null
                    || !playerRef.getReference().isValid()) {
                Veinmine.LOGGER.atWarning().log("Couldn't apply component to " + event.getPlayerUuid() + " because playerRef is invalid - assuming player is offline");
                return;
            }

            giveComp(playerRef.getReference().getStore().getExternalData().getWorld(), playerRef.getReference());
        }
    }

    public void onPermissionRemoved(PlayerPermissionChangeEvent.PermissionsRemoved event) {
        if(!permEnabled()) {
            return;
        }
        if(checkPermissionInGroup(event.getRemovedPermissions())){
            Veinmine.LOGGER.atFine().log("Found player " + event.getPlayerUuid() + " has veinmine perm removed, attempting to remove component");
            PlayerRef playerRef = Universe.get().getPlayer(event.getPlayerUuid());
            if(playerRef == null || !playerRef.isValid() || playerRef.getReference() == null
                    || !playerRef.getReference().isValid()) {
                Veinmine.LOGGER.atWarning().log("Couldn't remove component from " + event.getPlayerUuid() + " because playerRef is invalid - assuming player is offline");
                return;
            }

            removeComp(playerRef.getReference().getStore().getExternalData().getWorld(), playerRef.getReference());
        }

    }

    public void onGroupAdded(PlayerPermissionChangeEvent.GroupAdded event) {

        if(!permEnabled()) {
            return;
        }
        var permsInGroup = PermissionsModule.get().getFirstPermissionProvider().getGroupPermissions(event.getGroupName());
        if(checkPermissionInGroup(permsInGroup)){
            Veinmine.LOGGER.atFine().log("Found player " + event.getPlayerUuid() + " was given veinmine perm, attempting to apply component");
            PlayerRef playerRef = Universe.get().getPlayer(event.getPlayerUuid());
            if(playerRef == null || !playerRef.isValid() || playerRef.getReference() == null
                    || !playerRef.getReference().isValid()) {
                Veinmine.LOGGER.atWarning().log("Couldn't apply component to " + event.getPlayerUuid() + " because playerRef is invalid - assuming player is offline");
                return;
            }

            giveComp(playerRef.getReference().getStore().getExternalData().getWorld(), playerRef.getReference());
        }

    }

    public void onGroupRemoved(PlayerPermissionChangeEvent.GroupRemoved event) {
        if(!permEnabled()) {
            return;
        }
        var permsInGroup = PermissionsModule.get().getFirstPermissionProvider().getGroupPermissions(event.getGroupName());
        if(checkPermissionInGroup(permsInGroup)){
            Veinmine.LOGGER.atFine().log("Found player " + event.getPlayerUuid() + " had veinmine perm removed, attempting to apply component");
            PlayerRef playerRef = Universe.get().getPlayer(event.getPlayerUuid());
            if(playerRef == null || !playerRef.isValid() || playerRef.getReference() == null
                    || !playerRef.getReference().isValid()) {
                Veinmine.LOGGER.atWarning().log("Couldn't remove component from " + event.getPlayerUuid() + " because playerRef is invalid - assuming player is offline");
                return;
            }

            removeComp(playerRef.getReference().getStore().getExternalData().getWorld(), playerRef.getReference());
        }
    }

    // Helpers

    private void giveComp(World world, Ref<EntityStore> ref) {
        var store = world.getEntityStore().getStore();

            world.execute(() -> {
                if (store.getComponent(ref, VeinmineAbleComponent.getComponentType()) == null) {
                    store.addComponent(ref, VeinmineAbleComponent.getComponentType());
                }
            });
    }

    private void removeComp(World world, Ref<EntityStore> ref) {
        world.execute(() ->  world.getEntityStore().getStore()
                .removeComponentIfExists(ref, VeinmineAbleComponent.getComponentType()));
    }

    private boolean hasPerm(World world, Ref<EntityStore> ref) {
        var playerRef = world.getEntityStore().getStore().getComponent(ref, PlayerRef.getComponentType());

        return hasPerm(playerRef);
    }

    private boolean hasPerm(PlayerRef playerRef) {
        return playerRef != null && playerRef.isValid() && hasPerm(playerRef.getUuid());
    }

    private boolean hasPerm(UUID playerUUID){
        return !permEnabled()
                || PermissionsModule.get().hasPermission(playerUUID, plugin.PERM);
    }

    private boolean permEnabled() {
        return plugin.getVeinmineConfig().get().getLockedBehindPerms();
    }

    private boolean checkPermissionInGroup(Set<String> nodes) {
        return Boolean.TRUE.equals(PermissionsModule.hasPermission(nodes, plugin.PERM));
    }
}
