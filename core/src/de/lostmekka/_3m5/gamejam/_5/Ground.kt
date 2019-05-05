package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import kotlin.math.floor

data class GroundTile(
    val pos: Vector2,
    val color: Color,
    val tex: TextureRegion
)

class Ground(val groundAtlas: List<TextureRegion>) {

    val ground = createGround()

    fun draw(batch: SpriteBatch) {
        val resetColor = batch.color

        for (tile in ground) {
            batch.color = tile.color
            batch.draw(tile.tex, tile.pos.x, tile.pos.y, 0.5f, 0.5f)
        }

        batch.color = resetColor
    }

    private fun createGround(): List<GroundTile> {
        val ground = mutableListOf<GroundTile>();

        val lightNoise = PerlinNoiseGenerator.generatePerlinNoise(100, 100, 5)
        val atlasIndexNoise = PerlinNoiseGenerator.generatePerlinNoise(100, 100, 1)

        for (y in 0..99) {
            for (x in 0..99) {
                val r = 0.3f + 0.7f * lightNoise[y][x]
                val g = r
                val b = r

                ground.add(
                    GroundTile(
                        Vector2(x * 16f / 32f - 25f, y * 16f / 32f - 25f),
                        Color(r, g, b, 1f),
                        groundAtlas[floor(atlasIndexNoise[y][x] * groundAtlas.size).toInt()]
                    )
                )
            }
        }

        return ground
    }
}