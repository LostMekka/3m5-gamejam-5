package de.lostmekka._3m5.gamejam._5

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import de.lostmekka._3m5.gamejam._5.entity.*
import de.lostmekka._3m5.gamejam._5.helper.addActor
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.graphics.use
import ktx.math.*


class GamePlayScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val world = createWorld()

    private val guiViewport = ScreenViewport()
    private val counter = FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf")).let {
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 40
        val font = it.generateFont(parameter)
        it.dispose()
        font
    }
    private var amount = "0"

    private val groundAtlas = Textures.groundAtlas
    private val ground = Ground(groundAtlas)

    private val viewport = ExtendViewport(50f, 25f, OrthographicCamera().also { it.zoom = 0.5f })
    private val mogul = Mogul(world, vec2(-7.5f, -2.7f))
    private val stage = Stage(viewport).apply {
        addActor(mogul)
        addActor(createTower(vec2(-8.4f, 0f)))
        addActor(createTower(vec2(-8.4f, -4f)))

        addActor(DudeSpawner(vec2(0f, 7f)) {
            Dude(world).also { it.dressColor = Color.RED }
        })
        addActor(DudeSpawner(vec2(-5f, 7f)) {
            Dude(world).also { it.dressColor = Color.MAROON }
        })
        addActor(DudeSpawner(vec2(2f, -9f)) {
            Dude(world).also { it.dressColor = Color.FIREBRICK }
        })

        addActor(Magistrate(world, vec2(-8.4f, -2.6f))) { isActive = true }
        addActor(Magistrate(world, vec2(7.8f, 4.1f))) { isActive = false }
        addActor(Magistrate(world, vec2(-0.1f, -5.1f))) { isActive = false }
    }

    private val lasers = mutableListOf<Laser>()

    private fun addLaser(start: Vector2, end: Vector2) {
        lasers += Laser(start, end, laserLifetime, laserColor)
    }

    private fun createTower(pos: Vector2) = Tower(world, pos, ::addLaser)

    private val userInputHandler = UserInputHandler(mogul, stage, world, ::createTower)

    private val theSongOfMyPeople = Music.musicTheMurkyMogul.play()

    private fun handleInput() {
        val screenCoords = vec2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        val stageCoords = stage.screenToStageCoordinates(screenCoords)

        userInputHandler.handleInput(stageCoords)
    }

    private var deathCheckTimer = postDeathCheckInterval
    private fun update(delta: Float) {
        stage.camera.position.set(0f, 0f, stage.camera.position.z)

        world.step(delta, 5, 5)
        for (actor in stage.actors) if (actor is PhysicsBodyActor) actor.updatePhysics()

        deathCheckTimer -= delta
        if (deathCheckTimer <= 0) {
            deathCheckTimer += postDeathCheckInterval
            val lonePosts = stage.actors
                .mapNotNull { it as? CaravanPost }
                .filter { it.connections.size <= 1 }
            for (lonePost in lonePosts) {
                val hasMogul = lonePost.position.dst(mogul.position) <= buildCaravanPostPostDistance
                if (lonePost.testForBuildingDeath(hasMogul)) lonePost.destroy()
            }
        }
        stage.act(delta)

        for (laser in lasers) laser.update(delta)
        lasers.removeAll { it.isDone }

        amount = slaves.toString()
    }

    private fun draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = viewport.camera.projection
        batch.use {
            ground.draw(batch)
            val sources = mutableSetOf<Connectable>()
            for (actor in stage.actors) {
                if (actor is Connectable) {
                    sources += actor
                    for (other in actor.connections) {
                        if (other !in sources) {
                            it.drawDots(actor.connectionOrigin, other.connectionOrigin)
                        }
                    }
                }
            }
        }

        stage.actors.sort { actorA, actorB ->
            when {
                actorA.y == actorB.y -> 0
                actorA.y > actorB.y -> -1
                else -> 1
            }
        }

        stage.draw()

        batch.use {
            userInputHandler.draw(batch)
        }

        batch.projectionMatrix = guiViewport.camera.projection
        batch.use {
            // draw textures that are not managed by stage
            counter.setColor(0f, 0f, 0f, 1.0f)
            counter.draw(batch, amount, 0f, (Gdx.graphics.height / 2f) - 0.1f)
        }

        shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
            it.projectionMatrix = stage.camera.projection
            it.setAutoShapeType(true)
            // draw lines and other shapes here. NOTE: actor debug stuff can be drawn by overriding Actor::drawDebug
            for (laser in lasers) laser.draw(shapeRenderer)
        }
    }

    private fun SpriteBatch.drawDots(start: Vector2, end: Vector2) {
        val dist = start.dst(end)
        val count = (dist / maxDotDistance).toInt()
        val step = (end - start) / (count + 1)
        for (i in (1..count)) {
            val pos = start + (step * i)
            draw(Textures.dot, pos.x - 0.125f, pos.y - 0.125f, 0.25f, 0.25f)
        }
    }

    override fun render(delta: Float) {
        handleInput()
        update(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        guiViewport.update(width, height, true)
    }

    override fun dispose() {
        batch.dispose()
    }
}
