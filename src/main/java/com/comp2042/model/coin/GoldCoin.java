package com.comp2042.model.coin;

import com.comp2042.util.ResourceLoader;
import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;

import javafx.scene.image.Image;

public class GoldCoin extends Coin {
    private Image image = new Image(ResourceLoader.externalForm("/views/res/goldcoin.png"));

    public GoldCoin(int x, int y, Snake snake, Obstacle obstacle) {
        super(x, y, snake, obstacle);
    }

    public Image getImage() {
        return image;
    }

    public int getPoints() {
        int points = 15;
        return points;
    }
}
