package com.robbyp.scab.domain

/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */

import org.joda.money.{BigMoney, CurrencyUnit}
import org.joda.time.{DateTime, Interval}
import collection.mutable

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

case class Account(uniqueId: Long, accountInfo: AccountInfo) {
  var accountingEntries = mutable.Set[Entry]()
  val currency: CurrencyUnit = accountInfo.currency
  val zeroAmount: BigMoney = BigMoney.of(currency, BigDecimal(0).bigDecimal)

  def addEntry(amount: BigMoney, date: DateTime) {
    BigMoney.nonNull(amount, accountInfo.currency)
    accountingEntries += new Entry(amount, date)
  }

  def addEntry(entry: Entry) {
    BigMoney.nonNull(entry.amount, accountInfo.currency)
    accountingEntries += entry
  }

  def entriesForInterval(interval: Interval): Seq[Entry] = accountingEntries.filter((entry: Entry) => interval.contains(entry.date.toInstant)).toSeq

  def balance(interval: Interval): BigMoney = entriesForInterval(interval).foldLeft(zeroAmount)(sumMoneyAmounts)

  def balance(date: DateTime): BigMoney = balance(new Interval(accountInfo.openingDate.toInstant, date.toInstant))

  def balance(): BigMoney = balance(new DateTime())

  def depositEntriesForInterval(interval: Interval): Seq[Entry] = entriesForInterval(interval).filter((entry: Entry) => entry.amount.isPositive)

  def deposits(interval: Interval): BigMoney = depositEntriesForInterval(interval).foldLeft(zeroAmount)(sumMoneyAmounts)

  def withdrawalEntriesForInterval(interval: Interval): Seq[Entry] = entriesForInterval(interval).filter((entry: Entry) => entry.amount.isNegative)

  def withdrawals(interval: Interval): BigMoney = withdrawalEntriesForInterval(interval).foldLeft(zeroAmount)(sumMoneyAmounts)

  private def sumMoneyAmounts = (sum: BigMoney, e: Entry) => sum.plus(BigMoney.of(currency, e.amount.getAmount))
}
