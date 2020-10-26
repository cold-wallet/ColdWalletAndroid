package com.murano500k.coldwallet.model

import com.murano500k.coldwallet.database.Asset
import java.math.BigDecimal

data class ConvertedAsset (val baseAsset: Asset, 
                           val convertedCurrencyCode: String, 
                           val convertedAmount: BigDecimal,
                           val convertedIsCrypto: Boolean
                           )