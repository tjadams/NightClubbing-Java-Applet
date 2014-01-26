import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Random;
//TYLER ADAMS
//THURSAY, APRIL 18th, 2013
//Main platform object class
public class Platform {
	private StartingPoint sp;
	private static boolean canLaunch=false;		// if you can launch
	static boolean isLaunch=false;		// is the specified platform a launcher , default visibility because im too lazy to add extra getter/setter lines
	private static boolean isBumper=false;		// is the specified platform a bumper
	int dx;
	int x, y, width, height;

	public Platform(){
		x = 300;
		y = 300;
		width = 120;
		height = 40;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	public Platform(int x, int y){
		this.x=x;
		this.y=y;
		width = 120;
		height = 40;
	}

	public Platform(int x, int y, boolean launcher, boolean bumper){
		this.x=x;
		this.y=y;
		width = 120;
		height = 40;
		this.isLaunch=launcher;
		this.isBumper=bumper;
	}

	public void update(StartingPoint sp, Ball b){

		checkForCollision(b);
	}

	private void checkForCollision(Ball b) {
		int ballX=b.getX();
		int ballY=b.getY();
		int radius = b.getRadius();
		//TOP / BOTTOM OF RECTANGLE COLLISION CHECK

		if (ballY + radius > y && ballY+radius <y + height + 2*radius) { // if bottom of ball within range of rectangle top and bottom +radius
			if(ballX > x && ballX< x + width){
				double newDY=b.getDy()*-1; 								// changing direction realistically
				if(sp.level==2 ){
					if(Math.abs(newDY)<10){
						newDY=0;
					}
				}else if(sp.level==3){
					if(Math.abs(newDY)<2){ 								// if moving really slow, stop
						newDY=0;
					} else{												// otherwise reduce speed ( less than energy loss because it's a longer platform)
						newDY*=0.9;										// low friction because on grass
					}

				}else if(sp.level==4){
					//newDY=0;
					if(isLaunch==true){
						if(Math.abs(newDY)<10){
							newDY=0;
						}
					}
				}
				b.setDy(newDY);
			}
		}
		if(sp.level!=3){											// gives complications
			int a = x - ballX;
			int bb = y - ballY; 									// ball object also labeled b so a +b is a+bb instead
			int collideleft = width/4; 								// lowest distance for collision to occur
			// if distance between these 2 balls is < collide, collision has occured
			double c = Math.sqrt((double)(a*a) + (double) (bb*bb)); // distance between centres of objects (2 balls)
			if( c  < collideleft){ 									// collision has occured
				double newDX=b.getDx()*-1;
				b.setDx(newDX);
			}
		}
	}
	public void paint(Graphics g){									// draw the platform
		g.setColor(Color.YELLOW);
		g.fillRect(x, y, width, height);							
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		return y;
	}
}