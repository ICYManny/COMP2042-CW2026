package com.comp2042.model;

import com.comp2042.model.coin.Coin;



import java.awt.*;
import java.util.ArrayList;

public class Snake {

    private boolean dead = false;
    private final Point head = new Point();
    private final ArrayList<Point> body = new ArrayList<>();
    private final int gx;
    private final int gy;
    private boolean ignoreBorders = false;
    private boolean noBorder;
    private Obstacle obstacle;
    private Direction direction = Direction.LEFT;

    public Snake(int x, int y, boolean noBorder, Obstacle obstacle) {
        this.obstacle = obstacle;
        this.noBorder = noBorder;
        this.head.setLocation(x / 2, y / 2);
        this.body.add(new Point(x / 2 + 1, y / 2));
        this.gx = x;
        this.gy = y;
    }

    public void move(Direction dir) {
        Point previous = new Point(head);
        body.add(previous);
        direction = dir;
        head.translate(dir.getDx(), dir.getDy());

        boolean outside = borderControl();
        boolean wraps = ignoreBorders || noBorder;
        if (outside && wraps) {
            head.setLocation(Math.floorMod(head.x, gx), Math.floorMod(head.y, gy));
        }
        if ((outside && !wraps) || obstacle.getObstacles().contains(head)) {
            head.setLocation(previous);
            dead = true;
        }

        if (body.size() > 4 && hitSelf() && !body.get(0).equals(head)) {
            dead = true;
        }
    }

    public boolean ateCoin(Coin coin) {
        if (!head.equals(coin.getPosition())) {
            body.remove(0);
        }
        return head.equals(coin.getPosition());
    }

    private boolean borderControl() {
        return head.getX() < 0 || head.getY() < 0 || head.getX() >= gx || head.getY() >= gy;
    }

    public boolean hitSelf() {
        return body.contains(head);
    }

    public ArrayList<Point> getBody() {
        return new ArrayList<>(body);
    }

    public Point getHead() {
        return new Point(head);
    }



    public boolean isDead() {
        return dead;
    }

    public void setIgnoreBorders(boolean ign) {
        ignoreBorders = ign;
    }




    public Direction getDirection() {
        return direction;
    }

}
