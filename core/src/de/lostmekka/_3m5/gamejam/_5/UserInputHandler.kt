package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import de.lostmekka._3m5.gamejam._5.entity.*
import de.lostmekka._3m5.gamejam._5.helper.randomElement
import ktx.box2d.Query
import ktx.box2d.query

private sealed class State {
    object Nothing : State()
    interface Build
    class BuildCaravanPost(val source: PhysicsBodyActor) : State(), Build
    class BuildTower(val source: PhysicsBodyActor) : State(), Build
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
        val state = state
        when (state) {
            is State.BuildCaravanPost -> previewCaravanPost(batch, state)
            is State.BuildTower -> previewTower(batch, state)
        }
    }

    //

    private fun previewCaravanPost(batch: Batch, state: State.BuildCaravanPost) {
        batch.color = if (canBuildCaravanPost(state)) buildPreviewGood else buildPreviewBad
        batch.draw(Textures.caravanPost, coords.x, coords.y, 1f, 1f)
    }

    private fun previewTower(batch: Batch, state: State.BuildTower) {
        batch.color = if (canBuildTower(state)) buildPreviewGood else buildPreviewBad
        batch.draw(Textures.tower, coords.x, coords.y, 1f, 1.25f)
    }

    private fun canBuildCaravanPost(state: State.BuildCaravanPost) : Boolean {
        val mogulIsNear = mogul.position.dst(coords) < buildCaravanPostMogulDistance

        val distance = buildCaravanPostDistance(state.source)
        var sourceIsNear = state.source.position.dst(coords) < distance

        return mogulIsNear && sourceIsNear
    }

    private fun canBuildTower(state: State.BuildTower) : Boolean {
        val mogulIsNear = mogul.position.dst(coords) < buildCaravanPostMogulDistance
        val caravanPostIsNear = isNear<CaravanPost>(coords, buildCaravanPostPostDistance)
        val magistrateIsNear = isNear<Magistrate>(coords, buildCaravanPostMagistrateDistance)

        return mogulIsNear && (caravanPostIsNear || magistrateIsNear)
    }

    private fun handleSecondaryAction(coords: Vector2) {
        when (state) {
            is State.Nothing -> {
                mogul.movementTarget = coords
                Sounds.mogulMove.randomElement().play()
            }
            is State.Build -> state = State.Nothing
        }
    }

    private fun handlePrimaryAction(coords: Vector2) {
        val actor: Actor? = stage.hit(coords.x, coords.y, true)
        val state = state
        when (state) {
            State.Nothing -> {
                when (actor) {
                    is Magistrate -> {
                        this.state = State.BuildCaravanPost(actor)
                        Sounds.initiateBuildMode.play()
                    }
                    is CaravanPost -> {
                        this.state = State.BuildCaravanPost(actor)
                        Sounds.initiateBuildMode.play()
                    }
                    is Mogul -> {
                        this.state = State.BuildTower(actor)
                        Sounds.initiateBuildMode.play()
                    }
                }
            }
            is State.BuildCaravanPost -> if (canBuildCaravanPost(state)) {
                buildCaravanPost(coords, actor)
                this.state = State.Nothing
            }
            is State.BuildTower -> if (canBuildTower(state)) {
                buildTower(coords)
                this.state = State.Nothing
            }
        }
    }

    private fun buildCaravanPost(coords: Vector2, actor: Actor?) {
        when (actor) {
            is Magistrate -> {
                // TODO: build post connection only
                Sounds.click.play()
            }
            null -> {
                stage.addActor(CaravanPost(world, coords))
                Sounds.build.play()
            }
        }
    }

    private fun buildTower(coords: Vector2) {
        stage.addActor(createTower(coords))
        Sounds.build.play()
    }

    private inline fun <reified T> isNear(coords: Vector2, dst: Float): Boolean {
        val acc = mutableListOf<T>()
        world.query(
            coords.x - dst, coords.y - dst, coords.x + dst, coords.y + dst
        ) {
            if (it.body.userData is T) acc.add(it.body.userData as T)
            Query.CONTINUE
        }
        return acc.size > 0
    }

    private fun buildCaravanPostDistance(source: PhysicsBodyActor) = when (source) {
        is CaravanPost -> buildCaravanPostPostDistance
        is Magistrate -> buildCaravanPostMagistrateDistance
        else -> 0f
    }
}