package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Screen
import ktx.app.KtxGame

class JamGame : KtxGame<Screen>() {
    override fun create() {
        addScreen(GamePlayScreen())
        setScreen<GamePlayScreen>()
    }
}
