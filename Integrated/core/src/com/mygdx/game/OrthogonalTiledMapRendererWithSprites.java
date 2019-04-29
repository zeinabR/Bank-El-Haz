package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {
    private List<Sprite> sprites;

    public OrthogonalTiledMapRendererWithSprites(TiledMap map) {
        super(map);
        sprites = new ArrayList<Sprite>();
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    @Override
    public void render() {
        beginRender();
        for (MapLayer layer : map.getLayers()) {
            if (layer.isVisible()) {
                if (layer instanceof TiledMapTileLayer) {
                    renderTileLayer((TiledMapTileLayer) layer);
                }
            }
        }
        for (Sprite sprite : sprites) {
            sprite.draw(this.getBatch());
        }
        endRender();
    }
}
