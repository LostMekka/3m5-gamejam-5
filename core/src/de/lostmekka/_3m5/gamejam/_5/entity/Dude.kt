package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.dudeSpeed
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import de.lostmekka._3m5.gamejam._5.dudesight
import de.lostmekka._3m5.gamejam._5.dudeHP
import ktx.math.minus
import ktx.math.times
import ktx.math.vec2

class Dude(world: World) : PhysicsBodyActor(world) {
    private val img = Texture("badlogic.jpg")
    private var hp = dudeHP

    override val body = world.body {
        userData = this@Dude
        box(width = 1f, height = 1f)
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

        if(tower != null){
            val pos = vec2(x, y)
            val target = vec2(tower.position.x, tower.position.y)
            val distanceToTarget = pos.dst(target)
            val distanceThisFrame = dt * dudeSpeed
            if (distanceThisFrame >= distanceToTarget) {
                setPosition(target.x, target.y)

            } else {
                val movement = (target - pos) * (distanceThisFrame / distanceToTarget)
                x += movement.x
                y += movement.y
                }
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
        batch.draw(img, x, y, 96f, 128f)
    }
}