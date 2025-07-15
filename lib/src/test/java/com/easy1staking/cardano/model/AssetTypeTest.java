package com.easy1staking.cardano.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssetTypeTest {

    @Test
    void emptyStringIsAda() {
        AssetType assetType = AssetType.fromUnit("");
        assertTrue(assetType.isAda());
    }

    @Test
    void lovelaceIsAda() {
        AssetType assetType = AssetType.fromUnit("lovelace");
        assertTrue(assetType.isAda());
    }

    @Test
    void nonAdaUnitCreatesAssetType() {
        String policyId = "a".repeat(56);
        String assetName = "626c7565"; // "blue" in hex
        AssetType assetType = AssetType.fromUnit(policyId + assetName);
        assertFalse(assetType.isAda());
        assertEquals(policyId, assetType.policyId());
        assertEquals(assetName, assetType.assetName());
    }

    @Test
    void toUnitReturnsConcatenatedString() {
        AssetType assetType = new AssetType("policy", "asset");
        assertEquals("policyasset", assetType.toUnit());
    }

    @Test
    void unsafeHumanAssetNameDecodesHex() {
        AssetType assetType = new AssetType("policy", "626c7565"); // "blue"
        assertEquals("blue", assetType.unsafeHumanAssetName());
    }

    @Test
    void fromUnitThrowsOnShortUnit() {
        String shortUnit = "a".repeat(10);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> AssetType.fromUnit(shortUnit));
        assertTrue(ex.getMessage().contains("Invalid asset unit length"));
    }

    @Test
    void adaStaticReturnsAda() {
        AssetType ada = AssetType.ada();
        assertTrue(ada.isAda());
    }

    @Test
    void getPlutusDataPolicyIdReturnsEmptyForAda() {
        AssetType ada = AssetType.ada();
        assertArrayEquals(new byte[0], ada.getPlutusDataPolicyId());
    }

    @Test
    void getPlutusDataAssetNameReturnsEmptyForAda() {
        AssetType ada = AssetType.ada();
        assertArrayEquals(new byte[0], ada.getPlutusDataAssetName());
    }

    @Test
    void getPlutusDataPolicyIdReturnsDecodedForNonAda() {
        String policyId = "616263"; // "abc" in hex
        AssetType assetType = new AssetType(policyId, "asset");
        assertArrayEquals(new byte[]{97, 98, 99}, assetType.getPlutusDataPolicyId());
    }

    @Test
    void getPlutusDataAssetNameReturnsDecodedForNonAda() {
        String assetName = "646566"; // "def" in hex
        AssetType assetType = new AssetType("policy", assetName);
        assertArrayEquals(new byte[]{100, 101, 102}, assetType.getPlutusDataAssetName());
    }

    @Test
    void fromPlutusDataReturnsAdaForEmptyPolicyId() {
        AssetType assetType = AssetType.fromPlutusData(new byte[0], new byte[]{1, 2, 3});
        assertTrue(assetType.isAda());
    }

    @Test
    void fromPlutusDataReturnsAssetTypeForNonEmptyPolicyId() {
        byte[] policyId = {1, 2, 3};
        byte[] assetName = {4, 5, 6};
        AssetType assetType = AssetType.fromPlutusData(policyId, assetName);
        assertEquals("010203", assetType.policyId());
        assertEquals("040506", assetType.assetName());
    }

    @Test
    void fromUnitThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> AssetType.fromUnit(null));
    }


}