/**
 * Copyright (c) 2015 Purcell Informatics Limited.
 *
 */
package com.robbyp.scab.domain

import com.robbyp.scab.TestDataGenerators._
import org.joda.money.BigMoney
import org.joda.time.LocalDate
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.{FunSuite, Matchers}

class TransactionUnitTest extends FunSuite with Matchers {

  test("should return a total value") {
    // Given
    val uniqueId = 1L
    val date = new LocalDate()
    val accountType = anyString()
    val description = anyString()
    val accountId = 1L
    val amounts = Table(
      ("quantity", "price", "total"),
      (1, "GBP 10", "GBP 10"),
      (2, "GBP 10", "GBP 20"),
      (5, "GBP -10", "GBP -50")
    )

    forAll(amounts) {
      // When
      (quantity, price, total) =>
        val transaction = Transaction(
          uniqueId,
          BigDecimal(quantity),
          BigMoney.parse(price),
          date,
          accountType,
          description,
          accountId
        )

        // Then
        transaction.total should equal(BigMoney.parse(total))
    }
  }

}
