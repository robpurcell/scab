/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */
package com.robbyp.scab

import java.sql.Timestamp

import com.robbyp.scab.domain.AccountType
import com.robbyp.scab.domain.AccountType.AccountType
import org.apache.commons.lang3.RandomStringUtils
import org.joda.money.CurrencyUnit
import org.joda.time.DateTime

object TestDataGenerators {

  def anyString(): String = anyString(Math.floor(Math.random() * 10).intValue())

  def anyString(length: Integer): String = RandomStringUtils.random(length)

  def anyInt(max: Int = 100): Int = Math.floor(Math.random() * max).intValue()

  def anyLong(): Long = anyInt().longValue()

  def anyChar(): Char = RandomStringUtils.random(1).toCharArray.head

  def anyDate(): DateTime = new DateTime(randomTimeBetweenTwoDates())

  private val BEGIN_TIME = Timestamp.valueOf("2010-01-01 00:00:00").getTime
  private val END_TIME = Timestamp.valueOf("2020-12-31 00:58:00").getTime

  private def randomTimeBetweenTwoDates() = {
    val diff = END_TIME - BEGIN_TIME + 1
    BEGIN_TIME + (Math.random() * diff)
  }

  def anyCurrency(): CurrencyUnit = {
    val currencies = Array(CurrencyUnit.CHF, CurrencyUnit.GBP, CurrencyUnit.USD, CurrencyUnit.EUR, CurrencyUnit.CAD)
    currencies(anyInt(currencies.length))
  }

  def anyAccountType(): AccountType = {
    val types = Array(AccountType.CreditCard, AccountType.Current, AccountType.Savings, AccountType.Mortgage)
    types(anyInt(types.length))
  }

}
