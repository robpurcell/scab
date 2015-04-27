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
