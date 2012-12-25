/**
 * 
 */
package src;

/**
 * @author YUAN
 *
 */
import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/* Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/* Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/* Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/* Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/* Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/* Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/* Separation between bricks */
	private static final int BRICK_SEP = 4;

/* Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/* Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/* Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/* Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/* Number of turns */
	private static final int NTURNS = 3;

	
/* Runs the Breakout program. */
	public void run() {
		// You fill this in, along with any subsidiary methods
		gameStart();
		gameing();
		gameOver();
	}
	
	/**
	 * Set up all the bricks.
	 */
	private void setUpBricks(){
		GRect[][] brick=new GRect[10][10];
		for(int j=0;j<10;j++){
			for(int i=0;i<10;i++){
				brick[i][j]=new GRect((i*(BRICK_WIDTH+BRICK_SEP)),
						(BRICK_Y_OFFSET+j*(BRICK_HEIGHT+BRICK_SEP)),
						BRICK_WIDTH,BRICK_HEIGHT);
				add(brick[i][j]);
				brick[i][j].setFilled(true);
				switch(j){
				case 0:
				case 1:brick[i][j].setColor(Color.RED);break;
				case 2:
				case 3:brick[i][j].setColor(Color.ORANGE);break;
				case 4:
				case 5:brick[i][j].setColor(Color.YELLOW);break;
				case 6:
				case 7:brick[i][j].setColor(Color.GREEN);break;
				case 8:
				case 9:brick[i][j].setColor(Color.CYAN);break;
				//default:brick[i][j].setColor(Color.BLACK);
				}
			}
		}
	}
	
	/**
	 * Set up the paddle.
	 */
	private void setUpPaddle(){
		paddle=new GRect((WIDTH-PADDLE_WIDTH)/2,HEIGHT-PADDLE_Y_OFFSET,
				PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	/**
	 * Add mouse listener to control the paddle.
	 */
	private void movePaddle(){
		addMouseListeners();
		
	}

	public void mousePressed(MouseEvent e){
		last= new GPoint(e.getX(),HEIGHT-PADDLE_Y_OFFSET);
		gobj= getElementAt(last);
	}
	
	public void mouseDragged(MouseEvent e){
		if(gobj !=null){
			gobj.move(e.getX()-last.getX(), 0);
			last=new GPoint(e.getX(),HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	
	public void mouseClicked(MouseEvent e){
		if(gobj!=null)
			gobj.sendToFront();
	}

	/**
	 * Prevent the paddle from moving off the edge of the window.
	 * Ensure the entire paddle is visible in the window.
	 */
	private void paddleOutOfBoundsPrevention(){
		if(paddle.getX()<=0){
			gobj.move(0-paddle.getX()+0.1, 0);
		}else if(paddle.getX()+PADDLE_WIDTH>=400){
			gobj.move(-2-(paddle.getX()+PADDLE_WIDTH-400), 0);
			}
	}
	
	/**
	 * Create a ball.
	 * @param x: Ball center, X-coordinate
	 * @param y: Ball center, Y-coordinate
	 * @return ball
	 */
	private GOval createBall(double x,double y){
		//radius is a constant
		GOval circle=new GOval(x-BALL_RADIUS,y-BALL_RADIUS,
				2*BALL_RADIUS,2*BALL_RADIUS);
		return circle;
	}
	
	/**
	 * Put the ball on the window.
	 */
	private void putBall(){
		ball=createBall(200,300);
		ball.setColor(Color.BLUE);
		ball.setFilled(true);
		add(ball);
	}
	
	/**
	 * Game start.
	 */
	private void gameStart(){
		setUpBricks();
		setUpPaddle();
		putBall();
		waitForClick();
		
	}
	
	/**
	 * Game-ing.
	 */
	private void gameing(){
		movePaddle();
		moveBall();
	}
	
	/**
	 * Ball movement.
	 * Get the ball to bounce.
	 */
	private void moveBall(){
		setRandomVx();
		setVy(INITIAL_Y_VELOCITY);
		while(true){
			/*try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			paddleOutOfBoundsPrevention();
			pause(THREAD_SLEEP_TIME);
			ball.move(vx,vy);
			if(hitUpBounds()) {
				bounceClip.play();
				setVy(-vy);
				}
			else if(hitLeftRightBounds()){
				bounceClip.play();
				setVx(-vx);
				}
			else if(hitDownBounds()||gameWon())
				break;
			collision();
		}
	}
	
	/**
	 * Set the x velocity.
	 * @param x: X velocity.
	 */
	private void setVx(double x){
		this.vx=x;
	}
	
	/**
	 * Set a random x velocity.
	 * @param x: X velocity.
	 */
	private void setRandomVx(){
		this.vx=rgen.nextDouble(MIN_X_VELOCITY,
				MAX_X_VELOCITY);
		if(rgen.nextBoolean()) vx=-vx;
	}
	
	/**
	 * Set the y velocity.
	 * @param y: Y velocity.
	 */
	private void setVy(double y){
		this.vy=y;
	}
	
	/**
	 * @return true: If the ball hits the upper bound;
	 */
	private boolean hitUpBounds(){
		return ball.getY()<=0;
	}
	
	/**
	 * @return true: If the ball hits the lower bound;
	 */
	private boolean hitDownBounds(){
		return ball.getY()+2*BALL_RADIUS>=600;
	}
	
	/**
	 * @return true: If the ball hits the left or right bound;
	 */
	private boolean hitLeftRightBounds(){
		return ball.getX()<=0||
					ball.getX()+2*BALL_RADIUS>=400;
	}
	
	/*
	private void collision(){
		//GObject obj=getElementAt(ball.getX(),(ball.getY()));//+BALL_RADIUS
		GObject corner1=getElementAt( ball.getX() , ball.getY() );		//left-up
		GObject corner2=getElementAt( (ball.getX()+2*BALL_RADIUS) , ball.getY());		//right-up
		GObject corner3=getElementAt( (ball.getX()+2*BALL_RADIUS) , (ball.getY()+2*BALL_RADIUS) );		//right-down
		GObject corner4=getElementAt( ball.getX(), (ball.getY()+2*BALL_RADIUS) );		//left-down
		
		if(corner1!=null&&corner1.getHeight()==BRICK_HEIGHT){
			remove(corner1);
			setVy(-vy);
		}else if(corner2!=null&&corner2.getHeight()==BRICK_HEIGHT){
			remove(corner2);
			setVy(-vy);
		}else if(corner3!=null&&corner3.getHeight()==BRICK_HEIGHT){
			remove(corner3);
			setVy(-vy);
		}else if(corner3!=null&&corner3.getHeight()==BRICK_HEIGHT){
			remove(corner4);
			setVy(-vy);
		}else if(corner3!=null&&corner3.getHeight()==PADDLE_HEIGHT){
			setVy(-vy);
		}else if(corner4!=null&&corner4.getHeight()==PADDLE_HEIGHT){
			setVy(-vy);
		}
	}*/
	/*
		if(obj!=null&&obj.getHeight()==BRICK_HEIGHT){
			remove(obj);
			setVy(-vy);
		}else if(obj!=null&&obj.getHeight()==PADDLE_HEIGHT){
			setVy(-vy);
		}
		*/
	
/**
 * This method gives the collision strategy.
 */
	private void collision(){
		GObject collider=getCollidingObject();
		
		if(collider!=null&&collider.getHeight()==BRICK_HEIGHT){
			bounceClip.play();
			remove(collider);
			counter--;
			setVy(-vy);
		}else if(collider!=null&&collider.getHeight()==PADDLE_HEIGHT
				&&(collider==getElementAt( (ball.getX()+2*BALL_RADIUS) , (ball.getY()+2*BALL_RADIUS) )||
						collider==getElementAt( ball.getX(), (ball.getY()+2*BALL_RADIUS) )) ){
//If the object is the paddle, only check corner3 and corner4 to change the y velocity.
			//If still check corner1 and corner2, bugs(glued balls)will happen.
			bounceClip.play();
			setVy(-vy);
			ball.setColor(rgen.nextColor());
		}
	}
	
	/**
	 * 
	 * @return :The colliding object.
	 */
	private GObject getCollidingObject(){
		GObject corner1=getElementAt( ball.getX() , ball.getY() );		//left-up
		GObject corner2=getElementAt( (ball.getX()+2*BALL_RADIUS) , ball.getY());		//right-up
		GObject corner3=getElementAt( (ball.getX()+2*BALL_RADIUS) , (ball.getY()+2*BALL_RADIUS) );		//right-down
		GObject corner4=getElementAt( ball.getX(), (ball.getY()+2*BALL_RADIUS) );	//left-down
		
		if(corner1!=null){
			return corner1;
		}else if(corner2!=null){
			return corner2;
		}else if(corner3!=null){
			return corner3;
		}else if(corner4!=null){
			return corner4;
		}else {
			return null;
		}
		
	}
	
	/**
	 * Game over.
	 */
	private void gameOver(){
		if(gameWon()){
			removeAll();
			GLabel gameOverSign=new GLabel("Game Won! Nice!", 20, 300);
			gameOverSign.setFont("SansSerif-36");
			gameOverSign.setColor(Color.BLACK);
			add(gameOverSign);
		}else{
			removeAll();
			GLabel gameOverSign=new GLabel("Game Over! Loser!", 20, 300);
			gameOverSign.setFont("SansSerif-36");
			gameOverSign.setColor(Color.BLACK);
			add(gameOverSign);
		}
		
	}
	
	/**
	 * 
	 * @return :True if game won.
	 */
	private boolean gameWon(){			
		return counter==0;
	}
	
	
	
	private GPoint last;
	private GObject gobj;
	GRect paddle;
	GOval ball;
	private double vx,vy;
	private RandomGenerator rgen=RandomGenerator.getInstance();
	private static final double MIN_X_VELOCITY=1.0;
	private static final double MAX_X_VELOCITY=3.0;
	private static final double INITIAL_Y_VELOCITY=5.0;
	private static final int TOTAL_NUMBER_OF_BALLS=100;
	private static final int THREAD_SLEEP_TIME=20;
	private int counter=TOTAL_NUMBER_OF_BALLS;
	
	AudioClip bounceClip=MediaTools.loadAudioClip("bounce.au");
	

}

