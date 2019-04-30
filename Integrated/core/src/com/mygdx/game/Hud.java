package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameLogic.GameBoard;

import javax.xml.soap.Text;
import java.util.ArrayList;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    public Integer score;
    public float time;

    Label playerScoreLabel;
    Label playerNameLabel;
    Label playerTimerLabel;

    Dialog buyDialog;
    Label buyDialogLabel;

    float buyDialogTime = -1;

    Dialog readOnlyDialog;
    Label readOnlyDialogLabel;

    float readOnlyDialogTime = -1;

    private Skin skin;

    public boolean flip_view = false;
    public int buy_result = -1;

    public final float turnLength = 05;

    private GameBoard board;

    private void NotifyCity(String cityName, int cityPrice, float timeout) {
        buy_result = -1;
        buyDialogLabel.setText(String.format("Buy %s for $%d?", cityName, cityPrice));
        buyDialog.show(stage);
        buyDialogTime = timeout;
    }

    private void NotifyReadOnly(String text, float timeout) {
        System.out.println("Here!");
        readOnlyDialogLabel.setText(text);
        readOnlyDialogTime = timeout;
        readOnlyDialog.show(stage);
    }

    private void notifyBuyDecision(boolean result) {
        board.notifyBuyDecision(result);
    }

    public Hud(SpriteBatch sb, float width, float height, GameBoard board) {
        this.board = board;
        score = 200;
        viewport = new FitViewport(width, height, new OrthographicCamera());
        stage = new Stage(viewport, sb);
//        skin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        BitmapFont jf_flat = new BitmapFont(Gdx.files.internal("fonts/jf_flat.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(jf_flat, Color.WHITE);
        labelStyle.font.getData().setScale(1.0f);
        playerNameLabel = new Label("Ahmad", labelStyle);
        playerScoreLabel = new Label(String.format("$ %05d", score), labelStyle);
        playerTimerLabel = new Label(String.format("%02d", (int)time), labelStyle);
        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle();
        tbStyle.font = jf_flat;
        Window.WindowStyle windowStyle = new Window.WindowStyle(jf_flat, Color.WHITE, new TextureRegionDrawable(new Texture("Monopoly_Tiles/dialog_bg.png")));

        buyDialog = new Dialog("", windowStyle) {
            protected void result(Object object) {
                if ((Boolean) object) {
                    buy_result = 1;
                } else {
                    buy_result = 0;
                }
                notifyBuyDecision((Boolean) object);
                this.hide(null);
            }
        };
        buyDialogLabel = new Label("Buy Cairo?", labelStyle);
        buyDialog.getContentTable().add(buyDialogLabel);
//        dialog.text("  Buy Cairo?  ", labelStyle);
        buyDialog.pad(80);
        tbStyle.up = new TextureRegionDrawable(new Texture("Monopoly_Tiles/yes_man.png"));
        buyDialog.button("Yes", true, tbStyle);
        buyDialog.button("No", false, tbStyle);

        readOnlyDialog = new Dialog("", windowStyle);
        readOnlyDialogLabel = new Label("You have been sent to jail!", labelStyle);
        readOnlyDialog.pad(80);
        readOnlyDialog.getContentTable().add(readOnlyDialogLabel);
        Gdx.input.setInputProcessor(stage);

        table.setDebug(true);

        table.add(playerNameLabel).expandX().padTop(2);
        table.add(playerScoreLabel).expandX().padTop(2);
        table.add(playerTimerLabel).expandX().padTop(2);
        table.row();
        table.right();
        stage.addActor(table);
    }

    public void update(float delta, Queue<Notification> notifications) {
//        if (time == 0) {
//            notifications.addFirst(new Notification(20, "Moldova", 5));
//        } else if (Math.abs(time - 6) <= 1) {
//            notifications.addFirst(new Notification("You will go to jail!", 5));
//        }
        while (! notifications.isEmpty()) {
            Notification notification = notifications.removeFirst();
            if (notification.timeout == -1) {
                notification.timeout = turnLength - time;
            }
            if (notification.notificationType == Notification.NotificationType.Buy) {
                NotifyCity(notification.text, notification.price, notification.timeout);
            } else if (notification.notificationType == Notification.NotificationType.ReadOnly) {
                NotifyReadOnly(notification.text, notification.timeout);
            }
        }
        if (buyDialogTime >= 0)
            buyDialogTime -= delta;
        else {
            buyDialog.hide(null);
            notifyBuyDecision(false);
        }
        if (readOnlyDialogTime >= 0)
            readOnlyDialogTime -= delta;
        else {
            readOnlyDialog.hide(null);
        }
        time += delta;
        if (time >= turnLength)
            time = 0;
        playerTimerLabel.setText(String.format("%02d", (int)time));
        playerScoreLabel.setText(String.format("$ %05d", score));
    };

}
