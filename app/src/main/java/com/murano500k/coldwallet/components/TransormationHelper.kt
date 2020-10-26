package com.murano500k.coldwallet.components

import android.content.Context
import android.util.Log
import java.math.BigDecimal
import javax.inject.Inject

class TransormationHelper @Inject constructor(
    private val context: Context,
    private val repository: Repository
) {

    companion object {
        const val TAG = "TransormationHelper"
        const val CODE_UAH = "UAH"
        const val CODE_USD = "USD"
        const val CODE_BTC = "BTC"
        const val CODE_EUR = "EUR"
    }

    suspend fun getAmountInOtherCurrency(amount: BigDecimal,
                                 baseCode: String,
                                 baseIsCrypto: Boolean,
                                 compareCode: String,
                                 compareIsCrypto: Boolean ): BigDecimal{
        return if(baseIsCrypto && compareIsCrypto){
            convertCryptoToCrypto(amount, baseCode, compareCode)
        }else if(baseIsCrypto && !compareIsCrypto){
            convertCryptoToFiat(amount, baseCode, compareCode)
        }else if(!baseIsCrypto && compareIsCrypto){
            convertFiatToCrypto(amount, baseCode, compareCode)
        }else {
            convertFiatToFiat(amount, baseCode, compareCode)
        }

    }





    private suspend fun convertCryptoToCrypto(
        amount: BigDecimal,
        baseCode: String,
        compareCode: String
    ): BigDecimal {
        if(baseCode.equals(compareCode)) return amount
        val rate = getCryptoRate(baseCode, compareCode)
        if(rate < BigDecimal.ZERO) return BigDecimal.ZERO
        else {
            return amount * rate
        }
    }


    private suspend fun getCryptoRate(rateFrom: String, rateTo: String) : BigDecimal {
        val symbol = "${rateFrom}${rateTo}"
        val symbolReverse = "${rateTo}${rateFrom}"
        Log.w(TAG, "getCryptoRate: symbol=$symbol symbolReverse=$symbolReverse" )

        if(rateFrom.equals(rateTo)){
            //same currency
            return BigDecimal.ONE
        }
        repository.getCryptoPricePairs().forEach{item ->
            if(item.symbol.contains(symbol)) {
                Log.w(TAG, "getCryptoRate: symbol=$symbol item.price= ${item.price}" )
                return BigDecimal(item.price)
            }
            if(item.symbol.contains(symbolReverse)) {
                val result = 1 / item.price.toDouble()
                Log.w(TAG, "getCryptoRate: symbolReverse=$symbolReverse iresult= ${result}" )
                return BigDecimal(result)
            }
        }
        return BigDecimal.ZERO
    }
    private suspend fun getRateEURtoUSD(): BigDecimal {
        repository.getFiatPricePairs().forEach {
            if(it.currencyCodeA.equals(CODE_EUR) && it.currencyCodeB.equals(CODE_USD)) {
                Log.w(TAG, "getRateEURtoUSD: ${it.rateCross} " )
                return BigDecimal(it.rateCross)
            }
        }
        return BigDecimal.ZERO
    }

    private suspend fun getFiatRateToUAH(rate: String): BigDecimal {
        if(CODE_UAH.equals(rate)) return BigDecimal.ONE
        if(CODE_EUR.equals(rate)){
            repository.getFiatPricePairs().forEach {
                if (it.currencyCodeA.equals(CODE_USD) && it.currencyCodeB.equals(CODE_UAH)) {
                    return BigDecimal(it.rateCross) * getRateEURtoUSD()
                }
            }
        }else {
            repository.getFiatPricePairs().forEach {
                if (it.currencyCodeA.equals(rate) && it.currencyCodeB.equals(CODE_UAH)) {
                    return BigDecimal(it.rateCross)
                }
            }
        }
        return BigDecimal.ZERO
    }

    private suspend fun convertFiatToFiat(
        amount: BigDecimal,
        baseCode: String,
        compareCode: String
    ): BigDecimal {
        if(baseCode.equals(compareCode)) {
            return amount
        }

        val amountInUAH = amount * getFiatRateToUAH(baseCode)
        val amountInCompareCurrency = amountInUAH / getFiatRateToUAH(compareCode)

        return amountInCompareCurrency

    }



    private suspend fun convertFiatToCrypto(
        amount: BigDecimal,
        baseCode: String,
        compareCode: String
    ): BigDecimal {
        val fiatRateToUAH = getFiatRateToUAH(baseCode)
        val amountInUAH = amount * getFiatRateToUAH(baseCode)
        val rateBTCUAH = getCryptoRate(CODE_BTC, CODE_UAH)
        val amountInBTC = amountInUAH / rateBTCUAH
        return amountInBTC / getCryptoRate(compareCode, CODE_BTC)
    }


    private suspend fun convertCryptoToFiat(
        amount: BigDecimal,
        baseCode: String,
        compareCode: String
    ): BigDecimal {
        Log.w(TAG, "convertCryptoToFiat: $amount $baseCode to $compareCode" )

        val cryptoRateToBTC = getCryptoRate(baseCode, CODE_BTC)
        val amountInBTC = amount * cryptoRateToBTC

        Log.w(TAG, "convertCryptoToFiat: amountInBTC=$amountInBTC cryptoRateToBTC=$cryptoRateToBTC" )

        val rateBTCUAH = getCryptoRate(CODE_BTC, CODE_UAH)
        val amountInUAH = amountInBTC * rateBTCUAH
        Log.w(TAG, "convertCryptoToFiat: rateBTCUAH=$rateBTCUAH amountInUAH=$amountInUAH" )


        return amountInUAH / getFiatRateToUAH(compareCode)
    }
}