package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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
    public int nextTile = 0;
    public Vector2 initialPos;

    public ArrayList<Notification> notifications;

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
        this.notifications = new ArrayList<Notification>();
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

    public void startAnimatedMotion(int targetTile, int nextTile) {
        this.targetTile = targetTile;
        this.nextTile = nextTile;
        this.motionState = MotionState.moving;
    }

    public void adjustRotation() {
        Rectangle rect = this.playerSprite.getBoundingRectangle();
        if (rect.x < 512) {
            playerSprite.setRotation(0);
        } else if (512 * 11 - rect.x < 512) {
            playerSprite.setRotation(-180);
        } else if (rect.y < 512) {
            playerSprite.setRotation(-270);
        } else {
            playerSprite.setRotation(-90);
        }
    }
}
