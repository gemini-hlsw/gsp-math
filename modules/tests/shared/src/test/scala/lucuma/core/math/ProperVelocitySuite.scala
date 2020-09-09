// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.core.math
import cats.kernel.laws.discipline._
import lucuma.core.math.arb.ArbProperVelocity._
import lucuma.core.optics.laws.discipline.SplitMonoTests
import munit.DisciplineSuite

final class ProperVelocitySuite extends DisciplineSuite {

  // Laws
  checkAll("Order[ProperVelocity]", OrderTests[ProperVelocity].order)
  checkAll("Monoid[ProperVelocity]", MonoidTests[ProperVelocity].monoid)

  checkAll("milliarcsecondsPerYear", SplitMonoTests(ProperVelocity.milliarcsecondsPerYear).splitMono)

}
