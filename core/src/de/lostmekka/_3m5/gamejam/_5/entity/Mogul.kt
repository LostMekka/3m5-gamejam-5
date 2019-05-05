package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.MogulSpeed
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.drawTexture
import de.lostmekka._3m5.gamejam._5.mogulHP
import ktx.box2d.body
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import kotlin.math.max

/**
 * Copyright 2019 LostMekkaSoft
 */
class Mogul(override val world: World, position: Vector2) : PhysicsBodyActor(), Damageble {
    override val body: Body = world.body {
        userData = this@Mogul
        box(width = 0.75f, height = 1f)
    }

    private var hp = mogulHP
    private val animation = Textures.mogulAtlas

    var movementTarget: Vector2? = null

    init {
        width = 0.75f
        height = 1f
        this.position = position
    }

    override fun act(delta: Float) {
        animation.update(delta)

        movementTarget?.let { target ->
            val pos = vec2(x, y)
            val distanceToTarget = pos.dst(target)
            val distanceThisFrame = delta * MogulSpeed
            if (distanceThisFrame >= distanceToTarget) {
                position = vec2(target.x, target.y)
                movementTarget = null
            } else {
                position += (target - pos) * (distanceThisFrame / distanceToTarget)
            }
        }
    }

    override fun damage(amount: Int) {
        hp = max(0, hp - amount)
        if (hp <= 0) removeFromStageAndPhysicsWorld()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.color = color
        drawTexture(batch, animation.currentRegion)
    }
}
