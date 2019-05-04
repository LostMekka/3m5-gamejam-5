package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import de.lostmekka._3m5.gamejam._5.MogulSpeed
import ktx.math.minus
import ktx.math.times
import ktx.math.vec2

/**
 * Copyright 2019 LostMekkaSoft
 */
class Mogul : Actor() {
    var movementTarget: Vector2? = null

    override fun act(delta: Float) {
        movementTarget?.let { target ->
            val pos = vec2(x, y)
            val distanceToTarget = pos.dst(target)
            val distanceThisFrame = delta * MogulSpeed
            if (distanceThisFrame >= distanceToTarget) {
                setPosition(target.x, target.y)
                movementTarget = null
            } else {
                val movement = (target - pos) * (distanceThisFrame / distanceToTarget)
                x += movement.x
                y += movement.y
            }
        }
    }
}