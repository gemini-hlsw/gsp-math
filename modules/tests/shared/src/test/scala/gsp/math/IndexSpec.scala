// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math

import cats.tests.CatsSuite
import cats.kernel.laws.discipline._
import gsp.math.arb._
import monocle.law.discipline._

final class IndexSpec extends CatsSuite {
  import ArbIndex._

  // Laws
  checkAll("Index", OrderTests[Index].order)
  checkAll("Index.fromShort", PrismTests(Index.fromShort))
  checkAll("Index.fromString", PrismTests(Index.fromString))

}
