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
    val dot by lazy { Texture("dot.png") }
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
    val laser by lazy { sound("weapon_shot.ogg", 0.35f) }
    val click by lazy { sound("click.ogg") }
    val initiateBuildMode by lazy { click }
    val build by lazy { sound("building_construction.ogg") }
    val destroyBuilding by lazy { sound("building_demolish.ogg", 0.5f) }
    val mogulMove by lazy {
        listOf(
            "mogul_acknowledging1.ogg",
            "mogul_acknowledging2.ogg",
            "mogul_devaluing1.ogg",
            "mogul_devaluing2.ogg",
            "mogul_devaluing3.ogg",
            "mogul_sardonic-laughter.ogg"
        ).map { sound(it) }
    }
    val mogulDeath by lazy {
        listOf(
            "mogul_dying1.ogg",
            "mogul_dying2.ogg"
        ).map { sound(it) }
    }
}

object Music {
    val musicTheMurkyMogul by lazy { music("music_the-murky-mogul.ogg") }
}

class SoundWithVolume(val sound: Sound, val volume: Float) {
    fun play() = sound.play(volume)
}

fun sound(path: String, volume: Float = 1f) = SoundWithVolume(
    Gdx.audio.newSound(Gdx.files.internal("sounds/$path")),
    volume
)

fun music(path: String, volume: Float = 1f) =
    Gdx.audio.newMusic(Gdx.files.internal("music/$path")).also {
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
    batch.draw(texture, x, y, originX, originY, width, height, 1f, 1f, 0f, 0, 0, texture.width, texture.height, false, false)
}

fun Actor.drawTexture(batch: Batch, texture: TextureRegion) {
    batch.draw(texture, x, y, originX, originY, width, height, 1f, 1f, 0f)
}
