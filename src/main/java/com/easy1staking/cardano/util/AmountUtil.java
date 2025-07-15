package com.easy1staking.cardano.util;

import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.transaction.spec.Asset;
import com.bloxbean.cardano.client.transaction.spec.MultiAsset;
import com.bloxbean.cardano.client.transaction.spec.Value;
import com.bloxbean.cardano.yaci.store.common.domain.Amt;
import com.easy1staking.cardano.model.AssetType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AmountUtil {

    public static Value fromAmount(Amount amount) {
        var assetType = AssetType.fromUnit(amount.getUnit());
        if (assetType.isAda()) {
            return Value.builder().coin(amount.getQuantity()).build();
        } else {
            return Value.builder()
                    .multiAssets(List.of(
                            MultiAsset.builder()
                                    .policyId(amount.getUnit().substring(0, 56))
                                    .assets(List.of(Asset.builder().name("0x" + amount.getUnit().substring(56)).value(amount.getQuantity()).build()))
                                    .build()
                    ))
                    .build();
        }
    }

    public static Value toValue(com.bloxbean.cardano.yaci.core.model.Amount amount) {
        var assetType = AssetType.fromUnit(amount.getUnit());
        Value value;
        if (assetType.isAda()) {
            value = Value.builder().coin(amount.getQuantity()).build();
        } else {
            value = Value.builder()
                    .multiAssets(List.of(
                            MultiAsset.builder()
                                    .policyId(amount.getPolicyId())
                                    .assets(List.of(Asset.builder().name("0x" + assetType.assetName()).value(amount.getQuantity()).build()))
                                    .build()
                    ))
                    .build();
        }

        log.debug("amount: {}", amount);
        log.debug("value: {}", value);

        return value;
    }

    public static Amount toAmountCore(com.bloxbean.cardano.yaci.core.model.Amount amount) {
        return Amount.builder()
                .unit(amount.getUnit().replaceAll("\\.", ""))
                .quantity(amount.getQuantity())
                .build();
    }

    public static Amount toAmountCore(Amt amount) {
        return Amount.builder()
                .unit(amount.getUnit().replaceAll("\\.", ""))
                .quantity(amount.getQuantity())
                .build();
    }

    public static Value toValue(Amt amount) {
        var assetType = AssetType.fromUnit(amount.getUnit());
        Value value;
        if (assetType.isAda()) {
            value = Value.builder().coin(amount.getQuantity()).build();
        } else {
            value = Value.builder()
                    .multiAssets(List.of(MultiAsset.builder()
                            .policyId(assetType.policyId())
                            .assets(List.of(Asset.builder()
                                    .name("0x" + assetType.assetName())
                                    .value(amount.getQuantity())
                                    .build()))
                            .build()))
                    .build();
        }

        log.debug("amt: {}", amount);
        log.debug("value: {}", value);

        return value;
    }


}
