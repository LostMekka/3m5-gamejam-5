package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.*
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2

class Dude(override val world: World, position: Vector2 = Vector2.Zero) : PhysicsBodyActor() {
    private val dress = Textures.dress
    private val hat = Textures.hat
    private val headhands = Textures.headhands

    private var hp = dudeHP
    private var cooldown = 0f

    override val body = world.body {
        userData = this@Dude
        box(width = 1f, height = 1f)
    }

    init {
        width = 1f
        height = 1.25f
        this.position = position
    }

    var hatColor = Color(MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1f)
    var dressColor = Color(1f, 1f, 1f, 1f)
    var skinColor = Color(1f, 1f, 1f, 1f)

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


        cooldown -= dt


        if (tower != null) {
            val pos = vec2(x, y)
            val target = tower.position
            val distanceToTarget = pos.dst(target)
            val distanceThisFrame = dt * dudeSpeed
            if (distanceThisFrame >= distanceToTarget) {
                position = vec2(target.x, target.y)
                if(isInRange(tower) && cooldown<=0f){
                    tower.attacked()
                    cooldown = dudeMeleeCooldown
                }

            } else {
                position += (target - pos) * (distanceThisFrame / distanceToTarget)
            }
        }
    }

    fun attacked() {
        if (hp > 0) hp--
        if (hp <= 0) removeFromStageAndPhysicsWorld()
    }

    private fun isInRange(tower: Tower) =
            tower.x - x in -dudeMeleeRadius..dudeMeleeRadius && tower.y - y in -dudeMeleeRadius..dudeMeleeRadius

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.color = dressColor
        drawTexture(batch, dress)

        val headIndex = 1
        batch.color = skinColor
        drawTexture(batch, headhands[headIndex])

        batch.color = hatColor
        drawTexture(batch, hat[headIndex])
    }
}