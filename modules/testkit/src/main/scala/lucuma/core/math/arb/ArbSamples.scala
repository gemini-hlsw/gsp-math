// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.core.math.arb

import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import lucuma.core.math.skycalc.solver.Samples
import scala.collection.immutable.TreeMap
import cats.Eval
import java.time.Instant
import ArbTime._
import org.scalacheck.Cogen

trait ArbSamples {
  import ArbEval._

  def genSamples[A: Arbitrary]: Gen[Samples[A]] =
    arbitrary[TreeMap[Instant, Eval[A]]].map(Samples.fromMap)

  implicit def arbSamples[A: Arbitrary]: Arbitrary[Samples[A]] =
    Arbitrary(genSamples[A])

  implicit def cogenSamples[A: Cogen]: Cogen[Samples[A]] =
    Cogen[Map[Instant, A]].contramap(_.toMap.view.mapValues(_.value).toMap)
}

object ArbSamples extends ArbSamples