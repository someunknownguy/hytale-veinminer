package unknownrek.hytalemodding.events;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import unknownrek.hytalemodding.Veinmine;
import unknownrek.hytalemodding.components.VeinmineAbleComponent;
import unknownrek.hytalemodding.config.VeinmineConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;

import static unknownrek.hytalemodding.Veinmine.LOGGER;
import static unknownrek.hytalemodding.utils.BlockUtils.getBlockBreakInfo;
import static unknownrek.hytalemodding.utils.BlockUtils.getSurroundingBlocks;

public class VeinmineSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final VeinmineConfig config;

    public VeinmineSystem(Veinmine plugin) {
        super(BreakBlockEvent.class);
        this.config = plugin.getVeinmineConfig().get();
    }

    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                       @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull BreakBlockEvent breakBlockEvent) {
        LOGGER.atFine().log("Handling BreakBlockEvent");
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(i);
        VeinmineAbleComponent comp = store.getComponent(playerRef, VeinmineAbleComponent.getComponentType());
        if(comp == null || comp.isOnCooldown() || !comp.isEnabled()) {
            return;
        }

        var startBlockType = breakBlockEvent.getBlockType();

        var heldItem = breakBlockEvent.getItemInHand();
        if (heldItem == null || heldItem.equals(ItemStack.EMPTY) || startBlockType.equals(BlockType.EMPTY)) {
            return;
        }

        if(!config.getWhitelistItemsAsPredicate().test(heldItem.getItemId())) {
            LOGGER.atFiner().log(heldItem.getItemId() + " does not match item in whitelist");
            return;
        }

        if(!config.getWhitelistBlocksAsPredicate().test(startBlockType.getId())) {
            LOGGER.atFinest().log(startBlockType.getId() + "does not match block in whitelist");
            return;
        }

        var world = commandBuffer.getExternalData().getWorld();
        var startPos = breakBlockEvent.getTargetBlock();

        var maxBlocks = config.getMaxBlocksVeined();
        Collection<Vector3i> blocksToBreak = new ArrayList<>();
        List<Vector3i> buffer = new ArrayList<>(maxBlocks),
                recent = new ArrayList<>(maxBlocks);
        recent.add(startPos);

        while (blocksToBreak.size() < maxBlocks) {
            recentSearch:
            for(Vector3i b : recent) {
                for (Vector3i surroundingBlock : getSurroundingBlocks(b, 1)) {
                    if (surroundingBlock == null || blocksToBreak.contains(surroundingBlock) || buffer.contains(surroundingBlock)) {
                        continue;
                    }

                    var block = world.getBlockType(surroundingBlock);

                    if (block != null && Objects.equals(startBlockType.getId(), block.getId())) {
                        if (blocksToBreak.size() + buffer.size() >= maxBlocks) {
                            break recentSearch;
                        }

                        buffer.add(surroundingBlock);
                    }
                }
            }
            if(buffer.isEmpty()) {
                break;
            }

            recent.clear();
            recent.addAll(buffer);
            blocksToBreak.addAll(buffer);
            buffer.clear();
        }

        var chunkStore = world.getChunkStore();

        var blockInfo = getBlockBreakInfo(startBlockType);

        world.execute(() -> {
            comp.applyCooldown();

            blocksToBreak.forEach(b ->  {
                var blockChunk = chunkStore.getChunkReference(ChunkUtil.indexChunkFromBlock(b.x, b.z));
                if(blockChunk == null || !blockChunk.isValid()) {
                    LOGGER.atWarning().log("You're fetching the blockchunk wrong. fix yer shit unknown!");
                    return;
                }

                var worldChunk = chunkStore.getStore().getComponent(blockChunk, WorldChunk.getComponentType());
                if(worldChunk == null || !worldChunk.getReference().isValid()) {
                    LOGGER.atWarning().log("Invalid worldChunk. Just like yo momma");
                    return;
                }
                BlockHarvestUtils.performBlockBreak(world, b, startBlockType, breakBlockEvent.getItemInHand(), blockInfo.quantity(), blockInfo.itemId(), blockInfo.dropListId(), 0, playerRef, blockChunk, store, chunkStore.getStore());
            });
        });

        LOGGER.atFine().log("Finished handling BreakBlockEvent");
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(VeinmineAbleComponent.getComponentType());
    }
}
