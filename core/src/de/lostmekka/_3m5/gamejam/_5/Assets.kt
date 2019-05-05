package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.collections.toGdxArray

/**
 * Copyright 2019 LostMekkaSoft
 */

object Textures {
    val magistrate by lazy { Texture("magistrate.png") }
    val caravanPost by lazy { Texture("caravan post.png") }
    val tower by lazy { Texture("tower.png") }
    val groundAtlas by lazy {
        Texture("ground.png")
            .filterNearest()
            .splitSpriteSheet(16, 16)
    }
    val mogulAtlas by lazy {
        Texture("mogul.png")
            .filterNearest()
            .splitSpriteSheet(24, 32)
            .toAnimation(0.5f)
    }
    val dress = Texture("dude-dress.png")
    val hat = Texture("dude-hat.png")
        .splitSpriteSheet(24, 32)
    val headhands = Texture("dude-headhands.png")
        .splitSpriteSheet(24, 32)
}

object Sounds {
    val click by lazy { sound("click.ogg") }
    val initiateBuildMode by lazy { sound("switch-to-construction-mode.ogg") }
}

object Music {
    // TODO: add music here
}

class SoundWithVolume(val sound: Sound, val volume: Float) {
    fun play() = sound.play(volume)
}

fun sound(path: String, volume: Float = 1f) = SoundWithVolume(
    Gdx.audio.newSound(Gdx.files.internal("sounds/$path")),
    volume
)

fun music(path: String, volume: Float = 1f) =
    Gdx.audio.newMusic(Gdx.files.internal(path)).also {
        it.volume = volume
        it.isLooping = true
    }

fun Texture.filterNearest(): Texture {
    setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    return this
}

fun Texture.splitSpriteSheet(spriteWidth: Int, spriteHeight: Int): List<TextureRegion> {
    val array = TextureRegion.split(this, spriteWidth, spriteHeight)
    val sprites = mutableListOf<TextureRegion>()
    for (row in array) for (sprite in row) sprites += sprite
    return sprites
}

fun List<TextureRegion>.toAnimation(frameDelay: Float): StatefulAnimation<TextureRegion> =
    StatefulAnimation(Animation<TextureRegion>(frameDelay, this.toGdxArray(), Animation.PlayMode.LOOP))

class StatefulAnimation<T>(
    val gdxAnimation: Animation<T>,
    var state: Float = 0f
) {
    val currentRegion get() = gdxAnimation.getKeyFrame(state)

    fun update(delta: Float) {
        state = (state + delta) % (gdxAnimation.frameDuration * gdxAnimation.keyFrames.size)
    }
}

fun Actor.drawTexture(batch: Batch, texture: Texture) {
    batch.draw(texture, x - width / 2, y - height / 2, width, height)
}

fun Actor.drawTexture(batch: Batch, texture: TextureRegion) {
    batch.draw(texture, x - width / 2, y - height / 2, width, height)
}
