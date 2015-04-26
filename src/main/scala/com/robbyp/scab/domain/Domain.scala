/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */
package com.robbyp.scab.domain


import org.joda.money.{BigMoney, CurrencyUnit}
import org.joda.time.DateTime

case class Entry(amount: BigMoney, date: DateTime)

object AccountType extends Enumeration {
  type AccountType = Value
  val Current, Savings, Mortgage = Value
  val CreditCard = Value(name = "Credit Card")
}

import com.robbyp.scab.domain.AccountType.AccountType

case class AccountInfo(uniqueId: Long,
                       name: String,
                       number: String,
                       institution: String,
                       currency: CurrencyUnit,
                       accountType: AccountType,
                       openingDate: DateTime,
                       openingBalance: BigMoney)


