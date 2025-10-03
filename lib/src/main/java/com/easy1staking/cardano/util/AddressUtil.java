package com.easy1staking.cardano.util;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.address.AddressType;
import com.bloxbean.cardano.client.address.Credential;
import com.bloxbean.cardano.client.address.util.AddressEncoderDecoderUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.util.Optional.empty;

@Slf4j
public class AddressUtil {

    public static Optional<Address> extractShelleyAddress(String address) {
        try {
            var addressBytes = com.bloxbean.cardano.client.address.util.AddressUtil.addressToBytes(address);
            var addressType = AddressEncoderDecoderUtil.readAddressType(addressBytes);
            if (AddressType.Byron.equals(addressType)) {
                return empty();
            } else {
                return Optional.of(new Address(address));
            }
        } catch (Exception e) {
            return empty();
        }
    }

    public static String shorten(String address) {
        return address.substring(0, 6) +
                "..." +
                address.substring(address.length() - 8);
    }

    /**
     * @param watchingAddress
     * @param address         can't be a staking address
     * @return
     */
    public static boolean areAddressesMatching(Address watchingAddress, String address) {
        return switch (watchingAddress.getAddressType()) {
            case Base, Enterprise -> watchingAddress.getAddress().equals(address);
            case Reward -> extractShelleyAddress(address)
                    .flatMap(shellyAddress -> switch (shellyAddress.getAddressType()) {
                        case Base, Reward -> Optional.of(shellyAddress);
                        default -> Optional.empty();
                    })
                    .map(AddressProvider::getStakeAddress)
                    .map(stakingAddress -> watchingAddress.getAddress().equals(stakingAddress.getAddress()))
                    .orElse(false);
            default -> false;
        };
    }

    public static boolean matchingCredential(Address watchingAddress, Credential credential) {
        return switch (watchingAddress.getAddressType()) {
            case Enterprise -> watchingAddress.getPaymentCredential().map(credential::equals).orElse(false);
            case Base -> watchingAddress.getPaymentCredential().map(credential::equals).orElse(false) ||
                    watchingAddress.getDelegationCredential().map(credential::equals).orElse(false);
            case Reward -> watchingAddress.getDelegationCredential().map(credential::equals).orElse(false);
            default -> false;
        };
    }

    public static boolean areAddressesMatching(String watchingAddress, String address) {
        return areAddressesMatching(new Address(watchingAddress), address);
    }

}
