package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import de.lostmekka._3m5.gamejam._5.entity.Mogul
import ktx.app.KtxScreen
import ktx.graphics.circle
import ktx.graphics.use

const val MogulRadius = 0.4f

class GamePlayScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()

    private val img = Texture("badlogic.jpg")

    private val viewport = ExtendViewport(20f, 10f)
    private val mogul = Mogul()
    private val stage = Stage(viewport).apply {
        addActor(mogul)
    }

    private fun handleInput(delta: Float) {
        // TODO
    }

    private fun update(delta: Float) {
        stage.camera.position.set(0f, 0f, stage.camera.position.z)
        stage.act(delta)
    }

    private fun draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()

        shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
            it.projectionMatrix = stage.camera.projection
            it.setAutoShapeType(true)
            it.circle(mogul.x, mogul.y, MogulRadius, 20)
        }
    }

    override fun render(delta: Float) {
        handleInput(delta)
        update(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}