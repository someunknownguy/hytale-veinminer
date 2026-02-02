package unknownrek.hytalemodding.utils;

import com.hypixel.hytale.math.vector.Vector3i;
import joptsimple.internal.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static unknownrek.hytalemodding.utils.BlockUtils.getSurroundingBlocks;


public class BlockUtilsTest {

    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6})
    void testGetSurroundingBlockHasExactly1Null(int radius) {
        var center = new Vector3i(0,0,0);

        var res = getSurroundingBlocks(center, radius);
        var countNulls = 0;
        for (Vector3i re : res) {
            if (re == null) {
                if (++countNulls > 1) {
                    fail("Found a null block coord");
                }
            }
        }
    }

    @Test
    void testGetSurroundingBlockReturnsExpectedValues() {
        var center = new Vector3i(0,0,0);

        var expected = new ArrayList<>(List.of(
                new Vector3i(-1, -1, 0), new Vector3i(-1, 0, 0), new Vector3i(-1, 1, 0),
                new Vector3i(-1, -1, -1), new Vector3i(-1, 0, -1), new Vector3i(-1, 1, -1),
                new Vector3i(-1, -1, 1), new Vector3i(-1, 0, 1), new Vector3i(-1, 1, 1),
                new Vector3i(0, -1, 0), /*new Vector3i(0, 0, 0),*/ new Vector3i(0, 1, 0),
                new Vector3i(0, -1, -1), new Vector3i(0, 0, -1), new Vector3i(0, 1, -1),
                new Vector3i(0, -1, 1), new Vector3i(0, 0, 1), new Vector3i(0, 1, 1),
                new Vector3i(1, -1, 0), new Vector3i(1, 0, 0), new Vector3i(1, 1, 0),
                new Vector3i(1, -1, -1), new Vector3i(1, 0, -1), new Vector3i(1, 1, -1),
                new Vector3i(1, -1, 1), new Vector3i(1, 0, 1), new Vector3i(1, 1, 1)
        ));

        var res = getSurroundingBlocks(center, 1);

        for(var block : res) {
            var expBlock = expected.stream().filter(b -> b.equals(block)).findFirst().orElse(null);
            if(expBlock == null && block != null) {
                fail("found block outside of bounds: " + block);
            }
            expected.remove(expBlock);
        }
        if(!expected.isEmpty()) {
            fail("missing expected blocks: " + Strings.join(expected.stream().map(Vector3i::toString)
                    .toArray(String[]::new), ","));
        }

    }
}
