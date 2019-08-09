import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

/* Original Code for Platformer By Chris DeLeon
 * From: http://www.hobbygamedev.com/int/platformer-game-source-in-processing/
 * 
 * Project created originally in Processing - see Processing.org for more information!
 * 
 * Code modifications and extensions by Ginny Tice for Digital Media Academy
 * Project modified for Eclipse, reading in csv files, tile image use etc.
 * Project extensions include: life lost behavior, enemies, levels etc.
 * 
 * Images by Kenney at kenney.nl
 * Most images are from this link: http://arcade.academy/_static/platform_tutorial.zip
 */

public class Platformer extends PApplet {

	// we use this to track how far the camera has scrolled left/right or up/down
	float cameraOffsetX = 0;
	float cameraOffsetY = 0;
	
	boolean showLoseScreen; //false by default
	boolean showWinScreen; // false by default	

	Player thePlayer;
	World theWorld;
	int level;
	
	//TODO: uncomment later when you add an enemy!
	ArrayList<Enemy> enemyList;

	// by adding this to the player's y velocity every frame, we get gravity
	final float GRAVITY_POWER = 0.6f; // try making it higher or lower!

	public static void main(String[] args) {
		PApplet.main("Platformer");			
	}

	public void settings() {
		size(1000,480); 
	}

	public void setup() {

		level = 1;
		loadLevel("restart5.csv");

		
	

	}
	
	public void loadLevel(String fileName) {
		String[] lines = loadStrings(fileName);
		theWorld = new World(this, lines);
		theWorld.reload();
		
		PVector playerPosition = theWorld.getPlayerStartingPosition();
		thePlayer = new Player(this, theWorld, GRAVITY_POWER, playerPosition);
		thePlayer.reset();
		
		//TODO: populate enemy list
		enemyList = theWorld.getEnemyStartingPositions();

	}
	
	

	public void updateCameraPosition() {
		int rightEdge = theWorld.GRID_UNITS_WIDE * theWorld.GRID_UNIT_SIZE - width;
		int bottomEdge = theWorld.GRID_UNITS_TALL * theWorld.GRID_UNIT_SIZE - height;

		// the left side of the camera view should never go to the right of the above number
		// think of it as "total width of the game world"
		// minus "width of the screen/window" (width)
		
		//if world grid width is smaller than canvas width, reset right edge to 0
		//this way, game will be drawn at x=0 and not the right of the screen
		//this may be necessary if you want to do a vertical instead of horizontal platformer
		if (rightEdge < 0)
			rightEdge = 0;


		cameraOffsetX = thePlayer.position.x - width / 2;
		if (cameraOffsetX < 0) {
			cameraOffsetX = 0;
		}

		if (cameraOffsetX > rightEdge) {
			cameraOffsetX = rightEdge;
		}
		
		
		//TODO: add camera code so camera follows player along the y axis as well as x
		if(level == 3) {
		if (bottomEdge < 0)
				bottomEdge = 0;
			cameraOffsetY = thePlayer.position.y - height / 2;
			if (cameraOffsetY < 0) {
				cameraOffsetY = 0;
			}
	
			if (cameraOffsetY > bottomEdge) {
				cameraOffsetY = bottomEdge;
			}
		}
	}

	public void draw() {

		
		pushMatrix(); // lets us easily undo the upcoming translate call
		
		updateCameraPosition();
		
		//translate *temporarily* affects all upcoming graphics calls, until popMatrix
		//this translates the effective (0,0) point of the screen/canvas to this point
		//this means we can treat (0,0) as the top left corner of the whole world grid
		//*rather* than the top left corner of the canvas
		//this keeps the player right at the middle of the screen
		translate(-cameraOffsetX, -cameraOffsetY); 


		theWorld.drawWorld();

		thePlayer.inputCheck();
		int gameState = thePlayer.move();

		if (gameState == 2) { //all lives lost: reset whole game
			resetLevel();
			showLoseScreen = true;
		}
		//TODO: show win screen when player has moved onto goal block
		if (gameState == 1) {
			resetLevel();
			showWinScreen = true;
		}

		thePlayer.draw();
		for(Enemy e: enemyList) {
			e.move();
			e.draw();
			}
			
		//TODO: move and draw enemies
		
		
	    popMatrix(); // undoes the translate function from earlier in draw()	
	    
	    
	    //****Canvas Persistence Section****
	    //Anything that should be drawn on the canvas 
	    //regardless of where the player is on the world grid goes here, **after** popMatrix()

	    
	    
	   
	    
	    //text should be drawn at the bottom of the world grid 
	    //regardless of canvas size
	    int textHeight;
	    if (height < theWorld.GRID_UNIT_SIZE*theWorld.GRID_UNITS_TALL)
	    	textHeight = height;
	    else
	    	textHeight = theWorld.GRID_UNIT_SIZE*theWorld.GRID_UNITS_TALL;
	   
	    
	    
	    //TODO: draw number of coins, score, and lives
	    
	    
	    textAlign(LEFT);
	    outlinedText("Coins:"+thePlayer.coinsCollected+"/"+theWorld.coinsInStage,8, textHeight-10);
	    
	    textAlign(CENTER);
	    outlinedText("Score:"+thePlayer.score,width/2 , textHeight-10);
	    
	    textAlign(RIGHT);
	    outlinedText("Lives:" +thePlayer.lives,width-8, textHeight-10);
	    
	    //TODO: if player lost, cover whole screen (change me!)
	    if (showLoseScreen) {
	    	background(0);
	    	textSize(300);
	    	textAlign(CENTER);
	    	fill(255,0,0);
	    	text("LOSER",width/2,height/2+120);
	    }
	    
	    //TODO: if player won, cover whole screen (change me!)
	    if (showWinScreen) {
	    	background(255);
	    	textSize(60);
	    	textAlign(CENTER);
	    	fill(255,0,0);
	    	text("you win.. press ENTER for level 2..",width/2,height/2+30);
	    }

	}
	
	
	//use this method to draw text on the screen
	//because it is outlined; it's guaranteed to be readable!
	public void outlinedText(String sayThis, float atX, float atY) {
		  textSize(30);
		  fill(0); // black for the upcoming text, drawn in each direction to make outline
		  text(sayThis, atX-1,atY);
		  text(sayThis, atX+1,atY);
		  text(sayThis, atX,atY-1);
		  text(sayThis, atX,atY+1);
		  fill(255); // white for this next text, in the middle
		  text(sayThis, atX,atY);
		}

	

	public void resetLevel() {

		thePlayer.lives = 0; //trigger reset of player fields
		thePlayer.reset(); // reset the coins collected number, etc.

		theWorld.reload(); // reset world map
		
		//TODO: reset enemies

	}

	public void keyPressed() {

		if (keyCode == UP &&	showLoseScreen == false  && showWinScreen==false) {
			thePlayer.holdingUp = true;
		}
		//TODO: set holdingLeft and holdingRight variables
		if (keyCode == LEFT &&	showLoseScreen == false && showWinScreen==false) {
			thePlayer.holdingLeft = true;
		}
		if (keyCode == RIGHT &&	showLoseScreen == false && showWinScreen==false) {
			thePlayer.holdingRight = true;
		}if(showWinScreen && keyCode == ENTER ) { //go to next level
			showWinScreen = false;
			level++;

			//if max level, what should you do????
			//show highest level????
			//show final win screen????
			//if (level > 2)
			// level = 2;

			if (level == 2)
			loadLevel("2ndStage5.csv");

			if(level == 3)
				loadLevel("3rdStage53.csv");
			}
			
		
		//TODO: press 'r' to restart level
		if((showLoseScreen || showWinScreen)   &&  
				(key == 'r' || key == 'R')) {  //restart level
			showLoseScreen = false;
			showWinScreen = false;
		}
		
		//TODO: press enter to go to next level
		if(showWinScreen && keyCode == ENTER ) { //go to next level
		
		}
		if(key == 's') {
			//save screenshot
			saveFrame("platformer-pic###.png");
		}
	}

	public void keyReleased() {
		if (keyCode == UP) {
			thePlayer.holdingUp = false;
		}
		//TODO: set holdingLeft and holdingRight variables
		if (keyCode == LEFT) {
			thePlayer.holdingLeft = false;
		}
		if (keyCode == RIGHT) {
			thePlayer.holdingRight = false;
		}
	}

}
