package com.easy1staking.cardano.util;

import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.api.util.ValueUtil;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.yaci.core.model.TransactionBody;
import com.bloxbean.cardano.yaci.core.model.TransactionOutput;
import com.bloxbean.cardano.yaci.store.common.domain.AddressUtxo;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.AddressUtxoEntity;

import java.util.ArrayList;
import java.util.List;

public class UtxoUtil {

    public static List<Utxo> getUtxos(TransactionBody transactionBody) {
        var utxos = new ArrayList<Utxo>();
        for (int i = 0; i < transactionBody.getOutputs().size(); i++) {
            TransactionOutput output = transactionBody
                    .getOutputs()
                    .get(i);
            utxos.add(toUtxo(output, transactionBody.getTxHash(), i));
        }
        return utxos;
    }

    public static List<Utxo> getUtxos(com.bloxbean.cardano.client.transaction.spec.TransactionBody transactionBody, String txHash) {
        var utxos = new ArrayList<Utxo>();
        for (int i = 0; i < transactionBody.getOutputs().size(); i++) {

            var output = transactionBody
                    .getOutputs()
                    .get(i);

            String dataHash = null;
            if (output.getDatumHash() != null) {
                dataHash = HexUtil.encodeHexString(output.getDatumHash());
            }

            var utxo = Utxo.builder()
                    .txHash(txHash)
                    .outputIndex(i)
                    .address(output.getAddress())
                    .dataHash(dataHash)
                    .inlineDatum(output.getInlineDatum() != null ? output.getInlineDatum().serializeToHex() : null)
                    .amount(ValueUtil.toAmountList(output.getValue()))
                    .build();

            utxos.add(utxo);
        }
        return utxos;
    }

    public static Utxo toUtxo(TransactionOutput output, String txHash, int index) {
        return Utxo.builder()
                .txHash(txHash)
                .outputIndex(index)
                .address(output.getAddress())
                .amount(output.getAmounts().stream().map(AmountUtil::toAmountCore).toList())
                .dataHash(output.getDatumHash())
                .inlineDatum(output.getInlineDatum())
                .referenceScriptHash(output.getScriptRef())
                .build();
    }

    public static Utxo toUtxo(AddressUtxoEntity addressUtxoEntity) {
        return Utxo.builder()
                .txHash(addressUtxoEntity.getTxHash())
                .outputIndex(addressUtxoEntity.getOutputIndex())
                .address(addressUtxoEntity.getOwnerAddr())
                .amount(addressUtxoEntity.getAmounts().stream().map(AmountUtil::toAmountCore).toList())
                .dataHash(addressUtxoEntity.getDataHash())
                .inlineDatum(addressUtxoEntity.getInlineDatum())
                .referenceScriptHash(addressUtxoEntity.getReferenceScriptHash())
                .build();
    }

    public static Utxo toUtxo(AddressUtxo addressUtxo) {
        return Utxo.builder()
                .txHash(addressUtxo.getTxHash())
                .outputIndex(addressUtxo.getOutputIndex())
                .address(addressUtxo.getOwnerAddr())
                .amount(addressUtxo.getAmounts().stream().map(AmountUtil::toAmountCore).toList())
                .dataHash(addressUtxo.getDataHash())
                .inlineDatum(addressUtxo.getInlineDatum())
                .referenceScriptHash(addressUtxo.getScriptRef())
                .build();
    }

}
