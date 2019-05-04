package de.lostmekka._3m5.gamejam._5.entity

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor

abstract class PhysicsBodyActor(val world: World) : Actor() {
    abstract val body: Body

    var position
        get() = body.position
        set(v) {
            body.setTransform(v.x, v.y, body.angle)
        }

    // call before act()
    fun updatePhysics() {
        x = body.position.x;
        y = body.position.y;
    }
}