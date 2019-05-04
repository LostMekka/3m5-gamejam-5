package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.box2d.Query
import ktx.box2d.body
import ktx.box2d.query


class Tower(private val world : World) : Actor() {
    private val img = Texture("badlogic.jpg")

    private val body = world.body{
        userData = this@Tower
        box(width = 1f, height = 1f)
    }

    override fun setPosition (x: Float, y: Float){
        super.setPosition(x, y)
        body.setTransform(x, y, body.angle)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
            batch.draw(img, x, y)
    }

    override fun act(dt: Float){
        val dudes = mutableListOf<Dude>()
        val attack_radius = 10f

        world.query(x - attack_radius, y - attack_radius, x + attack_radius, y + attack_radius){
            val dude = it.body.userData as? Dude
            if (dude != null) {
                dudes.add(dude)
            }
            Query.CONTINUE
        }

        val tower = dudes.minBy{
            Vector2(it.x, it.y).dst2(body.position)
        }
    }
}
