package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query

class Dude(world: World) : PhysicsBodyActor(world) {
    private val img = Texture("badlogic.jpg")
    private var dudeHP = 10

    override val body = world.body {
        userData = this@Dude
        box(width = 1f, height = 1f)
    }

    val sight = 16f

    override fun act(dt: Float) {
        super.act(dt)

        val towers = mutableListOf<Tower>()

        world.query(x - sight, y - sight, x + sight, y + sight) {
            val tower = it.body.userData as? Tower
            if (tower != null) {
                towers.add(tower)
            }
            Query.CONTINUE
        }

        val tower = towers.minBy {
            Vector2(it.x, it.y).dst2(body.position)
        }

        if (tower != null) {
            println("$tower")
        }
    }

    fun attacked() {
        dudeHP--

        if (dudeHP < 1) {
            world.destroyBody(body)
            this@Dude.remove()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(img, x, y, 96f, 128f)
    }
}