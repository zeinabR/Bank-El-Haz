package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Network.NetworkManager;

public class BankElHaz extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public NetworkManager manager;

    public BankElHaz(NetworkManager mgr) {
        this.manager = mgr;
    }

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MonopolyGameScreen(this, manager));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
