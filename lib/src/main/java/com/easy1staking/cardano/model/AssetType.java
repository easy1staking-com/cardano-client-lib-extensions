package com.easy1staking.cardano.model;

import com.bloxbean.cardano.client.util.HexUtil;
import com.easy1staking.json.AssetTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = AssetTypeSerializer.class)
public record AssetType(String policyId, String assetName) {

    private static final Integer POLICY_ID_LENGTH = 56;

    public static final String LOVELACE = "lovelace";

    private static final AssetType Ada = new AssetType("", "");

    public String toUnit() {
        return policyId + assetName;
    }

    public boolean isAda() {
        return this.equals(Ada);
    }

    public String unsafeHumanAssetName() {
        return new String(HexUtil.decodeHexString(assetName));
    }

    public static AssetType fromUnit(String unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if (unit.equalsIgnoreCase(LOVELACE)) {
            return Ada;
        } else if (unit.isBlank()) {
            return Ada;
        }

        var sanitizedUnit = unit.replaceAll("\\.", "");
        if (sanitizedUnit.length() < POLICY_ID_LENGTH) {
            throw new IllegalArgumentException("Invalid asset unit length: " + unit.length());
        } else {
            return new AssetType(sanitizedUnit.substring(0, 56), sanitizedUnit.substring(56));
        }
    }

    /**
     * Returns the singleton instance representing Ada (lovelace).
     */
    public static AssetType ada() {
        return Ada;
    }

    public byte[] getPlutusDataPolicyId() {
        if (this.equals(Ada)) {
            return new byte[0];
        } else {
            return HexUtil.decodeHexString(policyId);
        }
    }

    public byte[] getPlutusDataAssetName() {
        if (this.equals(Ada)) {
            return new byte[0];
        } else {
            return HexUtil.decodeHexString(assetName);
        }
    }

    public static AssetType fromPlutusData(byte[] policyId, byte[] assetName) {
        if (policyId.length == 0) {
            return Ada;
        } else {
            return new AssetType(HexUtil.encodeHexString(policyId), HexUtil.encodeHexString(assetName));
        }
    }

}
