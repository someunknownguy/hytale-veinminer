package unknownrek.hytalemodding.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.util.WildcardMatch;

import java.util.function.Predicate;

public class VeinmineConfig {

    public static final BuilderCodec<VeinmineConfig> CODEC = BuilderCodec.builder(VeinmineConfig.class, VeinmineConfig::new)
            .append(new KeyedCodec<>("MaxBlocksVeined", Codec.INTEGER),
                    VeinmineConfig::setMaxBlocksVeined,
                    VeinmineConfig::getMaxBlocksVeined).add()
            .append(new KeyedCodec<>("LockedBehindPermission", Codec.BOOLEAN),
                    VeinmineConfig::setLockedBehindPerms,
                    VeinmineConfig::getLockedBehindPerms).add()
            .append(new KeyedCodec<>("WhitelistHeldItemIds", Codec.STRING_ARRAY),
                    VeinmineConfig::setWhitelistItemIds,
                    VeinmineConfig::getWhitelistItemIds).add()
            .append(new KeyedCodec<>("WhitelistBlockIds", Codec.STRING_ARRAY),
                    VeinmineConfig::setWhitelistBlockIds,
                    VeinmineConfig::getWhitelistBlockIds).add()
            .build();

    //Default for potentially nullable cases
    private static final Boolean LOCKED_BEHIND_PERMS = Boolean.FALSE;
    private static final String[] WHITELIST_ITEMS = new String[] {"Tool_Pickaxe_*"};
    private static final String[] WHITELIST_BLOCKS = new String[] {"Ore_*"};

    //primitive types can have default set here
    private int maxBlocksVeined = 32;

    private Boolean lockedBehindPerms;
    private String[] whitelistItemIds;
    private String[] whitelistBlockIds;

    public int getMaxBlocksVeined() {
        return maxBlocksVeined > 0 ? Math.abs(maxBlocksVeined) : maxBlocksVeined;
    }

    public void setMaxBlocksVeined(int maxBlocksVeined) {
        this.maxBlocksVeined = maxBlocksVeined;
    }

    public Boolean getLockedBehindPerms() {
        return lockedBehindPerms == null ? LOCKED_BEHIND_PERMS : lockedBehindPerms;
    }

    public void setLockedBehindPerms(Boolean lockedBehindPerms) {
        this.lockedBehindPerms = lockedBehindPerms;
    }

    public Predicate<String> getWhitelistItemsAsPredicate() {
        return buildOrPredicateFrom(getWhitelistItemIds());
    }
    public Predicate<String> getWhitelistBlocksAsPredicate() {
        return buildOrPredicateFrom(getWhitelistBlockIds());
    }


    public String[] getWhitelistItemIds() {
        return whitelistItemIds == null ? WHITELIST_ITEMS : whitelistItemIds;
    }

    public void setWhitelistItemIds(String[] whitelistItemIds) {
        this.whitelistItemIds = whitelistItemIds;
    }

    public String[] getWhitelistBlockIds() {
        return whitelistBlockIds == null ? WHITELIST_BLOCKS : whitelistBlockIds;
    }

    public void setWhitelistBlockIds(String[] whitelistBlockIds) {
        this.whitelistBlockIds = whitelistBlockIds;
    }

    /**
     * Builds a Predicate that tests a String against any of the values using WildCardMatch.test logic
     * and OR strategy (i.e: "Tool_Pickaxe_Crude" will match ["Tool_Hatchet_*","Tool_Shovel_*","Tool_Pickaxe_*"]
     * on the 3rd value provided
     * @param values list of WildCardMatch style values to create predicate from
     * @return Predicate instance
     */
    private Predicate<String> buildOrPredicateFrom(String[] values) {
        Predicate<String> pred = (_) ->  false;
        for (String val : values) {
            pred = pred.or(s -> WildcardMatch.test(s, val, true));
        }
        return pred;
    }
}
