import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Enemy {
	// PVector contains two floats, x and y
	//position refers to the (x,y) point in the *middle* of the enemy
	PVector position; //current position of the enemy
	PVector velocity;  //current velocity of the enemy

	PImage enemyImage;
	
	PApplet p;


	int initialFrameCount;

	//force of player movement on ground, in pixels/cycle
	static float INITIAL_RUN_VELOCITY; 

	World theWorld;

	PVector startingPosition;

	int enemyHeight;
	int enemyWidth;
	int offsetToGround; //so that enemy can have a different height than the grid square size

	public Enemy(PApplet startp, World startWorld, PVector startPosition, String imageName, double runVelocity, int sizeOffset) {

		p = startp;
		theWorld = startWorld;
		startingPosition = startPosition;
		enemyImage = p.loadImage(imageName);		
		INITIAL_RUN_VELOCITY = (float)runVelocity;
		
		position = new PVector();
		velocity = new PVector();

		velocity.x = INITIAL_RUN_VELOCITY;
		//no y velocity in this project: enemy only moves horizontally		
		

		//enemy generally should be shorter than the player and should fit into a grid square	
		//if you want the enemy to be *smaller* (or larger?) than the grid square,
		//then create an enemy object with a sizeOffset other than zero
		//a negative offset will make the enemy smaller
		//a positive offset will make the enemy bigger
		enemyHeight = theWorld.GRID_UNIT_SIZE + sizeOffset;
		//scale proportionally
		double playerWidthDouble = enemyHeight * enemyImage.width / (double) enemyImage.height;
		enemyWidth = (int) Math.round(playerWidthDouble);	
		
		
		//this offset will be added to the enemy position
		//so that if enemy is shorter than grid square,
		//it is still on the ground
		offsetToGround = (theWorld.GRID_UNIT_SIZE - enemyHeight)/2;

		reset();
	}
	
	
	
	public void reset() {

		velocity.x = INITIAL_RUN_VELOCITY;
		velocity.y = 0;
		position.x = startingPosition.x;
		position.y = startingPosition.y + offsetToGround;

	}
	
	
	

	public void move() {

		position.add(velocity);

		// check for the enemy bumping into a wall 
		// OR the enemy getting to the edge of a platform
		
		//this will be simpler than player collision code
		//the player moves in x and y directions
		//but we're just writing a horizontal enemy here, 
		//so checking the right and left edges of the enemy is good enough
		
		//calculate the four edges of the enemy
		//corresponds to the tile next to the enemy
		//and diagonally below the enemy
		PVector rightEdge = new PVector(position.x+enemyWidth/2, position.y);
		PVector rightEdgeBelow = new PVector(position.x + enemyWidth/2, position.y + theWorld.GRID_UNIT_SIZE);
		PVector leftEdge = new PVector(position.x-enemyWidth/2, position.y);
		PVector leftEdgeBelow = new PVector(position.x-enemyWidth/2, position.y + theWorld.GRID_UNIT_SIZE);
		
		//if enemy is going right, check *right* edge of enemy
		if (velocity.x > 0) {
			
			//when enemy bumps directly into a solid tile
			if (theWorld.isSolidTileAt(rightEdge) || theWorld.isKillBlockAt(rightEdge)) {
				//negate the velocity - go left
				velocity.x*=-1;
			}
			//when enemy comes to the edge of a platform, turn around
			else if (theWorld.isEmptyTileAt(rightEdge) &&
					theWorld.isEmptyTileAt(rightEdgeBelow)) {
				velocity.x*=-1;
			}
			
		}
		//if enemy is going left, check *left* edge of enemy
		else if (velocity.x < 0) {
					
			//when enemy bumps directly into a solid tile
			if(theWorld.isSolidTileAt(leftEdge) || theWorld.isKillBlockAt(leftEdge)) {
				//negate the velocity
				velocity.x*=-1;
			}
			//when enemy comes to the edge of a platform, turn around
			else if (theWorld.isEmptyTileAt(leftEdge) &&
					theWorld.isEmptyTileAt(leftEdgeBelow)) {
				velocity.x*=-1;
			}						
			
		}	

	}

	public void draw() {
		
		p.pushMatrix();
		p.translate(position.x, position.y); //temporarily translate (0,0) of canvas to middle of enemy
		
		//by default, enemy image is assumed to be facing left
		//if your enemy image is *not* facing left in its default state, 
		//edit the image and re-add it to your project so that it is!
		
		if (velocity.x > 0) { //if enemy is going right, flip image to face right 
			p.scale(-1, 1); // flip horizontally by scaling horizontally by	-100%
		}
		
	
		
		//draw image from upper left corner of enemy
		//because the image function requires an (x,y) referring to the upper left corner
		p.image(enemyImage, -enemyWidth/2, -enemyHeight/2, enemyWidth, enemyHeight);

		p.popMatrix();

	}

}
