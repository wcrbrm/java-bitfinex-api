package com.webcerebrium.bitfinex.datatype;

import com.google.common.base.Strings;
import com.webcerebrium.bitfinex.api.BitfinexApiException;

public class BitfinexSymbol {

    String symbol = "";

    public BitfinexSymbol(String symbol)  throws BitfinexApiException {
        // sanitizing symbol, preventing from common user-input errors
        if (Strings.isNullOrEmpty(symbol)) {
            throw new BitfinexApiException("Symbol cannot be empty. Example: ltcbtc");
        }
        if (symbol.contains(" ")) {
            throw new BitfinexApiException("Symbol cannot contain spaces. Example: ltcbtc");
        }
        if (!symbol.endsWith("btc") && !symbol.endsWith("eth") && !symbol.endsWith("usd")) {
            throw new BitfinexApiException("Market Symbol should be ending with btc, eth, usd. Example: ltcbtc. Provided: " + symbol);
        }
        this.symbol = symbol.replace("_", "").replace("-", "").toLowerCase();
    }

    public String get(){ return this.symbol; }

    public String getSymbol(){ return this.symbol; }

    public String toString() { return this.get(); }

    public static BitfinexSymbol valueOf(String s) throws BitfinexApiException {
        return new BitfinexSymbol(s);
    }

    public static BitfinexSymbol BTC(String pair) throws BitfinexApiException {
        return BitfinexSymbol.valueOf(pair.toLowerCase() + "btc");
    }

    public static BitfinexSymbol ETH(String pair) throws BitfinexApiException {
        return BitfinexSymbol.valueOf(pair.toLowerCase() + "eth");
    }

    public static BitfinexSymbol USD(String pair) throws BitfinexApiException {
        return BitfinexSymbol.valueOf(pair.toLowerCase() + "usd");
    }

    public boolean contains(String coin) {
        return (symbol.endsWith(coin.toLowerCase())) || (symbol.startsWith(coin.toLowerCase()));
    }

    public String getOpposite(String coin) {
        if (symbol.startsWith(coin.toLowerCase())) {
            return symbol.substring((coin).length());
        }
        if (symbol.endsWith(coin.toLowerCase())) {
            int index = symbol.length() - (coin).length();
            return symbol.substring(0, index);
        }
        return "";
    }
}
