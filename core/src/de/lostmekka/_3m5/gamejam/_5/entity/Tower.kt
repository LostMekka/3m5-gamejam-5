package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.*
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import kotlin.math.max


class Tower(
    override val world: World,
    position: Vector2,
    private val onAddLaser: (Vector2, Vector2)->Unit
) : PhysicsBodyActor(), Damageble {
    private val texture = Textures.tower
    private var hp = towerHP
    private var cooldown = 0f

    override val body = world.body {
        userData = this@Tower
        box(width = 1f, height = 1.25f)
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

        if (dude != null && cooldown <= 0f) {
            dude.damage(towerAttackDamage)
            cooldown = towerCooldown
            onAddLaser(position, dude.position)
            Sounds.laser.play()
        }
    }

   override fun damage(amount: Int) {
        hp = max(0, hp - amount)
        if (hp <= 0) {
            removeFromStageAndPhysicsWorld()
            Sounds.destroyBuilding.play()
        }
    }
}
