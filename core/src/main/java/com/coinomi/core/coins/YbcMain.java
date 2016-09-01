package com.coinomi.core.coins;

import com.coinomi.core.coins.families.PeerFamily;

public class YbcMain extends CoinType {
    private YbcMain() {
        id = "ybc.main";

        addressHeader = 78;
        p2shHeader = 140;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        spendableCoinbaseDepth = 30;

        family = PeerFamily.get();
        name = "YBCCoin";
        symbol = "YBC";
        uriScheme = "ybcoin";
        bip44Index = 49;
        unitExponent = 8;
        feePerKb = value(10000);
        minNonDust = value(1);
        softDustLimit = value(10000);
        softDustPolicy = SoftDustPolicy.AT_LEAST_BASE_FEE_IF_SOFT_DUST_TXO_PRESENT;
        signedMessageHeader = toBytes("YBC Signed Message:\n");
    }

    private static YbcMain instance = new YbcMain();
    public static synchronized YbcMain get() {
        return instance;
    }
}
