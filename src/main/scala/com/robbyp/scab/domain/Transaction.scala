/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */
package com.robbyp.scab.domain

import org.joda.money.BigMoney
import org.joda.time.LocalDate

case class Transaction(uniqueId: Long,
                       quantity: BigDecimal,
                       price: BigMoney,
                       date: LocalDate,
                       accountType: String,
                       description: String,
                       accountId: Long) {

  val total: BigMoney = price.multipliedBy(quantity.bigDecimal)

}

