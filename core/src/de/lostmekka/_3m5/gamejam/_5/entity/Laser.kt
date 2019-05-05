package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

/**
 * Copyright 2019 LostMekkaSoft
 */

class Laser(
    val start: Vector2,
    val end: Vector2,
    var lifetime: Float,
    val color: Color
) {
    val isDone get() = lifetime <= 0
    fun update(delta: Float) {
        lifetime -= delta
    }
    fun draw(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = color
        shapeRenderer.line(start, end)
    }
}