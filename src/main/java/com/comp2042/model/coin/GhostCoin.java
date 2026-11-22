package com.comp2042.model.coin;

import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.Position;

/**
 * Power-up: for a few seconds the snake passes through walls and obstacles.
 * (Previously BlueCoin, which only ignored the outer walls until the next coin.)
 */
public class GhostCoin extends Coin {

    private static final int POINTS = 5;

    public GhostCoin(Position position) {
        super(position);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public String getSpriteKey() {
        return "bluecoin";
    }

    @Override
    public void applyEffect(GameModel game) {
        game.activateEffect(Effect.GHOST);
    }
}
