package com.murano500k.coldwallet.model

import java.math.BigDecimal

data class StatsItem(val baseCurrency: String,
                     val total: BigDecimal,
                     val rows: List<StatsRow>
 )