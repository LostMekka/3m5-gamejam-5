package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import de.lostmekka._3m5.gamejam._5.entity.Dude
import ktx.math.vec2

class DudeSpawner(pos: Vector2, val createDude: () -> Dude) : Actor() {
    var cooldown = 0f

    override fun act(dt: Float) {
        cooldown -= dt
        if (cooldown <= 0f) {
            cooldown = dudeSpawnerCooldown
            spawn()
        }
    }

    init {
        x = pos.x
        y = pos.y
    }

    fun spawn() {
        stage.addActor(createDude().also {
            it.position = vec2(x, y)
        })
    }
}