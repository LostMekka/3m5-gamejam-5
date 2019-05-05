package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import de.lostmekka._3m5.gamejam._5.entity.Dude
import de.lostmekka._3m5.gamejam._5.entity.Mogul
import de.lostmekka._3m5.gamejam._5.entity.PhysicsBodyActor
import de.lostmekka._3m5.gamejam._5.entity.Tower
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.graphics.use
import ktx.math.vec2


class GamePlayScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val world = createWorld()

    private val groundAtlas = Texture("ground.png")
        .also { it.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest) }
        .splitSpriteSheet(16, 16)
    private val ground = Ground(groundAtlas)

    private val viewport = ExtendViewport(40f, 20f, OrthographicCamera().also { it.zoom = 0.5f })
    private val mogul = Mogul(world, vec2())
    private val stage = Stage(viewport).apply {
        addActor(mogul)
        addActor(Tower(world, vec2(5f, 2f)))
        addActor(Dude(world, vec2(-2f, 1f)))
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