package com.webcerebrium.bitfinex.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.webcerebrium.bitfinex.datatype.BitfinexSymbol;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Data
public class BitfinexApi {

    /* Actual API key and Secret Key that will be used */
    public String apiKey;
    public String secretKey;

    public BitfinexConfig config = new BitfinexConfig();

    /**
     * API Base URL
     */
    public String baseUrl = "https://api.bitfinex.com/";

    /**
     * Guava Class Instance for escaping
     */
    private Escaper esc = UrlEscapers.urlFormParameterEscaper();


    /**
     * Constructor of API when you exactly know the keys
     * @param apiKey Public API Key
     * @param secretKey Secret API Key
     * @throws BitfinexApiException in case of any error
     */
    public BitfinexApi(String apiKey, String secretKey) throws BitfinexApiException {

        this.apiKey = apiKey;
        this.secretKey = secretKey;
        validateCredentials();
    }

    /**
     * Constructor of API - keys are loaded from VM options, environment variables, resource files
     * @throws BitfinexApiException in case of any error
     */
    public BitfinexApi() {
        this.apiKey = config.getVariable("BITFINEX_API_KEY");
        this.secretKey = config.getVariable("BITFINEX_SECRET_KEY");
    }

    /**
     * Validation we have API keys set up
     * @throws BitfinexApiException in case of any error
     */
    protected void validateCredentials() throws BitfinexApiException {
        String humanMessage = "Please check environment variables or VM options";
        if (Strings.isNullOrEmpty(this.getApiKey()))
            throw new BitfinexApiException("Missing BITFINEX_API_KEY. " + humanMessage);
        if (Strings.isNullOrEmpty(this.getSecretKey()))
            throw new BitfinexApiException("Missing BITFINEX_SECRET_KEY. " + humanMessage);
    }

    public JsonArray getSymbols() throws BitfinexApiException {
        return new BitfinexRequest(baseUrl + "v1/symbols").read().asJsonArray();
    }

    public JsonArray getSymbolDetails() throws BitfinexApiException {
        return new BitfinexRequest(baseUrl + "v1/symbols_details").read().asJsonArray();
    }

    // MARKET INFORMATION
    public Set<String> getCoinsOf(String coin) {
        try {
            Set<String> results = new TreeSet<>();
            JsonArray symbols = this.getSymbols();
            log.info("SYMBOLS = {}", symbols.toString());
            for (JsonElement element: symbols) {
                BitfinexSymbol symbol = BitfinexSymbol.valueOf(element.getAsString());
                if (symbol.contains(coin)) {
                    results.add(symbol.getOpposite(coin));
                }
            }
            return results;
        } catch (Exception e) {
            log.error("Bitfinex UNCAUGHT EXCEPTION {}", e.getMessage());
        } catch (BitfinexApiException e) {
            log.warn("Bitfinex ERROR {}", e.getMessage());
        }
        return ImmutableSet.of();
    }


}
