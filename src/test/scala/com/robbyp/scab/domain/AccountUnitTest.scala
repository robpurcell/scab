/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */
package com.robbyp.scab.domain

import com.github.nscala_time.time.Imports._
import com.robbyp.scab.TestDataGenerators._
import org.joda.money.{BigMoney, CurrencyMismatchException, CurrencyUnit}
import org.joda.time.{DateTime, Days, Interval}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.{FunSuite, Matchers}

class AccountUnitTest extends FunSuite with Matchers {

  val today = new DateTime()

  val defaultGBPAccountInfo = AccountInfo(uniqueId = 1L,
    name = anyString(),
    number = anyString(),
    institution = anyString(),
    currency = CurrencyUnit.GBP,
    accountType = AccountType.Current,
    openingDate = today - Days.days(10),
    openingBalance = BigMoney.parse("GBP 0")
  )

  def intervalStart = (today - Days.ONE).toInstant

  def intervalStop = (today + Days.ONE).toInstant

  def interval = new Interval(intervalStart, intervalStop)

  test("duplicate entries cannot be added to the account") {
    // Given
    val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)

    // When
    account.addEntry(BigMoney.parse("GBP 10"), today)
    account.addEntry(BigMoney.parse("GBP 10"), today)

    // Then
    assert(account.accountingEntries.size === 1)
  }

  test("should throw an exception if entry does not have account currency") {
    // Given
    val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)

    // Then
    intercept[CurrencyMismatchException] {
      account.addEntry(BigMoney.parse("USD 10"), today)
    }
  }

  test("should check the currency of money matches the account") {
    // Given
    val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
    account.addEntry(BigMoney.parse("GBP 10"), today)
  }

  test("should add entries to the account") {
    // Given
    val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)

    // When
    account.addEntry(BigMoney.parse("GBP 10"), today)

    // Then
    assert(account.accountingEntries.contains(Entry(BigMoney.parse("GBP 10"), today)))
  }

  test("should return a list of entries in the date interval") {
    // Given
    val intervals = Table(
      ("first", "middle", "last", "numberOfEntries"),
      (today - Days.TWO, today - Days.TWO, today - Days.TWO, 0),
      (today, today, today, 3),
      (today - Days.ONE, today, today + Days.ONE, 2),
      (today, today + Days.ONE, today + Days.TWO, 1),
      (today + Days.TWO, today + Days.TWO, today + Days.TWO, 0)
    )

    forAll(intervals) {
      (first, middle, last, numberOfEntries) =>
        // When
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        account.addEntry(BigMoney.parse("GBP 10"), first)
        account.addEntry(BigMoney.parse("GBP 20"), middle)
        account.addEntry(BigMoney.parse("GBP 30"), last)

        // Then
        account.entriesForInterval(interval).size should equal(numberOfEntries)
    }
  }

  test("should return the total of the entries") {
    // Given
    val amounts = Table(
      ("amount1", "amount2", "balance"),
      (100, 30, 130),
      (20, -40, -20),
      (0, 0, 0)
    )

    forAll(amounts) {
      (amount1, amount2, balance) =>
        // When
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        account.addEntry(BigMoney.parse("GBP " + amount1), today)
        account.addEntry(BigMoney.parse("GBP " + amount2), today)

        // Then
        account.balance(interval) should equal(BigMoney.parse("GBP " + balance))
    }
  }

  test("should return the balance for entries created since account opening") {
    // Given
    val amounts = Table(
      ("amount1", "amount2", "balance"),
      (100, 30, 130),
      (20, -40, -20),
      (0, 0, 0)
    )

    // When
    forAll(amounts) {
      (amount1, amount2, balance) =>
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        account.addEntry(BigMoney.parse("GBP " + amount1), today - Days.days(5))
        account.addEntry(BigMoney.parse("GBP " + amount2), today - Days.days(2))

        // Then
        account.balance(today) should equal(BigMoney.parse("GBP " + balance))
    }
  }

  test("should return the balance for now") {
    // Given
    val amounts = Table(
      ("amount1", "amount2", "balance"),
      (100, 30, 130),
      (20, -40, -20),
      (0, 0, 0)
    )

    // When
    forAll(amounts) {
      (amount1, amount2, balance) =>
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        account.addEntry(BigMoney.parse("GBP " + amount1), today - Days.days(5))
        account.addEntry(BigMoney.parse("GBP " + amount2), today - Days.days(2))

        // Then
        account.balance() should equal(BigMoney.parse("GBP " + balance))
    }
  }


  test("should return the positive balance and entries") {
    // Given
    val amounts = Table(
      ("amount1", "amount2", "balance", "numberOfEntries"),
      (100, 30, 130, 2),
      (20, -40, 20, 1),
      (0, 0, 0, 0),
      (-12, -32, 0, 0)
    )

    // When
    forAll(amounts) {
      (amount1, amount2, balance, numberOfEntries) =>
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        def entry1 = new Entry(BigMoney.parse("GBP " + amount1), today)
        def entry2 = new Entry(BigMoney.parse("GBP " + amount2), today)
        account.addEntry(entry1)
        account.addEntry(entry2)

        // Then
        account.depositEntriesForInterval(interval).size should equal(numberOfEntries)
        account.deposits(interval) should equal(BigMoney.parse("GBP " + balance))
    }
  }

  test("should return the negative balance and entries") {
    // Given
    val amounts = Table(
      ("amount1", "amount2", "balance", "numberOfEntries"),
      (100, 30, 0, 0),
      (20, -40, -40, 1),
      (0, 0, 0, 0),
      (-12, -32, -44, 2)
    )

    // When
    forAll(amounts) {
      (amount1, amount2, balance, numberOfEntries) =>
        val account = Account(uniqueId = 1, accountInfo = defaultGBPAccountInfo)
        def entry1 = new Entry(BigMoney.parse("GBP " + amount1), today)
        def entry2 = new Entry(BigMoney.parse("GBP " + amount2), today)
        account.addEntry(entry1)
        account.addEntry(entry2)

        // Then
        account.withdrawalEntriesForInterval(interval).size should equal(numberOfEntries)
        account.withdrawals(interval) should equal(BigMoney.parse("GBP " + balance))
    }
  }

}

