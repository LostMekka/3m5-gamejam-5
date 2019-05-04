package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxScreen
import ktx.graphics.use

class GamePlayScreen : KtxScreen {
    val batch = SpriteBatch()
    private val img = Texture("badlogic.jpg")

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.use {
            batch.draw(img, 0f, 0f)
        }
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}