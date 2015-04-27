// Copyright 2015 Purcell Informatics Limited
//
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.robbyp.scab.domain

import org.joda.money.{BigMoney, CurrencyUnit}
import org.joda.time.{Interval, DateTime}

import scala.collection.mutable

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
