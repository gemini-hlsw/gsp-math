// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.skycalc.solver

import java.time.Duration
import gsp.math.Declination
import gsp.math.skycalc.SkyCalcResults
import gsp.math.HourAngle

/**
  * Convenience class to find a [[Schedule]] meeting a [[Constraint]] from [[Samples]] at a given [[Interval]].
  *
  * @tparam S [[SolverStrategy]] to use
  * @tparam R [[RoundStrategy]] to use
  * @tparam T the type of results held by the [[Samples]]
  * @tparam A the type on which the [[Constraint]] is checked (usually obtainable from <code>T</code>)
  */
class ConstraintSolver[S, R, T, A](
  constraint:      Constraint[T, A],
  tolerance:       Duration = Duration.ofSeconds(30)
)(implicit solver: Solver[S]) {
  def solve(calc: Samples[T])(interval: Interval)(implicit rounder: SampleRounder[R, A]): Schedule =
    solver.solve(constraint.metAt[R](calc))(interval, tolerance)
}

object ConstraintSolver {

  /**
    * Convenience method to build a [[ConstraintSolver]] by inferring <code>T</code> and <code>A</code> from the [[Constraint]].
    */
  def apply[S, G]: WithStrategies[S, G] = new WithStrategies[S, G]

  protected class WithStrategies[S, G] {
    def apply[T, A](
      constraint:      Constraint[T, A],
      tolerance:       Duration = Duration.ofSeconds(30)
    )(implicit solver: Solver[S]): ConstraintSolver[S, G, T, A] =
      new ConstraintSolver[S, G, T, A](constraint, tolerance)
  }
}

case class ElevationSolver(
  min:       Declination,
  max:       Declination,
  tolerance: Duration = Duration.ofSeconds(30)
) extends ConstraintSolver[
      SolverStrategy.Default,
      RoundStrategy.LinearInterpolating,
      SkyCalcResults,
      Declination
    ](ElevationConstraint(min, max), tolerance)

case class SkyBrightnessSolver(
  min:       Double,
  max:       Double,
  tolerance: Duration = Duration.ofSeconds(30)
) extends ConstraintSolver[
      SolverStrategy.Default,
      RoundStrategy.LinearInterpolating,
      SkyCalcResults,
      Double
    ](SkyBrightnessConstraint(min, max), tolerance)

case class AirmassSolver(min: Double, max: Double, tolerance: Duration = Duration.ofSeconds(30))
    extends ConstraintSolver[
      SolverStrategy.Default,
      RoundStrategy.LinearInterpolating,
      SkyCalcResults,
      (Double, Declination)
    ](AirmassConstraint(min, max), tolerance)

case class HourAngleSolver(
  min:       HourAngle,
  max:       HourAngle,
  tolerance: Duration = Duration.ofSeconds(30)
) extends ConstraintSolver[
      SolverStrategy.Default,
      RoundStrategy.LinearInterpolating,
      SkyCalcResults,
      (HourAngle, Declination)
    ](HourAngleConstraint(min, max), tolerance)