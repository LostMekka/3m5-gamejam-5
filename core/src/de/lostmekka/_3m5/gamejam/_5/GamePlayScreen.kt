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
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.graphics.use
import ktx.math.vec2



class GamePlayScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val world = createWorld()

    private val guiviewport = ScreenViewport()
    private val counter = FreeTypeFontGenerator(Gdx.files.internal("fonts/UbuntuMono-R.ttf")).let {
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 40
        val font = it.generateFont(parameter)
        it.dispose()
        font
    }
    private var amount = "Slaves: 0"

    private val groundAtlas = Textures.groundAtlas
    private val ground = Ground(groundAtlas)

    private val viewport = ExtendViewport(40f, 20f, OrthographicCamera().also { it.zoom = 0.5f })
    private val mogul = Mogul(world, vec2())
    private val stage = Stage(viewport).apply {
        addActor(mogul)
        addActor(createTower(vec2(5f, 2f)))
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

    private val lasers = mutableListOf<Laser>()

    private fun addLaser(start: Vector2, end: Vector2) {
        lasers += Laser(start, end, laserLifetime, laserColor)
    }

    private fun createTower(pos: Vector2) = Tower(world, pos, ::addLaser)

    private val userInputHandler = UserInputHandler(mogul, stage, world, ::createTower)

    private fun handleInput() {
        val screenCoords = vec2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        val stageCoords = stage.screenToStageCoordinates(screenCoords)

        userInputHandler.handleInput(stageCoords)
    }

    private fun update(delta: Float) {
        stage.camera.position.set(0f, 0f, stage.camera.position.z)

        world.step(delta, 5, 5)
        for (actor in stage.actors) if (actor is PhysicsBodyActor) actor.updatePhysics()
        stage.act(delta)

        for (laser in lasers) laser.update(delta)
        lasers.removeAll { it.isDone }

        amount = "Slaves:" + slaves

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

        batch.use {
            userInputHandler.draw(batch)
        }

        batch.projectionMatrix = guiviewport.camera.projection
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

    override fun render(delta: Float) {
        handleInput()
        update(delta)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        guiviewport.update(width,height, true)
    }

    override fun dispose() {
        batch.dispose()
    }
}