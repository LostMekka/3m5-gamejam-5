package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.*
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query


class Tower(override val world: World, position: Vector2) : PhysicsBodyActor() {
    private val texture = Textures.tower
    private var hp = towerHP
    private var cooldown = 0f

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
        batch.color = color
        drawTexture(batch, texture)
    }

    override fun act(dt: Float) {
        val dudes = mutableListOf<Dude>()

        world.query(
            x - towerAttackRadius, y - towerAttackRadius, x + towerAttackRadius,
            y + towerAttackRadius
        ) {
            val dude = it.body.userData as? Dude
            if (dude != null) {
                dudes.add(dude)
            }
            Query.CONTINUE
        }

        val dude = dudes.minBy {
            Vector2(it.x, it.y).dst2(body.position)
        }

        cooldown -= dt

        if (dude != null) {
            if (cooldown <= 0f) {
                dude.attacked()
                cooldown = towerCooldown
            }
        }
    }

    private fun isInRange(dude: Dude) =
        dude.x - x in -towerAttackRadius..towerAttackRadius && dude.y - y in -towerAttackRadius..towerAttackRadius

    fun attacked() {
        if (hp > 0) hp--
        if (hp <= 0) removeFromStageAndPhysicsWorld()
    }
}
