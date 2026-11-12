package com.comp2042.model;

import com.comp2042.util.ResourceLoader;
import com.comp2042.model.coin.Coin;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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
    private Image imgInputHead = new Image(ResourceLoader.externalForm("/views/res/snakehead.png"));
    private ImageView imageHead = new ImageView(imgInputHead);
    private Image imgInputBody = new Image(ResourceLoader.externalForm("/views/res/snakebody.png"));
    private ImageView imageBody = new ImageView(imgInputBody);
    private Image imgInputTurn = new Image(ResourceLoader.externalForm("/views/res/snaketurnleft.png"));
    private ImageView imageTurn = new ImageView(imgInputTurn);
    private Image imgInputTail = new Image(ResourceLoader.externalForm("/views/res/snaketail.png"));
    private ImageView imageTail = new ImageView(imgInputTail);
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

    public int getBodyDir(int bodyCount) {
        if (bodyCount == 0) {
            return 0;
        } else if (body.get(bodyCount).getY() == body.get(bodyCount - 1).getY()) {
            return 270;
        } else {
            return 0;
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

    public ImageView getImageViewHead() {
        return imageHead;
    }

    public ImageView getImageViewBody() {
        return imageBody;
    }

    public ImageView getImageViewTurn() {
        return imageTurn;
    }

    public Direction getDirection() {
        return direction;
    }

    public ImageView getImageViewTail() {
        return imageTail;
    }
}
