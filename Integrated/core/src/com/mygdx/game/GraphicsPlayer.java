package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class GraphicsPlayer {
    public int score;
    public Sprite playerSprite;

    public enum MotionState {
        idle,
        should_move,
        moving,
        finished_moving
    };

    public MotionState motionState = MotionState.idle;
    public int targetTile = 0;
    public Vector2 initialPos;

    public Queue<Notification> notifications;

    public GraphicsPlayer(Texture texture) {
        this(new Sprite(texture), 0);
    }

    public GraphicsPlayer(Sprite sp) {
        this(sp, 0);
    }

    public GraphicsPlayer(Sprite sp, int score) {
        this.score = score;
        this.playerSprite = sp;
        this.initialPos = new Vector2();
        this.notifications = new Queue<Notification>();
    }

    public Rectangle getCollider() {
        return playerSprite.getBoundingRectangle();
    }

    public void setPos(float posX, float posY) {
        playerSprite.setX(posX);
        playerSprite.setY(posY);
    }

    public void translate(float x, float y) {
        playerSprite.translate(x, y);
    }

    public void startAnimatedMotion(int targetTile) {
        this.targetTile =targetTile;
        this.motionState = MotionState.should_move;
    }
}
