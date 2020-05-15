// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.geom
package arb

import gsp.math.geom.syntax.all._
import gsp.math.syntax.int._
import gsp.math.{Angle, Offset}
import gsp.math.arb._
import org.scalacheck._
import org.scalacheck.Arbitrary._

trait ArbShapeExpression {

  import ArbAngle._
  import ArbOffset._

  // Generates arbitrary width and height of a rectangle contained in a circle
  // with the given radius.
  private def genCenteredBoxOf(radius: Angle): Gen[(Angle, Angle)] =
      Gen.choose(0, 90).map { d =>
        val r   = d.degrees.toDoubleRadians
        val µas = radius.toMicroarcseconds
        val w   = Angle.fromMicroarcseconds((2 * Math.cos(r) * µas).round)
        val h   = Angle.fromMicroarcseconds((2 * Math.sin(r) * µas).round)
        (w, h)
      }

  def genCenteredEllipseOf(radius: Angle): Gen[ShapeExpression] =
    genCenteredBoxOf(radius).map { case (w, h) => ShapeExpression.centeredEllipse(w, h) }

  def genCenteredPolygonOf(radius: Angle): Gen[ShapeExpression] =
    Gen.choose(3, 10).map(ShapeExpression.regularPolygon(radius, _))

  def genCenteredRectangleOf(radius: Angle): Gen[ShapeExpression] =
    genCenteredBoxOf(radius).map { case (w, h) => ShapeExpression.centeredRectangle(w, h) }

  def genCenteredShapeOf(radius: Angle): Gen[ShapeExpression] =
    Gen.oneOf(genCenteredPolygonOf(radius), genCenteredRectangleOf(radius), genCenteredEllipseOf(radius))

  private def withArbitraryRadius(g: Angle => Gen[ShapeExpression]): Gen[ShapeExpression] =
      for {
        r <- Gen.choose(1, 400).map(_.arcsec)
        s <- g(r)
      } yield s

  val genCenteredEllipse: Gen[ShapeExpression] =
    withArbitraryRadius(genCenteredEllipseOf)

  val genCenteredPolygon: Gen[ShapeExpression] =
    withArbitraryRadius(genCenteredPolygonOf)

  val genCenteredRectangle: Gen[ShapeExpression] =
    withArbitraryRadius(genCenteredRectangleOf)

  val genCenteredShape: Gen[ShapeExpression] =
    Gen.oneOf(genCenteredEllipse, genCenteredPolygon, genCenteredRectangle)

  def withPerturbation(g: Gen[ShapeExpression]): Gen[ShapeExpression] =
    for {
      s <- g
      o <- arbitrary[Offset]
      a <- arbitrary[Angle]
    } yield (s ⟲ a) ↗ o

  val genEllipse: Gen[ShapeExpression] =
    withPerturbation(genCenteredEllipse)

  val genPolygon: Gen[ShapeExpression] =
    withPerturbation(genCenteredPolygon)

  val genRectangle: Gen[ShapeExpression] =
    withPerturbation(genCenteredRectangle)

  val genShape: Gen[ShapeExpression] =
    Gen.oneOf(genEllipse, genPolygon, genRectangle)

  // Not implicit.  This is a single arbitrary shape, not in any way a
  // combination of shapes, so it isn't really an "arbitrary ShapeExpression".
  val arbShape: Arbitrary[ShapeExpression] =
    Arbitrary(genShape)

}

object ArbShapeExpression extends ArbShapeExpression