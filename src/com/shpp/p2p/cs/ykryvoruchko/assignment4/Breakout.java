package com.shpp.p2p.cs.ykryvoruchko.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

    /** Classic arcade game "Breakout" */
public class Breakout extends WindowProgram {
    /** Method which overrides  mouseMoved. The paddle is controlled by the mouse,
     * so whenever the user does not move the mouse,
     * the paddle will move with the cursor, while it is centered relative to the cursor.
     * The paddle always has the same coordinate along the Y axis, it never moves up and down.
     * When the user takes the mouse out of the screen, the paddle remains completely on the screen. */
    public void mouseMoved(MouseEvent mouseEvent){
        paddle.setLocation(mouseEvent.getX() - PADDLE_WIDTH / 2.0, getHeight() - PADDLE_Y_OFFSET);
        if (paddle.getX() <= 0){
            paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
        }
        if ( paddle.getX() + PADDLE_WIDTH  >= getWidth()){
            paddle.setLocation( getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
        }
    }

    /** The amount of time to pause between frames (48fps). */
    private static final double PAUSE_TIME = 1000.0 / 48;

    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of turns */
    private static final int NTURNS = 3;

    /** Ball velocity */
    RandomGenerator rgen = RandomGenerator.getInstance();
    private double vx = rgen.nextDouble(1.0, 3.0);
    private double vy = 6;
    

    public void run() {
        createNewGameWorld();
    }

    /** Actions which prepares all game elements.*/
    private void createNewGameWorld(){
      createBrickRows();
      addMouseListeners();
      createBall();
      createPaddle();
      ongoingBall();
 }

    /** Method create rows of bricks (NBRICK_ROWS x NBRICKS_PER_ROW)
     * Each two rows has a new color in the sequence: RED, ORANGE, YELLOW, GREEN, CYAN.*/
    private void createBrickRows(){
        for (int i = 0; i < NBRICK_ROWS; i++){
            Color color;
            if (i < NBRICK_ROWS / 5){
                color = Color.RED;
            }
            else if(i < (NBRICK_ROWS / 5) * 2){
                color = Color.ORANGE;
            }
            else if(i < (NBRICK_ROWS / 5) * 3){
                color = Color.YELLOW;
            }
            else if(i < (NBRICK_ROWS / 5) * 4){
                color = Color.GREEN;
            }
            else {
                color = Color.CYAN;
            }
            createBrickRow(BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP ), color);
        }
    }
    
     GRect brick = null;    // used to be able to change values anywhere in the class
        /**
         * Create row of filled bricks.
         *
         * @param y Value of Y coordinate.
         * @param color Color of brick.
         */   
    private void createBrickRow( int y, Color color) {
        for (int i = 0; i < NBRICKS_PER_ROW; i++){
                brick = new GRect(BRICK_SEP / 2.0 + i * (BRICK_WIDTH + BRICK_SEP), y,
                                     BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);
                brick.setFillColor(color);
                add(brick);
        }
    }

    /**
     * Catch and analyze a colliding object .
     *
     * @return GObject which catch in points.
     */
    private GObject getCollidingObject(){
       if(getElementAt(paddle.getX() - 1, paddle.getY() ) != null){  //Scanning points near paddles corners check is it ball
           return getElementAt(paddle.getX() - 1, paddle.getY());
       }
       else if (getElementAt(paddle.getX() + PADDLE_WIDTH + 1,paddle.getY() ) != null){
          return getElementAt(paddle.getX() + PADDLE_WIDTH + 1,paddle.getY());
       }

      else if(getElementAt(ball.getX(),ball.getY()) != null){     //Scanning points around ball check is it paddle
          return getElementAt(ball.getX(),ball.getY());
      }
      else if(getElementAt(ball.getX(),ball.getY()) != null &&
               getElementAt(ball.getX(),ball.getY()) != paddle ){    //Scanning points around ball check is it brick
          return getElementAt(ball.getX(),ball.getY());
      }

       else if(getElementAt(ball.getX() + BALL_RADIUS * 2  ,ball.getY()) != null){
          return getElementAt(ball.getX() + BALL_RADIUS * 2  ,ball.getY()) ;
      }
       else if(getElementAt(ball.getX() + BALL_RADIUS * 2  ,ball.getY()) != null
               && getElementAt(ball.getX() + BALL_RADIUS * 2  ,ball.getY()) != paddle ) {
           return getElementAt(ball.getX() + BALL_RADIUS * 2  ,ball.getY()) ;
       }

       else if(getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2) != null){
            return getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2);
        }
        else if(getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2) != null &&
               getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2) != paddle ){
            return getElementAt(ball.getX(),ball.getY() + BALL_RADIUS * 2);
        }

       else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2) != null){
          return getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2);
      }
       else if(getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2) != null &&
               getElementAt(ball.getX() + BALL_RADIUS * 2,ball.getY() + BALL_RADIUS * 2) != paddle  ) {
           return getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
       }
       else{
           return null;
       }
    }

    /** Method that include paddle collision handling,
     * collision with a brick and as a result movement of ball changes.*/
    private void ongoingBall(){
        int lives = NTURNS;        // value of current lives
        int nbricks = NBRICK_ROWS * NBRICKS_PER_ROW;
        if (rgen.nextBoolean(0.5)) vx = -vx;
        while (lives >= 0 || nbricks==0) {
            /* Move the ball by the current velocity. */
            ball.move(vx, vy);
            /* Get a colliding object calls collider */
            GObject collider = getCollidingObject();
            /* Ball change Ox direction if  hit right or left wall */
            if (ballHitRightWall(ball) || ballHitLeftWall(ball)) vx = -vx;
            /* Ball change Oy direction if  hit right or left wall */
            if (ballHitUpWall(ball)) vy = -vy;

            /* Ball disappears and you lose 1 life if hit down wall(fall under the paddle) */
            if (ballHitDownWall(ball)){
                lives--;
                remove(ball);
                waitForClick();
                createBall();
            }
            /* Ball jumps off the paddle */
             if (collider == paddle ) vy = -vy;
            /* Ball jumps off the paddles corner*/
             if (collider == ball){
                 vy = - vy;
                 vx = - vx;
             }
            /* Ball crushes the brick and jump off it */
             if (collider != paddle  && collider != null && collider != ball){
                 vy= -vy;
                 remove(collider);
                 nbricks--;         //Count crushed bricks
             }
            ifGameOver(lives);
            ifWinTheGame(nbricks);
            pause(PAUSE_TIME);
        }
    }

    /** Method that clear the world and print victory label if all bricks are destroyed.
     * After click you can start another game*/
    private void ifWinTheGame(int nbricks) {
        if (nbricks == 0){
            removeAll();
            GLabel win = new GLabel("YOU WIN !!!" );
            win.setFont("Serif-40");
            win.setColor(Color.GREEN);
            add(win, getWidth() / 2.0 - win.getWidth() / 2.0, getHeight() / 2.0 -win.getAscent() / 2.0);
            waitForClick();
            removeAll();
            createNewGameWorld();
        }
    }

    /** Method that clear the world and print lose label if all lives are wasted.
     * After click you can start another game*/
    private void ifGameOver(int lives) {
        if(lives == 0){
            removeAll();
            GLabel lose = new GLabel("GAME OVER !!!" );
            lose .setFont("Serif-40");
            lose.setColor(Color.RED);
            add(lose , getWidth()/2.0 - lose .getWidth()/2.0, getHeight()/2.0 - lose .getAscent()/2.0);
            waitForClick();
            removeAll();
            createNewGameWorld();
        }
    }

    /** Create a ball*/
    GOval ball = null;        // used to be able to change  anywhere in the class
    private void createBall() {
         ball = new GOval(getWidth() / 2.0-BALL_RADIUS,
                               getHeight() / 2.0-BALL_RADIUS ,
                                      BALL_RADIUS * 2,BALL_RADIUS * 2);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);

    }

    /** Create a paddle*/
    GRect paddle = null;               // used to be able to change  anywhere in the class
    private void createPaddle() {
        paddle  = new GRect (getWidth() / 2.0 - PADDLE_WIDTH / 2.0,
                           getHeight() - PADDLE_Y_OFFSET,
                            PADDLE_WIDTH, PADDLE_HEIGHT );
        paddle.setFilled(true);
        paddle.setColor(Color.BLACK);
        add(paddle);
    }

    /**
     * Determines whether the ball touched the walls.
     *
     * @param ball The ball to test.
     * @return Whether it's touch the wall.
     */
    private boolean ballHitRightWall(GOval ball) {
        return ball.getX() + ball.getWidth() >= getWidth();
    }
    private boolean ballHitLeftWall(GOval ball) {
        return ball.getX() <= 0;
    }
    private boolean ballHitDownWall(GOval ball) {
        return ball.getY() + ball.getHeight() >= getHeight();
    }
    private boolean ballHitUpWall(GOval ball) {
        return ball.getY()  <= 0;
    }

}
