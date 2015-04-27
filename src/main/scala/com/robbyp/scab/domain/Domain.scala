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


