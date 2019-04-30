package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameLogic.GameBoard;

import java.util.ArrayList;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.floor;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class MonopolyGameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private MapLayers mapLayers;
    private OrthogonalTiledMapRendererWithSprites tiledMapRenderer;
    final BankElHaz game;
    private Hud hud;
    private ArrayList<RectangleMapObject> collidableObjects;
    private ArrayList<GraphicsPlayer> players;
    private int[][] tilePos;
    private GameBoard board;
    int numPlayers;
    int currentPlayerTurn = -1;
    public Sprite dieSprite;
    Texture[] dieTextures;
    int dieOutcome = 1;

    public MonopolyGameScreen(final BankElHaz game) {
        this(game, 4);
    }

    public MonopolyGameScreen(final BankElHaz game, int n_players) {
        this.numPlayers = n_players;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512 * 12, 512 * 9);
        camera.translate(-512 / 2, - 512 / 2);
        tiledMap = new TmxMapLoader().load("Monopoly_Tiles/main_board.tmx");
        mapLayers = tiledMap.getLayers();
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
        int[] objectLayersIndices  = new int[] {
            mapLayers.getIndex("Cities"),
            mapLayers.getIndex("Other")
        };
        MapObjects cities = tiledMap.getLayers().get(objectLayersIndices[0]).getObjects();
        MapObjects other_objects = tiledMap.getLayers().get(objectLayersIndices[1]).getObjects();
        collidableObjects = new ArrayList<RectangleMapObject>();
        addRectMapObjects(cities, collidableObjects);
        addRectMapObjects(other_objects, collidableObjects);
        RectangleMapObject startObject = (RectangleMapObject) other_objects.get("Start");
        players = new ArrayList<GraphicsPlayer>();
        String[] dieTexturesAddr = new String[] {
                "diceWhite_border/dieWhite_border1.png",
                "diceWhite_border/dieWhite_border2.png",
                "diceWhite_border/dieWhite_border3.png",
                "diceWhite_border/dieWhite_border4.png",
                "diceWhite_border/dieWhite_border5.png",
                "diceWhite_border/dieWhite_border6.png"
        };
        dieTextures = new Texture[6];
        for (int i = 0; i < 6; i++) {
            dieTextures[i] = new Texture(Gdx.files.internal(dieTexturesAddr[i]));
        }
        String[] carTextures = {
                "cars/car1.png",
                "cars/car2.png",
                "cars/car3.png",
                "cars/car4.png"
        };
        for (int i = 0; i < n_players; i++) {
            GraphicsPlayer player = new GraphicsPlayer(new Texture(Gdx.files.internal(carTextures[i])));
            players.add(player);
            tiledMapRenderer.addSprite(player.playerSprite);
        }
        board = new GameBoard(n_players);
        hud = new Hud(game.batch, camera.viewportWidth, camera.viewportHeight, board);
        dieSprite = new Sprite(dieTextures[0]);
        dieSprite.setPosition(512 * 11 / 2 - 256, 512 * 8 / 2 - 256 );
        tiledMapRenderer.addSprite(dieSprite);
        initTilePos();
    }

    private void addRectMapObjects(MapObjects objects, ArrayList<RectangleMapObject> rectangleMapObjects) {
        for (MapObject mapObject : objects) {
            if (mapObject instanceof RectangleMapObject) {
                collidableObjects.add((RectangleMapObject) mapObject);
            }
        }
    }

    private void initTilePos() {
        tilePos = new int[11][8];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 8; j++) {
                tilePos[i][j] = -1;
            }
        }
        tilePos[0][0] = 0;
        tilePos[0][1] = 1;
        tilePos[0][2] = 2;
        tilePos[0][3] = 3;
        tilePos[0][4] = 4;
        tilePos[0][5] = 5;
        tilePos[0][6] = 6;
        tilePos[0][7] = 7;
        tilePos[1][7] = 8;
        tilePos[2][7] = 9;
        tilePos[3][7] = 10;
        tilePos[4][7] = 11;
        tilePos[5][7] = 12;
        tilePos[6][7] = 13;
        tilePos[7][7] = 14;
        tilePos[8][7] = 15;
        tilePos[9][7] = 16;
        tilePos[9][6] = 16;
        tilePos[10][7] = 17;
        tilePos[10][6] = 18;
        tilePos[10][5] = 19;
        tilePos[10][4] = 20;
        tilePos[10][3] = 21;
        tilePos[10][2] = 22;
        tilePos[10][1] = 23;
        tilePos[10][0] = 24;
        tilePos[9][0] = 25;
        tilePos[8][0] = 26;
        tilePos[7][0] = 27;
        tilePos[6][0] = 28;
        tilePos[5][0] = 29;
        tilePos[4][0] = 30;
        tilePos[3][0] = 31;
        tilePos[2][0] = 32;
        tilePos[1][0] = 33;
    }

    private int getTileFromPos(float posX, float posY) {

        int x = abs(floor(posX / 512));
        int y = abs(floor(posY / 512));
        if (tilePos[x][y] == -1) {
            System.out.println(String.format("Error: unhandled [x][y] in tilePos: [%d][%d]", x, y));
        }
        return tilePos[x][y];
    }

    private Vector2 getPosFromTile(int tileNumber) {
        Vector2 pos = new Vector2(-1, -1);
        if (tileNumber >= 0 && tileNumber < 8)
            pos.set(0, tileNumber);
        else if (tileNumber >= 8 && tileNumber < 17)
            pos.set(tileNumber - 7, 7);
        else if (tileNumber >= 17 && tileNumber < 24)
            pos.set(10, 24 - tileNumber);
        else if (tileNumber >= 24 && tileNumber < 34)
            pos.set(34 - tileNumber, 0);
        else
            System.out.println(String.format("Error! Illegal tileNumber: %d!", tileNumber));
        pos.x = pos.x * 512;
        pos.y = pos.y * 512;
        return pos;

    }

    private boolean snap(Rectangle rect, Sprite sp, float posToSnapX, float posToSnapY, float tolerance) {
        int changedPos = 0;
        if (abs(rect.x - posToSnapX) < tolerance) {
            changedPos += 1;
            sp.translateX(posToSnapX - rect.x);
        }
        if (abs(rect.y - posToSnapY) < tolerance) {
            changedPos += 1;
            sp.translateY(posToSnapY - rect.y);
        }
        return (changedPos == 2);
    }

    private void updatePlayers(float delta) {
        for (GraphicsPlayer player : players) {
            if (player.motionState == GraphicsPlayer.MotionState.finished_moving) {
                Rectangle playerCollider = player.getCollider();
                snap(playerCollider, player.playerSprite, round(playerCollider.x / 512) * 512, round(playerCollider.y  / 512) * 512, 16);
                player.motionState = GraphicsPlayer.MotionState.idle;
            }
            if (player.motionState == GraphicsPlayer.MotionState.should_move) {
                Rectangle playerCollider = player.getCollider();
                player.initialPos.set(playerCollider.x, playerCollider.y);
                player.motionState = GraphicsPlayer.MotionState.moving;
            }
            else if (player.motionState == GraphicsPlayer.MotionState.moving) {
                Vector2 targetTilePos = getPosFromTile(player.targetTile);
                Rectangle playerCollider = player.getCollider();
//                System.out.println(
//                        String.format("(%f, %f) -> (%f, %f)", playerCollider.x, playerCollider.y, targetTilePos.x, targetTilePos.y)
//                );
                if (abs(targetTilePos.x - playerCollider.x) < 32 && abs(targetTilePos.y - playerCollider.y) < 32) {
                    player.motionState = GraphicsPlayer.MotionState.finished_moving;
                } else {
                    int currentTile = getTileFromPos(playerCollider.x, playerCollider.y);
                    int nextTile = (currentTile + 1) % 34;
                    Vector2 nextTilePos = getPosFromTile(nextTile);
                    if (currentTile >= 0 && nextTile < 8) {
                        player.playerSprite.setRotation(0);
                        player.playerSprite.translateY(16);
                    } else if (nextTile < 17) {
                        player.playerSprite.setRotation(-90);
                        player.playerSprite.translateX(16);
                    } else if (nextTile == 17 && 512 * 11 - playerCollider.x > 32) {
                        player.playerSprite.setRotation(-90);
                        player.playerSprite.translateX(16);
                    } else if (nextTile < 25) {
                        player.playerSprite.setRotation(-180);
                        player.playerSprite.translateY(-16);
                    } else if (nextTile == 25 && playerCollider.y > 32) {
                        player.playerSprite.setRotation(-180);
                        player.playerSprite.translateY(-16);
                    }
                    else /* if (nextTile > 25) */ {
                        player.playerSprite.setRotation(-270);
                        player.playerSprite.translateX(-16);
                    }
                }
            }
        }
    }

    private void update(float delta) {
        if (hud.time == 0) {
            boolean skipTurn = true;
            do {
                currentPlayerTurn = (currentPlayerTurn + 1) % numPlayers;
                dieOutcome = board.updateUserPosition(currentPlayerTurn, players.get(currentPlayerTurn));
                skipTurn = (dieOutcome != 0);
            } while (!skipTurn);
        }
        dieSprite.setTexture(dieTextures[dieOutcome - 1]);
        updatePlayers(delta);
        GraphicsPlayer currentPlayer = players.get(currentPlayerTurn);
        int i = 0;
        // A hack, **for now**.
        for (GraphicsPlayer graphicsPlayer : players) {
            if (i == currentPlayerTurn) continue;
            graphicsPlayer.notifications.clear();
        }
        if (currentPlayer.motionState == GraphicsPlayer.MotionState.finished_moving) {
            /* Notify the GameBoard. */
            board.processCurrentBlock(currentPlayerTurn, currentPlayer);
        }
        hud.update(delta, currentPlayer.notifications);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(91/256f, 110/256f,225/256f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void show() { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        tiledMap.dispose();
    }
}
