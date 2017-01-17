package com.avaglir.knave.util

/**
  * Calculates shadows from a given point via raycasting.
  */
object ShadowRaycast {
  /**
    * Build the set of all locations that are visible from the camera location.
    * @param camera The location of the virtual camera rendering the scene.
    * @param fov The field of view of the camera.
    * @param checkVisibility A function that checks whether the square occludes vision. Returns true if the square does
    *                        *not* occlude.
    * @return The set of locations visible from the camera.
    */
  def calculate(camera: Vector2[Int], fov: Int, checkVisibility: (Vector2[Int]) => Boolean): List[Vector2[Int]] = {
    circle_simple(camera, fov).filter { elem =>

      val line = bresenhamLine(camera, elem)
      line.forall(checkVisibility)
    }
  }
}
