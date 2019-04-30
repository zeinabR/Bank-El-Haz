package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.Network.NetworkManager;

public class MainMenuScreen implements Screen {
    final BankElHaz game;
    OrthographicCamera camera;
    NetworkManager manager;

    public MainMenuScreen(final BankElHaz game, NetworkManager mgr) {
        this.game = game;
        this.manager = mgr;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512 * 12, 512 * 9);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to BankElHaz!", camera.viewportWidth / 2, camera.viewportHeight);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 250);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new MonopolyGameScreen(game, manager));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
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
    public void dispose() {
    }
}
