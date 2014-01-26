import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JOptionPane;
//TYLER ADAMS
//THURSAY, APRIL 18th, 2013
//Secondary platform and primary end object class
public class Item{
	private int x, y, dx, radius;
	private boolean isHole;													// its not a hole for the golf ball
	private static boolean leveldone=false;
	private static int scoreFour=0;												//temporary score for level 4
	private StartingPoint sp;
	static String []showsc=new String[4];									// string that contains the score of current level, set to default (friendly) so that other classes can see it without a getter/setter
	public Item(){
	}

	public Item(int x){
		this.x = x;
		radius = 10;
	}

	public Item(int i, int j) {
		this.x=i;
		this.y=j;
		radius =10;
		this.isHole=false;
	}

	public Item(int i, int j, boolean isHole, int radius) {
		this.x=i;
		this.y=j;
		this.isHole=isHole;
		this.radius =radius;
	}

	public void update(StartingPoint sp, Ball b){
		this.sp=sp; 											// the private sp = sp getting passed in
		if(isHole==false){
			checkForCollision(b);
		}else{
			checkForCircleCollision(b);
		}
	}

	private void checkForCircleCollision(Ball b) {
		int ballX=b.getX();
		int ballY=b.getY();
		int ballR= b.getRadius();
		//Pythagorean
		int a = x - ballX;
		int bb = y - ballY; 									// ball object also labeled b so a +b is a+bb instead
		int collide = radius + ballR; 							// radius of item class + radius of ball
		// if distance between these 2 balls is < collide, collision has occured
		double c = Math.sqrt((double)(a*a) + (double) (bb*bb)); // distance between centres of objects (2 balls)
		if( c < collide){									 	// collision has occured
			int ddy=(int) b.getDy();
			b.setDy(ddy*-0.99);
			if( ballX > x){ 									// on right half of circle
				int ddx= (int)b.getDx();
				if(sp.level==4){								// for circular bumpers
					//ddx+=7;
					ddx+=1;
				}else{
					ddx +=1;
				}
				b.setDx(ddx);
			}else{ 												// on left half of circle
				int ddx= (int)b.getDx();
				if(sp.level==4 && sp.initBounce==true){								// for circular bumpers
					ddx-=7;
					b.setDy(45);
					sp.initBounce=false;
				}else{
					ddx -=1;
				}
				b.setDx(ddx);
			}
		}
	}

	private void checkForCollision(Ball b) {
		int ballX=b.getX();
		int ballY=b.getY();
		int ballR= b.getRadius();
		//Pythagorean
		int a = x - ballX;
		int bb = y - ballY; 									// ball object also labeled b so a +b is a+bb instead
		int collide = radius + ballR; 							// radius of item class + radius of ball
		// if distance between these 2 balls is < collide, collision has occured
		double c = Math.sqrt((double)(a*a) + (double) (bb*bb)); // distance between centres of objects (2 balls)
		if( c < collide){										// collision has occured
			performAction(b);
		}
	}

	private void performAction(Ball b) {
		int score = sp.getScore();
		if(sp.level<4){
			if(score==1){  											// array values are sp.level-1 because this array is zero indexed
				showsc[sp.level-1]="Hole in one!";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
			else if(score==2){
				showsc[sp.level-1]="Albatross";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
			else if(score==3){
				showsc[sp.level-1]="Eagle";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
			else if(score==4){
				showsc[sp.level-1]="Birdie";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
			else if(score==5){
				showsc[sp.level-1]="Par";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
			else{
				showsc[sp.level-1]=score-5+" over Par";
				++sp.level;										// move to next level
				sp.levelup=true;
			}
		}else{			//level 4
			scoreFour=scoreFour+10;
			showsc[3]=scoreFour +"";
			int ddy=(int) b.getDy();
			b.setDy(ddy*-1);
			int ballX=b.getX();
			double ballDX=b.getDx();
			if(ballX<x-radius || ballX>x+radius){ // hit either edge of the pinball bouncer
				ballDX*=-1; 						// bounce off
			}
			b.setDx(ballDX);
		}
		
	}

	public void paint(Graphics g){
		if(isHole==false){
			g.setColor(Color.RED);
		}else{
			g.setColor(Color.MAGENTA);
		}
		g.fillOval(x-radius, y-radius, radius*2, radius*2);

	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public static boolean getLeveldone() {
		return leveldone;
	}

	public static void setLeveldone(boolean leveldonee) {
		leveldone = leveldonee;
	}

	public static String[] getShowsc() {
		return showsc;
	}

	public void setShowsc(String[] showsc) {
		this.showsc = showsc;
	}
}