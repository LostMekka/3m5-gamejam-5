package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.Textures
import de.lostmekka._3m5.gamejam._5.drawTexture
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import de.lostmekka._3m5.gamejam._5.towerHP
import de.lostmekka._3m5.gamejam._5.towerAttackRadius


class Tower(world: World, position: Vector2) : PhysicsBodyActor(world) {
    private val texture = Textures.tower
    private var hp = towerHP

    override val body = world.body {
        userData = this@Tower
        box(width = 1f, height = 1f)
    }

    init {
        width = 1f
        height = 1.25f
        this.position = position
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        drawTexture(batch, texture)
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
            if (dude.position.x <= x + towerAttackRadius && dude.position.x >= x - towerAttackRadius
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
