import java.awt.Color;
import java.awt.Image;

import java.awt.Graphics;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

//TYLER ADAMS
//THURSAY, APRIL 18th, 2013
//Main object class

public class Ball {
	private double Gravity = 15;			// gravitational constant ( kind of like weight of the specific ball) 
	private double Energyloss = 0.65; 			// vertical friction multiplier, may change depending on level
	private double dt = .2; 					// change in time
	private double xFriction = 0.7;				// horizontal friction multiplier, may change depending on level
	private int x=400; 							// initial x,y position if constructor is passed nothing
	private int y=25;
	private double dx=0; 						// change in x
	private double dy=0; 						// change in y
	private double GameDy=-100; 				// for occasional use in game physics
	private int Radius =20; 					// initial Radius 
	Image golfball;								// the image of the realistic looking golf ball
	public Ball(int i, int j) { 				// load position of ball
		x=i;
		y=j;
	}

	public double getGameDy() {
		return GameDy;
	}

	public void setGameDy(double gameDy) {
		GameDy = gameDy;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getGravity() {
		return Gravity;
	}

	public int getRadius() {
		return Radius;
	}

	public void setRadius(int Radius) {
		this.Radius = Radius;
	}

	public Ball() {
	}

	public void moveRight(){
		if(dx+1<10){ 								// if next increment of speed is less than max speed 
			dx+=1;									// increment speed
		}
	}

	public void moveLeft(){
		if(dx-1>-10){
			dx-=1;
		}
	}

	public void update(StartingPoint sp){
		int xBound=0; // initialize boundaries
		int yBound=0;
		int xBoundLeft=0;
		if(sp.level==4){ // have different boundaries
			xBound=750; // just after the ball
			yBound=sp.getHeight();
			xBoundLeft=70;
			if(x<135){
				dx+=4;
			}
		}
		else{
			xBound=sp.getWidth();
			yBound = sp.getHeight();
			xBoundLeft=0+Radius;
		}
			if(x + dx > xBound-Radius-1){ 		//-Radius to prevent being cut off half way
				x=xBound-Radius-1; 				// -1 because > 800 so it must be 799 and drawing canvas starts at 0 (total 800)
				dx = -dx;
				dx*=xFriction;
			}else if(x+dx<xBoundLeft){ 					// if going towards the left bounds of the screen
				x=xBoundLeft;								// reset position to the edge of the left side of the screen and bounce off
				dx=-dx;
				dx*=xFriction;
			}else{										// keep moving
				x+=dx;
			}

			if(y<0){									// if collision occured with the top of the screen
				y=0;
				dy *= Energyloss; 						// making it lose energy every time it bounces
				dy=-dy;
			}

			// collision check with top of the screen
			if(y==yBound-Radius-1){				// bottom of applet, deduct horizontal direction
				if(sp.level==4){
					JOptionPane.showMessageDialog(null, "You have hit the bottom and lost your ball. Game Over!");
					sp.level+=1;
				}
				dx*=xFriction;
				if(Math.abs(dx) < 0.8){ 				// velocity can be negative so must use absolute value
					dx=0;
				}
				if(Math.abs(dy) <7){					 // make it stop bouncing after a while 
					dy=0;
					GameDy=0;
				}
			}

			if(y>yBound - Radius - 1){
				y=yBound-Radius-1;
				dy *= Energyloss; 						// making it lose energy every time it bounces
				dy=-dy;
			}else{ 										// if you havent hit the bottom of the screen yet

				// velocity formula
				dy+=getGravity()*dt; 						// increase velocity towards the bottom of the screen

				// position formula
				y+= dy*dt + 0.5*getGravity()*dt*dt; 			// keep moving towards the bottom of the screen
			}
		}
	

	public double getxFriction() {
		return xFriction;
	}


	public double getEnergyloss() {
		return Energyloss;
	}


	public void paint(Graphics g){
		if(StartingPoint.level>=3){					 // if current level is 3
			g.setColor(Color.WHITE);				 // make the ball look like a real golf ball
			Radius=9;								 // half the x or y dimension of the image
			try {
				golfball = ImageIO.read(getClass().getResource("images/golfballcopy2.png"));
			} catch (IOException e) {
				System.out.println("image won't load");
			}	
			g.drawImage(golfball, x-Radius, y-Radius, Radius*2, Radius*2,null); // x,y is in the top left corner of the image so subtracting the radius will make x,y the ball's center, radius*2 is height and width, null is for imageobserver
		}else{
			g.setColor(Color.GREEN);							// create the game image
			g.fillOval(x-Radius, y-Radius, Radius*2, Radius*2);
		}
	}

	public void setGravity(double gravity) {
		this.Gravity = gravity;
	}
}