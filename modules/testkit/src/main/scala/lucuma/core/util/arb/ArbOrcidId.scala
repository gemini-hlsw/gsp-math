// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.core.util.arb

import cats.implicits._
import lucuma.core.model.OrcidId
import org.scalacheck._
import org.scalacheck.cats.implicits._

trait ArbOrcidId {

  def digits(n: Int): Gen[String] =
    Gen.numChar.replicateA(n).map(_.mkString)

  implicit val ArbOrcidId: Arbitrary[OrcidId] =
    Arbitrary {
      (digits(4), digits(4), digits(4), digits(3)).mapN { case (a, b, c, d) =>
        val x = OrcidId.checkDigit(a + b + c + d)
        OrcidId.fromString(s"https://orcid.org/$a-$b-$c-$d$x") match {
          case Left(s)  => sys.error(s)
          case Right(o) => o
        }
      }
    }

  implicit val CogOrcidId: Cogen[OrcidId] =
    Cogen[String].contramap(_.value.toString)

}

object ArbOrcidId extends ArbOrcidId
