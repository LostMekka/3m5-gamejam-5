package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.Sounds
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.caravanPostHP
import de.lostmekka._3m5.gamejam._5.drawTexture
import ktx.box2d.body
import kotlin.math.max
import ktx.math.vec2

/**
 * Copyright 2019 LostMekkaSoft
 */

class CaravanPost(override val world: World, position: Vector2) : PhysicsBodyActor(), Connectable, Damageble {
    override val connections = mutableListOf<Connectable>()
    override val isBase: Boolean = false
    override val body = world.body {
        userData = this@CaravanPost
        box(width = 1f, height = 1f)
    }
    override val connectionOrigin get() = vec2(x + originX, y + originY)

    private var hp = caravanPostHP

    private val texture = Textures.caravanPost

    init {
        width = 1f
        height = 1f
        this.position = position
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.color = color
        drawTexture(batch, texture)
    }

   override fun damage(amount: Int) {
        hp = max(0, hp - amount)
        if (hp <= 0) {
            onDeath()
            handleDeath()
        }
    }

    override fun onDeath() {
        removeFromStageAndPhysicsWorld()
        Sounds.destroyBuilding.play()
    }
}