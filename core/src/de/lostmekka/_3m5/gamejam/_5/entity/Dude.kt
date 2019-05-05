package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.drawTexture
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import de.lostmekka._3m5.gamejam._5.dudesight
import de.lostmekka._3m5.gamejam._5.dudeHP

class Dude(world: World, position: Vector2) : PhysicsBodyActor(world) {
    private val texture = Textures.mogul
    private var hp = dudeHP

    override val body = world.body {
        userData = this@Dude
        box(width = 1f, height = 1f)
    }

    init {
        width = 1f
        height = 1.25f
        this.position = position
    }

    override fun act(dt: Float) {
        super.act(dt)

        val towers = mutableListOf<Tower>()

        world.query(x - dudesight, y - dudesight, x + dudesight, y + dudesight) {
            val tower = it.body.userData as? Tower
            if (tower != null) {
                towers.add(tower)
            }
            Query.CONTINUE
        }

        val tower = towers.minBy {
            Vector2(it.x, it.y).dst2(body.position)
        }
    }

    fun attacked() {
        hp--

        if (hp < 1) {
            world.destroyBody(body)
            this@Dude.remove()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawTexture(batch, texture)
    }
}