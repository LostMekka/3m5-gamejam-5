package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.Sounds
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.drawTexture
import de.lostmekka._3m5.gamejam._5.magistrateHP
import ktx.box2d.body
import ktx.math.vec2
import kotlin.math.max

/**
 * Copyright 2019 LostMekkaSoft
 */

class Magistrate(override val world: World, position: Vector2) : PhysicsBodyActor(), Connectable, Damageble {
    override val connections = mutableListOf<Connectable>()
    override val isBase: Boolean = true
    override val body = world.body {
        userData = this@Magistrate
        box(width = 2f, height = 2f)
    }
    override val connectionOrigin get() = vec2(x + originX, y + originY)

    var isActive = false
        set(value) {
            field = value
            color = if (value) Color.WHITE else Color.GRAY
        }

    var hp = magistrateHP
    private val texture = Textures.magistrate

    init {
        width = 2f
        height = 2f
        this.position = position
    }

    override fun damage(amount: Int) {
        hp = max(0, hp - amount)
        if (hp <= 0) {
            isActive = false
            clearConnections()
            Sounds.destroyBuilding.play()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.color = color
        drawTexture(batch, texture)
    }
}