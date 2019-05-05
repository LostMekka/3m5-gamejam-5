package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import de.lostmekka._3m5.gamejam._5.entity.CaravanPost
import de.lostmekka._3m5.gamejam._5.entity.Magistrate
import de.lostmekka._3m5.gamejam._5.entity.Mogul
import de.lostmekka._3m5.gamejam._5.entity.Tower
import de.lostmekka._3m5.gamejam._5.helper.randomElement

enum class PrimaryActionState {
    Nothing,
    BuildCaravanPoint,
    BuildTower
}

class UserInputHandler(
    private val mogul: Mogul,
    private val stage: Stage,
    private val world: World,
    private val createTower: (pos: Vector2) -> Tower
) {
    private var state = PrimaryActionState.Nothing
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

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (state) {
            PrimaryActionState.BuildCaravanPoint -> previewCaravanPoint(batch)
            PrimaryActionState.BuildTower -> previewTower(batch)
        }
    }

    //

    private fun previewCaravanPoint(batch: Batch) {
        batch.draw(Textures.caravanPost, coords.x, coords.y, 1f, 1f)
    }

    private fun previewTower(batch: Batch) {
        batch.draw(Textures.tower, coords.x, coords.y, 1f, 1.25f)
    }

    private fun handleSecondaryAction(coords: Vector2) {
        mogul.movementTarget = coords
        Sounds.mogulMove.randomElement().play()
    }

    private fun handlePrimaryAction(coords: Vector2) {
        val actor = stage.hit(coords.x, coords.y, true)
        if (actor != null) {
            actOnActor(actor, coords)
        } else {
            actOnEmptySpace(coords)
        }
    }

    private fun actOnActor(actor: Actor, coords: Vector2) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (state) {
            PrimaryActionState.Nothing -> when (actor) {
                is Magistrate -> {
                    state = PrimaryActionState.BuildCaravanPoint
                }
                is CaravanPost -> {
                    state = PrimaryActionState.BuildCaravanPoint
                }
                is Mogul -> {
                    state = PrimaryActionState.BuildTower
                }
            }
        }
    }

    private fun actOnEmptySpace(coords: Vector2) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (state) {
            PrimaryActionState.BuildCaravanPoint -> {
                state = PrimaryActionState.Nothing
                buildCaravanPoint(coords)
            }
            PrimaryActionState.BuildTower -> {
                state = PrimaryActionState.Nothing
                buildTower(coords)
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