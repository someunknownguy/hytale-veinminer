package unknownrek.hytalemodding.utils;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.*;

public class BlockUtils {

    public record BlockBreakInfo(String itemId, String dropListId, int quantity) {}

    public static Vector3i[] getSurroundingBlocks(Vector3i center, int radius) {
        if(radius == 0) {
            return new Vector3i[] {center};
        }

        int rad = Math.abs(radius);
        int length = (1+2*rad);

        var output = new Vector3i[Math.powExact(length, 3)];

        for(int x = 0; x < length; x++) {
            for(int y = 0; y < length; y++) {
                for(int z = 0; z < length; z++) {
                    if(x == rad && y == rad && z == rad) {
                        continue;
                    }
                    output[x*length*length + y*length + z] = new Vector3i(center).add(x-rad,y-rad,z-rad);
                }
            }
        }

        return output;
    }

    public static BlockBreakInfo getBlockBreakInfo(BlockType blockType) {
        String itemId = null;
        String dropListId = null;
        int quantity = 0;
        BlockGathering blockGathering = blockType.getGathering();
        if (blockGathering != null) {
            PhysicsDropType physics = blockGathering.getPhysics();
            BlockBreakingDropType breaking = blockGathering.getBreaking();
            SoftBlockDropType soft = blockGathering.getSoft();
            HarvestingDropType harvest = blockGathering.getHarvest();
            if (physics != null) {
                itemId = physics.getItemId();
                dropListId = physics.getDropListId();
            } else if (breaking != null) {
                quantity = breaking.getQuantity();
                itemId = breaking.getItemId();
                dropListId = breaking.getDropListId();
            } else if (soft != null) {
                itemId = soft.getItemId();
                dropListId = soft.getDropListId();
            } else if (harvest != null) {
                itemId = harvest.getItemId();
                dropListId = harvest.getDropListId();
            }
        }
        return new BlockBreakInfo(itemId, dropListId, quantity);
    }
}
