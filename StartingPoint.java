import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

//TYLER ADAMS
//THURSAY, APRIL 18th, 2013
// Main engine class

// APPLET SOMETIMES GLITCHES TO IT'S BUILT-IN SIZE, SHOULD BE SET TO 900,800

//right side of rectangle collisions is the only side that doesn't work, 

//for some reason, the title image won't allign properly unless it's a button
//so i made it a button and then tried to disable 
//it in the hopes to of retaining the functionality of a JLabel, but it didn't work

//forcebar line draws over other shapes (yellow rectangle, balls etc) but doesn't really matter
//because its repainted immediately after it is drawn again


public class StartingPoint extends Applet implements Runnable, ActionListener , MouseListener, MouseMotionListener, KeyListener{
	private JButton start,help,exit,title,helppic;				// buttons / pictures in help and title screens
	private static JFrame happ = new JFrame("How to Play");		// help screen frame
	private static JFrame app = new JFrame("NightClubbing");	// title screen frame
	private boolean drag =false;								// to check whether or not the mouse is being dragged
	private Image i, club, imghelp, background3;				// entire screen as an image for use in double buffering, club cursor, help image, level 3 background
	private Graphics doubleG;									// the graphics component for double buffering
	private int mx=0,my=0,mx2=0,my2=0; 							// initial and final mouse positions
	private int score=0;										// temporary score for current level
	private int totalscore=0;									// total score for current session
	private boolean MouseHeld=false; 							// to prevent an old line state from being restored after the ball stops moving
	private boolean canGo=false;    							// golf check (obviously cant hit ball in the air more than once)
	private double a,bb,theta, pythag;							// a and bb are for use in getting the angle for theta and the amount of force in pythag	
	static boolean initial=false;
	static boolean levelup=false;
	private static boolean canLaunch=true;						// for pinball: has level 4 just started?
	private static boolean diffFlip=false;						// start off with standard flippers
	static boolean initBounce=false;							// determines whether or not the ball will bounce off a certain item. Set to default to allow easy access without getters and setters
	private static int fSpeed=70; 								// speed of flipper movement
	static int level = 1;
	Toolkit toolkit = Toolkit.getDefaultToolkit();				// toolkit to get dimensions of screen

	File f;														// for use in high scores
	FileWriter fw;
	BufferedWriter bw;
	PrintWriter pw;	

	Ball b,b2,b3,b4;												// different balls for each level
	Platform p[] = new Platform[12];								// different platforms for each level
	Item item[] = new Item[30];									// different circle platforms and end game holes for each level

	public StartingPoint() throws IOException {
		//level=4;

		// set up the initial frame	
		app.setSize(900, 800);
		app.setBackground(Color.WHITE);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		app.setVisible(true);
		app.setLayout(new BorderLayout());

		//load the title image onto a jbutton in order ot be used with borderyLayout
		Image imgt = ImageIO.read(getClass().getResource("images/title.png"));
		title = new JButton();
		title.setBackground(Color.WHITE);
		title.setIcon(new ImageIcon(imgt));
		app.add(title, BorderLayout.PAGE_START);
		app.validate();

		//load the play button
		Image img = ImageIO.read(getClass().getResource("images/play.png"));
		start = new JButton();
		start.addActionListener(this);
		start.setIcon(new ImageIcon(img));
		start.setBackground(Color.WHITE);
		app.add(start,BorderLayout.CENTER);
		app.validate();

		// load the help button
		Image imgh = ImageIO.read(getClass().getResource("images/helptitle.png"));
		help = new JButton();
		help.addActionListener(this);
		help.setIcon(new ImageIcon(imgh));
		help.setBackground(Color.WHITE);
		app.add(help,BorderLayout.LINE_START);
		app.validate();

		//load the exit button
		Image imge = ImageIO.read(getClass().getResource("images/exit.png"));
		exit = new JButton();
		exit.addActionListener(this);
		exit.setIcon(new ImageIcon(imge));
		exit.setBackground(Color.WHITE);
		app.add(exit,BorderLayout.LINE_END);
		app.validate();

		// LOAD THE HELP FRAME
		happ.setBackground(Color.BLACK);
		happ.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		happ.setSize(900, 800);
		happ.setLayout(new BorderLayout());
		helppic = new JButton();
		helppic.setBackground(Color.BLACK);
		imghelp = ImageIO.read(getClass().getResource("images/help80.png"));
		helppic.setIcon(new ImageIcon(imghelp));
		helppic.addActionListener(this);
	}

	public static void main(String args[]) throws IOException {
		new StartingPoint();					//initialize the title screen
	}

	@Override
	public void init() {
		setName("NightClubbing");				 //initialize the applet
		setSize(800,600);
		setBackground(Color.black);
		//add golf image as cursor to the current point
		try {
			club = ImageIO.read(new File("images/golfclubtrans.png"));

		}
		catch (IOException e){
		}
		final java.awt.Cursor myCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(club,new java.awt.Point(8,0),"test");
		setCursor(myCursor);
	}
	@Override
	public void start() {

		addMouseListener(this); 							// for drag use
		addMouseMotionListener(this);
		addKeyListener(this);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// LEVEL 1
		b=new Ball(0,dim.height-100); 						// objects must be created before thread starts
		p[0]= new Platform(175,400); 						// platforms
		p[1]= new Platform(290,300);
		p[2] = new Platform(450,200);
		p[3] = new Platform(600, 100);
		item[0] = new Item(720, 90); 						// ending hole

		//LEVEL 2
		b2=new Ball(55,20);
		b2.setDy(0);
		p[4] = new Platform(20,45); 						// initial platform
		item[1]= new Item(200,200,true,55); 				// circle platforms
		item[2]= new Item(325,300,true,100);
		item[3] = new Item (525,425,true,120);
		item[4] = new Item (780,600); 						// ending hole

		// level 3
		b3 = new Ball(780,222); 
		p[5]= new Platform(200,240);						 // initial platform
		p[5].setHeight(4);
		p[5].setWidth(600);
		item[5] = new Item(178,567); 						// ending hole

		b4= new Ball(715,533);
		b4.setGravity(12);
		p[6] = new Platform(710,550,true,false);					// create a launcher
		p[6].setWidth(20);
		p[6].setHeight(40);
		p[6].isLaunch=true;
		//p[7] = new Platform(300,200,false,true);					// create a bumper ( DOESNT DO ANYTHING
		item[6]= new Item(760,160,true,60);							// create the collision that will launch the ball into the playing field
		initBounce=true;
		item[7]= new Item(440+75,540,true,75);							// create an alternate flipper
		item[8]= new Item(220+75,540,true,75);							// create an alternate flipper
		p[8] = new Platform(470,550,false,false);					// create a flipper
		p[8].setWidth(150);
		p[9] = new Platform (220,550,false,false);					// create a flipper
		p[9].setWidth(150);

		item[9] = new Item(270+30,140,false,30); // adding the radius to the x value
		item[10]= new Item(490+30,140,false,30);
		item[11] = new Item(160+42,180,false,42);
		item[12] = new Item(580+42,180,false,42);
		item[13] = new Item(250,365,false,40);
		item[14] = new Item(580,365,false,40);
		
		p[10] = new Platform(200,65,false,false);
		p[10].setWidth(500);
		p[10].setHeight(20);
	
		item [15] = new Item(100,100,true,50);
		item [16] = new Item(100,200,true,50);
		item [17] = new Item(100,300,true,50);
		item [18] = new Item(100,400,true,50);
		item [19] = new Item(135,490,true,70);
		//item[20] = new Item (2330,550,true,40);
		
		
		item[21] = new Item(750,100,true,50);
		item[22] = new Item(750,200,true,40);
		item[23] = new Item(750,300,true,50);
		item[24] = new Item(750,400,true,50);
		item[25] = new Item(675,490,true,70);
		
		
		/*
		p[11] = new Platform(750,60,false,false);
		p[11].setWidth(20);
		p[11].setHeight(600);
		*/
		
		
		Thread thread = new Thread(this); 					// create a thread to run the game engine
		thread.start();
	}

	@Override
	public void update(Graphics g) {
		if(i==null){
			i=createImage(this.getSize().width, this.getSize().height); // refer to applet,
			doubleG=i.getGraphics();
		}

		// Double Buffering
		doubleG.setColor(getBackground());
		doubleG.fillRect(0, 0, this.getSize().width, this.getSize().height);
		doubleG.setColor(getForeground());
		paint(doubleG);
		g.drawImage(i, 0, 0, this);
	}
	@Override
	public void paint(Graphics g) {
		if(level==2){
			if(initial==true){
				g.dispose();								// reset
			}
			p[4].paint(g);
			b2.paint(g);
			item[1].paint(g);
			item[2].paint(g);
			item[3].paint(g);
			item[4].paint(g);
		}else if(level == 1){
			b.paint(g);
			p[0].paint(g);
			p[1].paint(g);
			p[2].paint(g);
			p[3].paint(g);
			item[0].paint(g);
		}else if(level ==3){
			if(initial ==true){
				g.dispose();
			}										
			//	SET BACKGROUND FOR APPLET
			try {
				background3 = ImageIO.read(getClass().getResource("images/course2.jpg"));
			} catch (IOException e) {
				System.out.println("course.jpg can't be found");
			}	
			g.drawImage(background3,  0 , 0 , getWidth() , getHeight() , this);
			b3.paint(g);
		}else if(level==4){
			if(initial==true){
				g.dispose();
			}
			try {
				background3 = ImageIO.read(getClass().getResource("images/pinballcourse2.jpg"));
			} catch (IOException e) {
				System.out.println("pinballcourse2.jpg can't be found");
			}	
			g.drawImage(background3,  0 , 0 , getWidth() , getHeight() , this);

			b4.paint(g);
			
			//p[7].paint(g);
			
			if(initBounce==true){
				p[6].paint(g);
				item[6].paint(g);
			}else{
				//p[11].paint(g);
				
				
				/*item[21].paint(g);// activate right boundaries
				item[22].paint(g);
				item[23].paint(g);
				item[24].paint(g);
				*/
				//item[25].paint(g);
				
			}
									//Dont paint the pinball bumpers
			/*item[9].paint(g);
			item[10].paint(g);
			item[11].paint(g);
			item[12].paint(g);
			item[13].paint(g);
			item[14].paint(g);
			
			p[10].paint(g);
			//p[11].paint(g);
			item[15].paint(g);
			item[16].paint(g);
			item[17].paint(g);
			item[18].paint(g);
			
			item[19].paint(g);
			
			//item[20].paint(g);
			*/
			//item[19].paint(g);
			if(diffFlip==true){
				item[7].paint(g);
				item[8].paint(g);
			}else{
				p[9].paint(g);
				p[8].paint(g);
			}
		}else{ // level >4 so the game is finished
			if(Item.showsc[3]==null){
				Item.showsc[3]="0";
			}
			int presults = Integer.parseInt(Item.showsc[3]);
			String results= "Congratulations! You have beaten the game with a mini-golf score of: " +totalscore;
			String spresults="Additionally, you have a bonus pinball score of: " +presults+ ". Thanks for playing!";	//"
			String results1= "Score from level one: "+Item.showsc[0];
			String results2= "Score from level two: "+Item.showsc[1];
			String results3= "Score from level three: "+Item.showsc[2];

			setBackground(Color.WHITE);
			Font font = new Font("MSReferenceSpecialty",Font.PLAIN,18);
			g.setFont(font);
			g.drawString(results, 55, 200);
			g.drawString(results1, 300, 220);
			g.drawString(results2, 300, 240);
			g.drawString(results3, 300, 260);
			g.drawString(spresults, 100, 280);
		}
		if(drag==true){
			// draw/update the force bar
			g.setColor(Color.white);
			g.drawLine(mx, my, mx2, my2);
			String angle =theta + " degrees";
			g.drawString(angle, mx, my-10);
			int frc = (int) (pythag/3);
			String force = "Power: "+frc;
			g.drawString(force, mx+3, my);
		}
		if(drag == false){								 //released
			g.setColor(getBackground());				// make all previous lines black
			g.drawLine(mx, my, mx2, my2);
		}
	}
	@Override
	public void run() {
		while(true){ 									// while the game is still running
			if(levelup==true){ 							// if you have just beaten a level
				if (level==2 || level==3 || level==4){ 			// initialize circle level or realistic level
					score=0;
					initial = true;
					repaint();
					initial = false;
					levelup = false;
				}else{
					// level is greater than 3 so call repaint and make it draw a string
					repaint();
					try {
						Thread.sleep(40000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					System.exit(0);															// no more levels so the game is done, close window
				}
			}

			if(level==1){
				if(Math.abs(b.getDx())<=Math.abs(1) && Math.abs(b.getDy())<=Math.abs(3)){   // if the ball is really slow, make it stop moving
					canGo=true; 															// the ball is stopped, you can have another turn
				}else{
					canGo=false; 															// the ball is in motion so you can't have another turn
				}
				b.update(this); 															// update the objects of level 1
				p[0].update(this, b);
				p[1].update(this, b);
				p[2].update(this, b);
				p[3].update(this, b);
				item[0].update(this, b);
			}else if(level==2){
				if(Math.abs(b2.getDx())<=Math.abs(1) && Math.abs(b2.getDy())<=Math.abs(3)){ 
					canGo=true;
				}else{
					canGo=false;
				}
				p[4].update(this, b2); 														// update the objects of level2
				b2.update(this);
				item[1].update(this, b2);
				item[2].update(this, b2);
				item[3].update(this, b2);
				item[4].update(this, b2);
			}else if(level==3){
				if(Math.abs(b3.getDx())<=Math.abs(1) && Math.abs(b3.getDy())<=Math.abs(3)){
					canGo=true;
				}else{
					canGo=false;
				}
				b3.update(this);															//update the objects of level3
				p[5].update(this, b3);
				item[5].update(this, b3);
			}else if(level==4){
				canGo=false;																// ensure that the ball cannot be moved by the force bar

				b4.update(this);
				if(initBounce==true){
					p[6].update(this, b4);
					//initBounce=true;				// give special paramaters to the initial bounce
				
					item[6].update(this, b4);
				}else{
					//p[11].update(this, b4);
					
					/*item[21].update(this, b4);// activate right boundaries
					item[22].update(this, b4);
					item[23].update(this, b4);
					item[24].update(this, b4);
					*/
					item[25].update(this, b4);
					
				}
				item[9].update(this, b4);
				item[10].update(this, b4);
				item[11].update(this, b4);
				item[12].update(this, b4);
				item[13].update(this, b4);
				item[14].update(this, b4);
				p[10].update(this, b4);
			
				/*item[15].update(this, b4); 
				item[16].update(this, b4);
				item[17].update(this, b4);
				item[18].update(this, b4);
				*/
				item[19].update(this, b4); // keep the rolling one
				
				//item[20].update(this, b4);
				if(diffFlip==true){
					item[7].update(this, b4);
					item[8].update(this, b4);
				}else{
					p[8].update(this, b4);
					p[9].update(this, b4);
				}
			}
			repaint(); 																		// after all objects are updated, redraw the screen
			try {
				Thread.sleep(17);													// 60 Frames per Second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent m) {

		if(canGo==true){ 															// INITIAL CLICK
			MouseHeld=true;
			mx = m.getX();
			my = m.getY();
		}
	}
	@Override
	public void mouseDragged(MouseEvent m) {
		if(canGo==true && MouseHeld==true){											 // just after pressed
			drag=true;
			//change next co-ordinates of mouse
			mx2= m.getX();
			my2 = m.getY();
			//start creating the force bar
			a = mx-mx2;
			bb = my-my2;
			theta = Math.toDegrees(Math.atan((bb)/(a)))*-1;							// get the angle the initial click makes with the final click
			theta=(double)Math.round(theta* 100) / 100; 							// round to the hundreds decimal point 
			pythag=Math.sqrt(a*a+bb*bb);
		}
	}
	@Override
	public void mouseReleased(MouseEvent m) {
		if(canGo==true && MouseHeld==true){ 									// if you can go and mouse was held prior to being released
			drag=false;
			MouseHeld=false; 													// let go of mouse

			// update speeds of level specific balls
			if(level==1){ 
				b.setDx(a/10);			// set the balls speed as a dividend of the horizontal force
				b.setDy(bb/4);			// set the balls speed as a dividend of the vertical force
			}else if(level==2){
				b2.setDx(a/10);
				b2.setDy(bb/4);
			}else if(level ==3){
				b3.setDx(a/10);
				b3.setDy(bb/4);
			}
			setScore(getScore() + 1); 											// increase score of current level
			totalscore++;														// increase total score of session
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==start){ 												// if start button was pressed, close the frame
			app.setVisible(false);
		}else if(e.getSource()==help){ 											// if help button was pressed, show the help frame
			// SET THE HELP FRAME VISIBLE
			happ.setVisible(true);
			happ.setBackground(Color.BLACK);									 // background matches background of helpPic
			happ.add(helppic, BorderLayout.CENTER); 
			happ.validate();													// update the layout of the screen
		}else if(e.getSource()==exit){											 // if exit button was pressed, close the game
			System.exit(0);
		}else if(e.getSource()==helppic){										 // if help button was pressed, close the help screen
			happ.setVisible(false);
		}

	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public void stop() {
	}
	@Override
	public void destroy() {
	}
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent m) {

	}

	@Override
	public void mouseEntered(MouseEvent m) {
	}
	@Override
	public void mouseExited(MouseEvent m) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(level==4){
			if(canLaunch==true){
				if(e.getKeyCode()==KeyEvent.VK_SPACE){
					b4.setDy(-125); // go up
					canLaunch=false;		// you have launched the ball so you don't want to be able to launch again
				}
			}
			if(e.getKeyCode()==KeyEvent.VK_SHIFT){			// transform a rectangle flipper to a circle flipper and vice-versa
				if(diffFlip==false){						
					diffFlip=true;							// change to circle if currently rectangle
				}else{
					diffFlip=false;							// change to rectangle if currently circle
				}
			}
			
			/*
			if(e.getKeyCode()==KeyEvent.VK_RIGHT){						// shift current flippers right
				int nextX=p[8].getX()+fSpeed;							// add the speed of flipper movement to the current position	
				int nextX2=p[9].getX()+fSpeed;
				if(nextX<this.getWidth() && nextX2<this.getWidth()){	// right horizontal boundaries of screen
					p[8].setX(nextX);
					p[9].setX(nextX2);
					item[7].setX(nextX+70);
					item[8].setX(nextX2+70);
				}
			}
			if(e.getKeyCode()==KeyEvent.VK_LEFT){						// shift current flippers left
				int nextX=p[8].getX()-fSpeed;
				int nextX2=p[9].getX()-fSpeed;
				if(nextX>0){// boundaries of screen
					item[7].setX(nextX+70); // + radius
					item[8].setX(nextX2+70);
					p[8].setX(nextX);
					p[9].setX(nextX2);
				}
			}
			*/
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { // mandatory unimplemented methods
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}