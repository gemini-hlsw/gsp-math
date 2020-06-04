// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.geom
package jts

import cats.syntax.all._
import gsp.math.geom.jts.syntax.all._
import gsp.math.Offset
import gsp.math.geom.ShapeExpression._
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.util.AffineTransformation
import org.locationtech.jts.util.GeometricShapeFactory

import java.util.Collections

/**
 * JTS shape interpreter.
 */
object JtsShapeInterpreter extends ShapeInterpreter {

  private val EmptyGeometry: Geometry =
    Jts.geometryFactory.buildGeometry(Collections.emptyList[Geometry])

  private def toGeometry(e: ShapeExpression): Geometry = {
    // The Geometry that comes out of this must have non-zero area or else
    // intersections raise exceptions.  Switch to EmptyGeometry if the area is
    // non-zero.
    def safeRectangularBoundedShape(a: Offset, b: Offset)(f: GeometricShapeFactory => Geometry): Geometry =
      if ((a.p === b.p) || (a.q === b.q)) EmptyGeometry
      else f(a.shapeFactory(b))

    def safePolygon(os: List[Offset]): Geometry =
      // We need at least 3 distinct points.
      if (os.toSet.size < 3)
        EmptyGeometry
      else {
        // The first and last point must be the same.
        val os2 = if (os.head === os.last) os else os.last :: os
        Jts.geometryFactory.createPolygon(os2.map(_.coordinate).toArray)
      }

    e match {
      // Constructors
      case Empty              => EmptyGeometry
      case Ellipse(a, b)      => safeRectangularBoundedShape(a, b) {_.createEllipse}
      case Polygon(os)        => safePolygon(os)
      case Rectangle(a, b)    => safeRectangularBoundedShape(a, b) {_.createRectangle}

      // Combinations
      case Difference(a, b)   => toGeometry(a).difference(toGeometry(b))
      case Intersection(a, b) => toGeometry(a).intersection(toGeometry(b))
      case Union(a, b)        => toGeometry(a).union(toGeometry(b))

      // Transformations
      case Rotate(e, a)       =>
        AffineTransformation
          .rotationInstance(a.toDoubleRadians)
          .transform(toGeometry(e))

      case Translate(e, o)    =>
        val c = o.coordinate
        AffineTransformation
          .translationInstance(c.x, c.y)
          .transform(toGeometry(e))
    }
  }

  override def interpret(e: ShapeExpression): Shape =
    JtsShape(toGeometry(e))
}


object interpreter {

  implicit val value: ShapeInterpreter =
    JtsShapeInterpreter

}

