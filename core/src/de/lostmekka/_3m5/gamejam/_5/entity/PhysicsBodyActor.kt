package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.math.vec2

abstract class PhysicsBodyActor : Actor() {
    abstract val world: World
    abstract val body: Body

    var isRemoved = false
        private set

    var position: Vector2
        get() = body.position
        set(v) {
            x = v.x
            y = v.y
            body.setTransform(v.x, v.y, body.angle)
        }

    override fun setPosition(x: Float, y: Float) {
        position = vec2(x, y)
    }

    /**
     * override to clean up after the entity got removed from the world
     */
    open fun onRemove() {}

    /**
     * call before act()
     */
    fun updatePhysics() {
        x = body.position.x
        y = body.position.y
    }

    fun removeFromStageAndPhysicsWorld() {
        if (isRemoved) return
        remove()
        world.destroyBody(body)
        isRemoved = true
        onRemove()
    }
}
