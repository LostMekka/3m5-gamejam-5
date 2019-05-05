package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.MogulSpeed
import de.lostmekka._3m5.gamejam._5.drawTexture
import de.lostmekka._3m5.gamejam._5.splitSpriteSheet
import de.lostmekka._3m5.gamejam._5.toAnimation
import ktx.box2d.body
import ktx.math.minus
import ktx.math.times
import ktx.math.vec2

/**
 * Copyright 2019 LostMekkaSoft
 */
class Mogul(world: World, position: Vector2) : PhysicsBodyActor(world) {
    override val body: Body = world.body {
        userData = this@Mogul
        box(width = 1f, height = 1f)
    }

    private val animation = Texture("mogul.png")
        .also { it.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest) }
        .splitSpriteSheet(24, 32)
        .toAnimation(0.5f)

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
                setPosition(target.x, target.y)
                movementTarget = null
            } else {
                val movement = (target - pos) * (distanceThisFrame / distanceToTarget)
                x += movement.x
                y += movement.y
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawTexture(batch, animation.currentRegion)
    }
}
