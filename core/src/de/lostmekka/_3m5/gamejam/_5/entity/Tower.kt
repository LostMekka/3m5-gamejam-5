package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import de.lostmekka._3m5.gamejam._5.towerHP
import de.lostmekka._3m5.gamejam._5.towerAttackRadius


class Tower(world: World) : PhysicsBodyActor(world) {
    private val img = Texture("badlogic.jpg")
    private var hp = towerHP

    override val body = world.body {
        userData = this@Tower
        box(width = 1f, height = 1f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(img, x, y)
    }

    override fun act(dt: Float) {
        val dudes = mutableListOf<Dude>()


        world.query(x - towerAttackRadius, y - towerAttackRadius, x + towerAttackRadius,
                y + towerAttackRadius) {
            val dude = it.body.userData as? Dude
            if (dude != null) {
                dudes.add(dude)
            }
            Query.CONTINUE
        }

        val dude = dudes.minBy {
            Vector2(it.x, it.y).dst2(body.position)
        }

        if (dude != null) {
            while (dude.position.x <= x + towerAttackRadius && dude.position.x >= x - towerAttackRadius
                    && dude.position.y <= y + towerAttackRadius && dude.position.y >= y - towerAttackRadius){
                dude.attacked()
            }
        }
    }

    fun attacked(){
        hp--

        if(hp < 1){
            world.destroyBody(body)
            this@Tower.remove()
        }
    }
}
