package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Color
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
import kotlin.math.max

class Dude(override val world: World, position: Vector2 = Vector2.Zero) : PhysicsBodyActor() {
    private val dress = Textures.dress
    private val hat = Textures.hat
    private val headhands = Textures.headhands
    private var target: PhysicsBodyActor? = null

    private var hp = dudeHP
    private var cooldown = 0f

    override val body = world.body {
        userData = this@Dude
        box(width = 0.75f, height = 1f)
    }

    init {
        width = 0.75f
        height = 1f
        this.position = position
    }

    var hatColor = Color(MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1f)
    var dressColor = Color(1f, 1f, 1f, 1f)
    var skinColor = Color(1f, 1f, 1f, 1f)

    override fun act(dt: Float) {
        if (stage == null) return
        else {
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


            val caravanposts = mutableListOf<CaravanPost>()
            world.query(x - dudesight, y - dudesight, x + dudesight, y + dudesight) {
                val caravanpost = it.body.userData as? CaravanPost
                if (caravanpost != null) {
                    caravanposts.add(caravanpost)
                }
                Query.CONTINUE
            }

            val caravanpost = caravanposts.minBy {
                Vector2(it.x, it.y).dst2(body.position)
            }

            val magistrates = mutableListOf<Magistrate>()
            world.query(x - dudesight, y - dudesight, x + dudesight, y + dudesight) {
                val magistrate = it.body.userData as? Magistrate
                if (magistrate != null) {
                    magistrates.add(magistrate)
                }
                Query.CONTINUE
            }

            val magistrate = magistrates.minBy {
                Vector2(it.x, it.y).dst2(body.position)
            }
            val mogul = stage.actors.find { it is Mogul } as Mogul?
            if (mogul != null) {
                val moguldist = mogul.position.dst(position)
            }
            if (caravanpost != null && magistrate != null && mogul != null && tower != null) {
                val list = listOf<PhysicsBodyActor>(caravanpost, magistrate, mogul)
                target = list.minBy { Vector2(it.x, it.y).dst2(body.position) }
                if (tower.position.x - x in -towerAttackRadius..towerAttackRadius
                        && tower.position.y - y in -towerAttackRadius..towerAttackRadius) {
                    target = tower
                }
            }

            val target = target

            if (target != null) {
                val pos = vec2(x, y)
                val direction = target.position
                val distanceToTarget = pos.dst(direction)
                val distanceThisFrame = dt * dudeSpeed
                if (distanceThisFrame >= distanceToTarget) {
                    position = vec2(direction.x, direction.y)
                    if (isInRange(target) && cooldown <= 0f) {
                        if (target is Damageble) target.damage(dudeMeleeDamage)
                        cooldown = dudeMeleeCooldown
                    }
                } else {
                    position += (direction - pos) * (distanceThisFrame / distanceToTarget)
                }
            }

        }
    }


    fun damage(amount: Int) {
        hp = max(0, hp - amount)
        if (hp <= 0){
            slaves++
            removeFromStageAndPhysicsWorld()
        }
    }

    private fun isInRange(target: PhysicsBodyActor) =
        target.x - x in -dudeMeleeRadius..dudeMeleeRadius && target.y - y in -dudeMeleeRadius..dudeMeleeRadius

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