// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math

import cats.tests.CatsSuite
import cats.{ Eq, Show }
import cats.kernel.laws.discipline._
import gsp.math.arb._
import gsp.math.laws.discipline._
import java.time.LocalDateTime

@SuppressWarnings(Array("org.wartremover.warts.ToString", "org.wartremover.warts.Equals"))
final class EpochSpec extends CatsSuite {
  import ArbEpoch._
  import ArbTime._

  // Laws
  checkAll("Epoch", OrderTests[Epoch].order)
  checkAll("fromString", FormatTests(Epoch.fromString).formatWith(ArbEpoch.strings))

  test("Epoch.eq.natural") {
    forAll { (a: Epoch, b: Epoch) =>
      a.equals(b) shouldEqual Eq[Epoch].eqv(a, b)
    }
  }

  test("Epoch.show.natural") {
    forAll { (a: Epoch) =>
      a.toString shouldEqual Show[Epoch].show(a)
    }
  }

  test("Epoch.until.identity") {
    forAll { (a: Epoch) =>
      a.untilEpochYear(a.epochYear) shouldEqual 0.0
    }
  }

  test("Epoch.until.sanity") {
    forAll { (a: Epoch, s: Short) =>
      a.untilEpochYear(a.epochYear + s.toDouble) shouldEqual s.toDouble
    }
  }

  test("Epoch.until.sanity2") {
    forAll { (s: Epoch.Scheme, d1: LocalDateTime, d2: LocalDateTime) =>
      val Δ1 = s.fromLocalDateTime(d1).untilLocalDateTime(d2)
      val Δ2 = s.fromLocalDateTime(d2).epochYear - s.fromLocalDateTime(d1).epochYear
      Δ1 shouldEqual Δ2
    }
  }

}
