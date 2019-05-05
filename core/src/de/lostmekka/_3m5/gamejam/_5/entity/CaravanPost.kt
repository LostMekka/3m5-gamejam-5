package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.drawTexture
import ktx.box2d.body

/**
 * Copyright 2019 LostMekkaSoft
 */

class CaravanPost(override val world: World, position: Vector2) : PhysicsBodyActor(), Connectable {
    override val connections = mutableListOf<Connectable>()
    override val isBase: Boolean = false
    override val body = world.body {
        userData = this@CaravanPost
        box(width = 1f, height = 1f)
    }

    private val texture = Textures.caravanPost

    init {
        width = 1f
        height = 1f
        this.position = position
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawTexture(batch, texture)
    }
}