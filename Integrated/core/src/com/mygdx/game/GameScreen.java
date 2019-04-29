package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final BankElHaz game;

    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;

    private Rectangle bucket;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Array<Rectangle> raindrops;
    private long lastDropTime;
    int dropsGathered;

    public GameScreen(final BankElHaz game) {
        this.game = game;
        dropImage = new Texture("droplet.png");
        bucketImage = new Texture("bucket.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);

        batch = new SpriteBatch();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        dropsGathered = 0;
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void update(float delta) {
        // For the bucket
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucket.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (bucket.x < 0)
            bucket.x = 0;
        else if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;
        // For the raindrops
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) iter.remove();
            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void render (float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose () {
        batch.dispose();
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
