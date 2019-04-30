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
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.GameLogic.GameBoard;
import com.mygdx.game.GameLogic.GamePlayer;
import com.mygdx.game.Network.NetworkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

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
    private int[][] tilePos;
    private GameBoard board;
    int numPlayers;
    int currentPlayerTurn = -1;
    public Sprite dieSprite;
    Texture[] dieTextures;
    int dieOutcome = 1;

    int myID;
    ObjectOutputStream[] objectOutputStreams;
    ObjectInputStream objectInputStream;
    private ArrayList<GraphicsPlayer> players;

    final public NetworkManager manager;

    public MonopolyGameScreen(final BankElHaz game, NetworkManager manager) {
        this.manager = manager;
        this.myID = manager.my_turn;
        this.numPlayers = manager.num_connected.get() + 1;
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
        for (int i = 0; i < numPlayers; i++) {
            GraphicsPlayer player = new GraphicsPlayer(new Texture(Gdx.files.internal(carTextures[i])));
            players.add(player);
            tiledMapRenderer.addSprite(player.playerSprite);
        }
        board = new GameBoard(numPlayers);
        hud = new Hud(game.batch, camera.viewportWidth, camera.viewportHeight, board, myID);
        dieSprite = new Sprite(dieTextures[0]);
        dieSprite.setPosition(512 * 11 / 2 - 256, 512 * 8 / 2 - 256 * 5 );
        tiledMapRenderer.addSprite(dieSprite);
        initTilePos();
        objectOutputStreams = new ObjectOutputStream[numPlayers];
        for(int i = 0; i < numPlayers; i++) {
            if (i == myID) continue;
            objectOutputStreams[i] = manager.getObjectOutputStream(manager.players_ips[i], manager.players_ports[i]);
        }
        objectInputStream = manager.getObjectInputStream(10 * 1000);
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
//        GamePlayer gamePlayer = board.players.getFirst();
        int i = 0;
        for (GraphicsPlayer player : players) {
            GamePlayer gamePlayer = board.players.get(i);
            if (player.motionState == GraphicsPlayer.MotionState.finished_moving) {
                player.motionState = GraphicsPlayer.MotionState.idle;
            }
            if (player.motionState == GraphicsPlayer.MotionState.should_move) {
                player.motionState = GraphicsPlayer.MotionState.moving;
            }
            else if (player.motionState == GraphicsPlayer.MotionState.moving) {
                Vector2 targetTilePos = getPosFromTile(player.targetTile);
                Rectangle playerCollider = player.getCollider();
                player.playerSprite.setRotation(0);
                Vector2 nextTilePos = getPosFromTile(player.nextTile);
                System.out.println(String.format("%f, %f", playerCollider.x, playerCollider.y));
                System.out.println(String.format("%f, %f", nextTilePos.x, nextTilePos.y));
                player.playerSprite.setPosition(nextTilePos.x, nextTilePos.y);
                player.adjustRotation();
                if (abs(nextTilePos.x - targetTilePos.x) <= 16 && abs(nextTilePos.y - targetTilePos.y) <= 16) {
                    player.motionState = GraphicsPlayer.MotionState.finished_moving;
                }
                player.nextTile += 1;
//                if (nextTilePos.x > playerCollider.x)
//                    player.playerSprite.translateX(16);
//                else if (nextTilePos.x < playerCollider.x)
//                    player.playerSprite.translateX(-16);
//                if (nextTilePos.y > playerCollider.y)
//                    player.playerSprite.translateY(16);
//                else if (nextTilePos.y < playerCollider.y)
//                    player.playerSprite.translateY(-16);
//                player.playerSprite.translate(targetTilePos.x - playerCollider.x, targetTilePos.y - playerCollider.y);
////                playerCollider = player.getCollider
//                player.motionState = GraphicsPlayer.MotionState.finished_moving;
            }
        }
    }

    private void update(float delta) {
        currentPlayerTurn = (currentPlayerTurn + 1) % numPlayers;
        hud.score = board.players.get(myID).account;
        if (currentPlayerTurn == myID) {
            if (hud.time == 0) {
                dieOutcome = board.updateUserPosition(currentPlayerTurn, players.get(currentPlayerTurn));
            }
            dieSprite.setTexture(dieTextures[dieOutcome - 1]);
            updatePlayers(delta);
            GraphicsPlayer currentPlayer = players.get(currentPlayerTurn);
            int idx = 0;
            // A hack, **for now**.
            for (GraphicsPlayer graphicsPlayer : players) {
                if (idx == myID) continue;
                graphicsPlayer.notifications.clear();
            }
            if (currentPlayer.motionState == GraphicsPlayer.MotionState.finished_moving) {
                /* Notify the GameBoard. */
                board.processCurrentBlock(currentPlayerTurn, currentPlayer);
            }
            hud.update(delta, currentPlayer.notifications);
            if (hud.time == 0) {
                // Send updates to others
                for (int i = 0; i < numPlayers; i++) {
                    if (i != currentPlayerTurn) {
//                        manager.send_Object(new Changes(currentPlayerTurn, board.players.get(currentPlayerTurn).position), manager.players_ips[i], manager.players_ports[i]);
                        GamePlayer currentGamePlayer = board.players.get(currentPlayerTurn);
                        String message = String.format("%1d %2d ***", currentPlayerTurn, currentGamePlayer.position);
                        System.out.println("Starting to send: ");
                        try {
//                            objectOutputStreams[i].writeObject(message);
                            objectOutputStreams[i].writeInt(currentPlayerTurn);
                            objectOutputStreams[i].writeInt(currentGamePlayer.position);
                            objectOutputStreams[i].flush();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Sent Object...");
                    }
                }
            }
        } else {
            hud.update(delta, new ArrayList<Notification>());
            if (hud.time == 0) {
//                if (!manager.my_Socket.isClosed()) {
//                    try {
//                        manager.my_Socket.close();
//                        manager.my_Socket = new ServerSocket(manager.my_port);
//                    } catch (IOException e) {
//                        System.out.println(e.getMessage());
//                        System.out.println("IO Exception occured on receiving updates!");
//                    }
//                }
//                GamePlayer gp = (GamePlayer) manager.rcv_Object( 10 * 1000);
//                Changes changes = (Changes) manager.rcv_Object(10 * 1000);
//                System.out.println("Received: " + changes.newPos);
//                board.players.set(gp.id, gp);
                try {
//                    String message = (String) objectInputStream.readObject();
//                    System.out.println("RECV: " + message);
//                    Scanner scanner = new Scanner(message);
//                    int idToChange = scanner.nextInt();
//                    int newPos = scanner.nextInt();
                    System.out.println("Starting to receive: ");
                    int idToChange = objectInputStream.readInt();
                    int newPos = objectInputStream.readInt();
                    System.out.println("Received objects");
                    GamePlayer gamePlayer = board.players.get(idToChange);
                    int nextTile = (gamePlayer.position + 1) % 34;
                    gamePlayer.position = newPos;
                    GraphicsPlayer player = players.get(idToChange);
                    player.startAnimatedMotion(newPos, nextTile);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("IO Exception occured on receiving updates!"); }
//                } catch (ClassNotFoundException e) {
//                    System.out.println(e.getMessage());
//                }

                // Receive updates from others
            }
        }

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
