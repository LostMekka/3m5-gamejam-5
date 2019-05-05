package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import de.lostmekka._3m5.gamejam._5.entity.*
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.graphics.use
import ktx.math.vec2


class GamePlayScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val world = createWorld()

    private val groundAtlas = Textures.groundAtlas
    private val ground = Ground(groundAtlas)

    private val viewport = ExtendViewport(40f, 20f, OrthographicCamera().also { it.zoom = 0.5f })
    private val mogul = Mogul(world, vec2())
    private val stage = Stage(viewport).apply {
        addActor(mogul)
        addActor(Tower(world, vec2(5f, 2f)))
        addActor(DudeSpawner(vec2(0f, 5f)) {
            Dude(world).also { it.dressColor = Color.RED }
        })
        addActor(DudeSpawner(vec2(-5f, 3f)) {
            Dude(world).also { it.dressColor = Color.MAROON }
        })
        addActor(DudeSpawner(vec2(8f, -6f)) {
            Dude(world).also { it.dressColor = Color.FIREBRICK }
        })
        addActor(Magistrate(world, vec2(-8.4f, -2.6f)))
        addActor(Magistrate(world, vec2(8.2f, 4.1f)))
    }

    private fun handleInput() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            val xPos = Gdx.input.x.toFloat()
            val yPos = Gdx.input.y.toFloat()
            mogul.movementTarget = stage.screenToStageCoordinates(vec2(xPos, yPos))
        }
    }

    private fun update(delta: Float) {
        stage.camera.position.set(0f, 0f, stage.camera.position.z)

        world.step(delta, 5, 5)
        for (actor in stage.actors) if (actor is PhysicsBodyActor) actor.updatePhysics()
        stage.act(delta)
    }

    private fun draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = viewport.camera.projection
        batch.use {
            // draw textures that are not managed by stage
            ground.draw(batch)
        }

        stage.actors.sort { actorA, actorB ->
            if (actorA.y > actorB.y) -1 else 1
        }

        stage.draw()

        shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
            it.projectionMatrix = stage.camera.projection
            it.setAutoShapeType(true)
            // draw lines and other shapes here. NOTE: actor debug stuff can be drawn by overriding Actor::drawDebug
        }
    }

    override fun render(delta: Float) {
        handleInput()
        update(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        batch.dispose()
    }
}