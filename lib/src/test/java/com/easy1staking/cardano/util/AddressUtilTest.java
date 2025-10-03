package com.easy1staking.cardano.util;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.address.Credential;
import com.bloxbean.cardano.client.crypto.Blake2bUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressUtilTest {

    // Create test accounts for generating valid addresses
    private static final Account ACCOUNT_1 = new Account();
    private static final Account ACCOUNT_2 = new Account();

    // Sample Shelley addresses for testing
    private static final String MAINNET_BASE_ADDRESS = ACCOUNT_1.baseAddress();
    private static final String MAINNET_BASE_ADDRESS_2 = ACCOUNT_2.baseAddress();
    private static final String MAINNET_ENTERPRISE_ADDRESS = ACCOUNT_1.enterpriseAddress();
    private static final String MAINNET_STAKE_ADDRESS = AddressProvider.getStakeAddress(ACCOUNT_1.getBaseAddress()).getAddress();

    // Byron address
    private static final String BYRON_ADDRESS = "Ae2tdPwUPEYxFtzxxN2w6shdxwuZeLEuD6EsbaN2rSsChrQc3xukakUJPvL";

    // Base address and stake address that share the same staking credential
    private static final String BASE_ADDRESS_WITH_STAKE = ACCOUNT_1.baseAddress();
    private static final String MATCHING_STAKE_ADDRESS = MAINNET_STAKE_ADDRESS;

    @Test
    void testExtractShelleyAddress_withShelleyBaseAddress_returnsAddress() {
        Optional<Address> result = AddressUtil.extractShelleyAddress(MAINNET_BASE_ADDRESS);
        assertTrue(result.isPresent());
        assertEquals(MAINNET_BASE_ADDRESS, result.get().getAddress());
    }

    @Test
    void testExtractShelleyAddress_withAnotherShelleyAddress_returnsAddress() {
        Optional<Address> result = AddressUtil.extractShelleyAddress(MAINNET_BASE_ADDRESS_2);
        assertTrue(result.isPresent());
        assertEquals(MAINNET_BASE_ADDRESS_2, result.get().getAddress());
    }

    @Test
    void testExtractShelleyAddress_withShelleyStakeAddress_returnsAddress() {
        Optional<Address> result = AddressUtil.extractShelleyAddress(MAINNET_STAKE_ADDRESS);
        assertTrue(result.isPresent());
        assertEquals(MAINNET_STAKE_ADDRESS, result.get().getAddress());
    }

    @Test
    void testExtractShelleyAddress_withByronAddress_returnsEmpty() {
        Optional<Address> result = AddressUtil.extractShelleyAddress(BYRON_ADDRESS);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractShelleyAddress_withInvalidAddress_returnsEmpty() {
        Optional<Address> result = AddressUtil.extractShelleyAddress("invalid_address");
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractShelleyAddress_withNullAddress_returnsEmpty() {
        Optional<Address> result = AddressUtil.extractShelleyAddress(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testShorten_withValidAddress_returnsShortened() {
        String shortened = AddressUtil.shorten(MAINNET_BASE_ADDRESS);
        assertEquals("addr1q...", shortened.substring(0, 9));
        assertTrue(shortened.contains("..."));
        assertEquals(17, shortened.length()); // 6 + 3 + 8
    }

    @Test
    void testAreAddressesMatching_baseAddressToSameBaseAddress_returnsTrue() {
        Address watchingAddress = new Address(MAINNET_BASE_ADDRESS);
        assertTrue(AddressUtil.areAddressesMatching(watchingAddress, MAINNET_BASE_ADDRESS));
    }

    @Test
    void testAreAddressesMatching_baseAddressToDifferentAddress_returnsFalse() {
        Address watchingAddress = new Address(MAINNET_BASE_ADDRESS);
        assertFalse(AddressUtil.areAddressesMatching(watchingAddress, MAINNET_ENTERPRISE_ADDRESS));
    }

    @Test
    void testAreAddressesMatching_enterpriseAddressToSameEnterpriseAddress_returnsTrue() {
        Address watchingAddress = new Address(MAINNET_ENTERPRISE_ADDRESS);
        assertTrue(AddressUtil.areAddressesMatching(watchingAddress, MAINNET_ENTERPRISE_ADDRESS));
    }

    @Test
    void testAreAddressesMatching_stakeAddressToBaseAddress_matchesStakingPart() {
        // Using known addresses that share the same staking credential
        Address stakeAddress = new Address(MATCHING_STAKE_ADDRESS);
        assertTrue(AddressUtil.areAddressesMatching(stakeAddress, BASE_ADDRESS_WITH_STAKE));
    }

    @Test
    void testAreAddressesMatching_stakeAddressToEnterpriseAddress_returnsFalse() {
        Address watchingAddress = new Address(MAINNET_STAKE_ADDRESS);
        assertFalse(AddressUtil.areAddressesMatching(watchingAddress, MAINNET_ENTERPRISE_ADDRESS));
    }

    @Test
    void testAreAddressesMatching_stringOverload_baseAddressMatch() {
        assertTrue(AddressUtil.areAddressesMatching(MAINNET_BASE_ADDRESS, MAINNET_BASE_ADDRESS));
    }

    @Test
    void testAreAddressesMatching_stringOverload_noMatch() {
        assertFalse(AddressUtil.areAddressesMatching(MAINNET_BASE_ADDRESS, MAINNET_ENTERPRISE_ADDRESS));
    }

    @Test
    void testMatchingCredential_enterpriseAddress_matchesPaymentCredential() {
        Address enterpriseAddress = new Address(MAINNET_ENTERPRISE_ADDRESS);
        Credential paymentCredential = enterpriseAddress.getPaymentCredential().orElseThrow();

        assertTrue(AddressUtil.matchingCredential(enterpriseAddress, paymentCredential));
    }

    @Test
    void testMatchingCredential_baseAddress_matchesPaymentCredential() {
        Address baseAddress = new Address(MAINNET_BASE_ADDRESS);
        Credential paymentCredential = baseAddress.getPaymentCredential().orElseThrow();

        assertTrue(AddressUtil.matchingCredential(baseAddress, paymentCredential));
    }

    @Test
    void testMatchingCredential_baseAddress_matchesDelegationCredential() {
        Address baseAddress = new Address(MAINNET_BASE_ADDRESS);
        Credential delegationCredential = baseAddress.getDelegationCredential().orElseThrow();

        assertTrue(AddressUtil.matchingCredential(baseAddress, delegationCredential));
    }

    @Test
    void testMatchingCredential_rewardAddress_matchesDelegationCredential() {
        Address rewardAddress = new Address(MAINNET_STAKE_ADDRESS);
        Credential delegationCredential = rewardAddress.getDelegationCredential().orElseThrow();

        assertTrue(AddressUtil.matchingCredential(rewardAddress, delegationCredential));
    }

    @Test
    void testMatchingCredential_wrongCredential_returnsFalse() {
        Address enterpriseAddress = new Address(MAINNET_ENTERPRISE_ADDRESS);
        // Create a different credential using a random hash
        byte[] randomHash = Blake2bUtil.blake2bHash224("different".getBytes());
        Credential wrongCredential = Credential.fromKey(randomHash);

        assertFalse(AddressUtil.matchingCredential(enterpriseAddress, wrongCredential));
    }

}