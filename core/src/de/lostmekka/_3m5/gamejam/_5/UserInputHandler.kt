package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import de.lostmekka._3m5.gamejam._5.entity.CaravanPost
import de.lostmekka._3m5.gamejam._5.entity.Magistrate
import de.lostmekka._3m5.gamejam._5.entity.Mogul
import de.lostmekka._3m5.gamejam._5.entity.Tower
import de.lostmekka._3m5.gamejam._5.helper.randomElement

private sealed class State {
    object Nothing : State()
    interface Build {
        object CaravanPost : State(), Build
        object Tower : State(), Build
    }
}

class UserInputHandler(
    private val mogul: Mogul,
    private val stage: Stage,
    private val world: World,
    private val createTower: (pos: Vector2) -> Tower
) {
    private var state: State = State.Nothing
    private var right = false
    private var left = false
    private var coords: Vector2 = Vector2.Zero

    fun handleInput(coords: Vector2) {
        val newRight = Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
        val newLeft = Gdx.input.isButtonPressed(Input.Buttons.LEFT)

        when {
            newRight && !right -> handleSecondaryAction(coords)
            newLeft && !left -> handlePrimaryAction(coords)
        }

        right = newRight
        left = newLeft
        this.coords = coords
    }

    fun draw(batch: Batch) {
        batch.color = Color(0f, 1f, 0.3f, 0.5f)
        when (state) {
            State.Build.CaravanPost -> previewCaravanPoint(batch)
            State.Build.Tower -> previewTower(batch)
        }
    }

    private fun previewCaravanPoint(batch: Batch) {
        batch.draw(Textures.caravanPost, coords.x, coords.y, 1f, 1f)
    }

    private fun previewTower(batch: Batch) {
        batch.draw(Textures.tower, coords.x, coords.y, 1f, 1.25f)
    }

    private fun handleSecondaryAction(coords: Vector2) {
        when (state) {
            State.Nothing -> {
                mogul.movementTarget = coords
                Sounds.mogulMove.randomElement().play()
            }
            is State.Build -> state = State.Nothing
        }
    }

    private fun handlePrimaryAction(coords: Vector2) {
        val actor = stage.hit(coords.x, coords.y, true)
        when (state) {
            State.Nothing -> {
                when (actor) {
                    is Magistrate, is CaravanPost -> {
                        state = State.Build.CaravanPost
                        Sounds.initiateBuildMode.play()
                    }
                    is Mogul -> {
                        state = State.Build.Tower
                        Sounds.initiateBuildMode.play()
                    }
                }
            }
            is State.Build -> {
                when {
                    actor != null && state is State.Build.CaravanPost -> {
                        // TODO: build post connection only
                    }
                    state is State.Build.CaravanPost -> {
                        buildCaravanPoint(coords)
                        state = State.Nothing
                    }
                    state is State.Build.Tower -> {
                        state = State.Nothing
                        buildTower(coords)
                    }
                }
            }
        }
    }

    private fun buildCaravanPoint(coords: Vector2) {
        stage.addActor(CaravanPost(world, coords))
    }

    private fun buildTower(coords: Vector2) {
        stage.addActor(createTower(coords))
    }
}