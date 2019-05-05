package de.lostmekka._3m5.gamejam._5.helper

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage

/**
 * Copyright 2019 LostMekkaSoft
 */

fun <T: Actor> Stage.addActor(actor: T, init: T.()->Unit) {
    actor.init()
    addActor(actor)
}
