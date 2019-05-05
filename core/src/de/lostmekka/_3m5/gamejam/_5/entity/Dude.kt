package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import de.lostmekka._3m5.gamejam._5.dudeHP
import de.lostmekka._3m5.gamejam._5.dudeSpeed
import de.lostmekka._3m5.gamejam._5.dudesight
import de.lostmekka._3m5.gamejam._5.splitSpriteSheet
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2

class Dude(override val world: World, position: Vector2) : PhysicsBodyActor() {
    private val dress = Texture("dude-dress.png")
    private val hat = Texture("dude-hat.png")
            .splitSpriteSheet(24, 32)
    private val headhands = Texture("dude-headhands.png")
            .splitSpriteSheet(24, 32)

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

    var hatColor = Color(MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1f)
    var dressColor = Color(1f, 1f, 1f, 1f)
    var skinColor = Color(1f, 1f, 1f, 1f)

    override fun act(dt: Float) {
        var dt = 0.1f
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

        if (tower != null) {
            val pos = vec2(x, y)
            val target = tower.position
            val distanceToTarget = pos.dst(target)
            val distanceThisFrame = dt * dudeSpeed
            if (distanceThisFrame >= distanceToTarget) {
                position = Vector2(target.x, target.y)

            } else {
                val movement = (target - pos) * (distanceThisFrame / distanceToTarget)
                position += movement
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
        val resetColor = batch.color

        batch.color = dressColor
        batch.draw(dress, x, y, 0.75f, 1f)

        val headIndex = 1
        batch.color = skinColor
        batch.draw(headhands[headIndex], x, y, 0.75f, 1f)

        batch.color = hatColor
        batch.draw(hat[headIndex], x, y, 0.75f, 1f)

        batch.color = resetColor
    }
}