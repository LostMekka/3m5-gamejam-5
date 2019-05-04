package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query

class Tower(private val world: World) {
    private val body = world.body {
        userData = this
        box(width = 1f, height = 1f)
    }

    var position
        get() = body.position
        set(v) { body.position.set(v) }
}

class Dude(private val world: World) : Actor() {
    private val img = Texture("badlogic.jpg")

    private val body = world.body {
        userData = this
        box(width = 1f, height = 1f)
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        body.position.set(x, y)
    }

    override fun act(dt: Float) {
        val towers = mutableListOf<Tower>()

        world.query(x - sight, y - sight, x + sight, y + sight) {
            val tower = it.body.userData as? Tower
            if (tower != null) {
                towers.add(tower)
            }
            Query.CONTINUE
        }

        val tower = towers.minBy {
            it.position.dst2(body.position)
        }

        println(tower)
    }

    override fun draw (batch: Batch, parentAlpha: Float) {
        batch.draw(img, x, y, 96f, 128f);
    }

    // call before act()
    fun updatePhysics() {
        x = body.position.x;
        y = body.position.y;
    }
}