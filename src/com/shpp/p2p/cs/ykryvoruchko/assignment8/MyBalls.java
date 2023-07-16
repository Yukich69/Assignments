package com.shpp.p2p.cs.ykryvoruchko.assignment8;

import acm.graphics.GObject;
import acm.graphics.GOval;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class MyBalls extends WindowProgram {
    private static final int NUM_OF_CIRCLES = 10;

    private static int MOVED_CIRCLES = 3;

    /* Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 400;
    private static final double PAUSE_TIME = 1000.0 / 48;

    MyBall[] balls = new MyBall[NUM_OF_CIRCLES];


    public void run() {
        startGame();
    }

    private class MyBall {

        /**
         * horizontal step
         */
        private double hx = 2;

        /**
         * vertical step
         */
        private double vy = 2;

        /**
         * our ball which used
         */
        private final GOval ball;

        /**
         * count ball clicks
         */
        private int clickCount;
        /**
         * 0 -not moving
         * 1 -moving horizontally
         * 2 -moving vertically
         */

        MyBall(GOval ball) {
            this.ball = ball;
        }

        /**
         * return ball which used
         */
        public GOval getBall() {
            return ball;
        }

        /**
         * return ball clicks
         */
        public int getClickCount() {
            return clickCount;
        }

        /**
         * horisontal move strategy of ball which used
         */
        public void moveHorizontally() {
            if (ballBehindLeft(ball)) hx = -hx;

            if (ballBehindRight(ball)) hx = -hx;

            ball.move(hx, 0);
        }

        /**
         * vertical move strategy of ball which used
         */
        public void moveVertically() {
            if (ballBehindFloor(ball)) vy = -vy;

            if (ballBehindCeiling(ball)) vy = -vy;

            ball.move(0, vy);
        }

        /**
         * count clicks and set the color if  click odd or even
         */
        public void incrementClick() {
            clickCount++;
            if (clickCount != 0 && clickCount % 2 != 0) ball.setColor(Color.CYAN);
            else if (clickCount != 0 && clickCount % 2 == 0) ball.setColor(Color.GRAY);
        }
    }

    private void startGame() {
        addMouseListeners();
        createBallArray();

        while (MOVED_CIRCLES != 0) {
            moveBalls();
            pause(PAUSE_TIME);
        }
    }

    /**
     * move balls  and set move strategy if they click counter odd or even
     */
    private void moveBalls() {
        for (MyBall ball : balls) {
            if (ball.getClickCount() != 0 && ball.getClickCount() % 2 != 0) ball.moveHorizontally();
            else if (ball.getClickCount() != 0 && ball.getClickCount() % 2 == 0) ball.moveVertically();
        }
    }

    /**
     * increment clicks on current object if it is ball
     */
    public void mouseClicked(MouseEvent me) {
        GObject selectedObject = getElementAt(me.getX(), me.getY());
        if (selectedObject != null) {
            MyBall myBall = findMyBall(selectedObject);
            if (myBall != null) {
                myBall.incrementClick();
            }
        }
    }

    /**
     * return ball if it is current object
     */
    private MyBall findMyBall(GObject selectedObject) {
        for (MyBall ball : balls) {
            if (selectedObject == ball.getBall()) {
                return ball;
            }
        }
        return null;
    }




    /**
     * cheks if it behind right
     */
    public boolean ballBehindRight(GOval ball) {
        return ball.getX() + ball.getWidth() > getWidth();
    }

    /**
     * cheks if it behind left
     */
    public boolean ballBehindLeft(GOval ball) {
        return ball.getX() < 0;
    }

    /**
     * checks if it behind ceiling
     */
    public boolean ballBehindCeiling(GOval ball) {
        return ball.getY() < 0;
    }

    /**
     * check if it out of floor
     */
    public boolean ballBehindFloor(GOval ball) {
        return ball.getY() + ball.getHeight() > getHeight();
    }

    /**
     * build array of Balls and add to array
     */
    private void createBallArray() {
        int x = (getWidth() - (NUM_OF_CIRCLES - 1)) / NUM_OF_CIRCLES;
        int y = (getHeight() - (NUM_OF_CIRCLES - 1)) / NUM_OF_CIRCLES;

        for (int i = 0; i < NUM_OF_CIRCLES; i++) {
            GOval ball = new GOval(i * x,i * y,  x,  y);
            ball.setFilled(true);
            ball.setColor(Color.GRAY);
            add(ball);
            balls[i] = new MyBall(ball);
        }
    }
}
