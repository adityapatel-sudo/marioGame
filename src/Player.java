import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Player {
	// PVector contains two floats, x and y
	// position refers to the (x,y) point at the bottom/feet of player
	PVector position; // current position of player
	PVector velocity; // current velocity of player

	PImage player;
	boolean showPlayerImage;

	boolean isOnGround; // used to keep track of whether the player is on the ground

	boolean jumpedFromSand; // a player jumping from sand can't jump as high
							// as a player jumping from the grass tile

	boolean facingRight; // used to keep track of which direction the player
							// last moved in. used to flip player image.

	int coinsCollected; // a counter to keep a tally on how many coins the
						// player has collected
	int lives = STARTING_LIVES;
	int score = 0; // based on coins, and other things collected

	boolean lifeJustLost; // for player blinking animation
	int initialFrameCount; // keep track of when player blinking animation starts

	static final int STARTING_LIVES = 3;

	// how hard the player jolts upward on jump - old: 11.0
	static final float JUMP_POWER = (float) 13.0;

	// force of player movement on ground, in pixels/cycle
	static final float RUN_SPEED = (float) 5.0;

	// like run speed, but used for control while in the air
	static final float AIR_RUN_SPEED = (float) 2.0;

	// horizontal movement in the air after the player jumped from sand
	static final float SAND_AIR_SPEED = (float) 1.0;

	// friction from the ground. multiplied by the x speed each frame.
	// note: a higher value here means *lower* friction
	// because it is multiplied by the speed
	static final float SLOWDOWN_PERC = (float) 0.6;

	// resistance in the air, otherwise air control
	// enables crazy speeds
	static final float AIR_SLOWDOWN_PERC = (float) 0.85;

	// sand friction - higher than grass
	static final float SAND_SLOWDOWN = (float) 0.3; // sand friction

	// if under this speed, the player is drawn as standing still
	static final float TRIVIAL_SPEED = (float) 1.0;

	// which keys is the player holding down right now
	boolean holdingUp, holdingRight, holdingLeft;

	World theWorld;

	PVector startingPosition; // player position at start of game

	PApplet p;
	float gravity;

	int playerHeight;
	int playerWidth;

	// constructor gets called automatically when the Player instance is created
	public Player(PApplet startp, World startWorld, float startGravity, PVector startPosition) {

		p = startp;
		theWorld = startWorld;
		gravity = startGravity;
		startingPosition = startPosition;

		player = p.loadImage("images/p1_front.png");

		isOnGround = false;
		facingRight = true;

		position = new PVector();
		velocity = new PVector();

		playerHeight = 80;
		// scale proportionally
		double playerWidthDouble = playerHeight * player.width / (double) player.height;
		playerWidth = (int) Math.round(playerWidthDouble);

		reset();
	}

	// reset is called when whole game is reset
	// initially this is whenever the player hits a kill block
	// eventually this will be when lives = 0
	public void reset() {
		// TODO: complete reset of game when lives = 0
		coinsCollected = 0;
		score = 0;
		velocity.x = 0;
		velocity.y = 0;
		position.x = startingPosition.x;
		position.y = startingPosition.y;
		lives = STARTING_LIVES;
		// TODO: reset player position and velocity

	}

	// calculates velocity based on which keys are pressed
	// as well as whether the player is in the air or on the ground
	public void inputCheck() {

		// these lines use a 'ternary operator' - basically a one line if statement
		float speedHere = (isOnGround ? RUN_SPEED : AIR_RUN_SPEED);
		float frictionHere = (isOnGround ? SLOWDOWN_PERC : AIR_SLOWDOWN_PERC);

		// TODO: change friction, speed, and jump status if player is on sand instead of
		// ground

		// keyboard flags are set by keyPressed/keyReleased
		if (holdingLeft) {
			velocity.x -= speedHere;
		} else if (holdingRight) {
			velocity.x += speedHere;
		}
		velocity.x *= frictionHere; // causes player to constantly lose speed

		if (isOnGround) { // player can only jump if currently on the ground
			if (holdingUp) { // up arrow causes the player to jump
								//
				// TODO: if player jumped from sand, don't jump as high

				velocity.y = -JUMP_POWER; // adjust vertical speed

				isOnGround = false; // mark that the player has left the ground,
									// i.e. cannot jump again for now
			}
		}
	}

	// method returns the *state* of the player after the checks
	// 2 = player overlapped kill block
	// 1 = player overlapped goal block, game is won
	// 0 = else. hit or didn't hit wall (doesn't matter)
	public int checkForWallBumping() {

		int wallProbeDistance = (int) (playerWidth * 0.5);
		int ceilingProbeDistance = (int) (playerHeight * 0.95);

		/*
		 * Because of how we draw the player, "position" is the center of the
		 * feet/bottom. To detect and handle wall/ceiling collisions, we create 5
		 * additional positions: leftSideHigh - left of center, at shoulder/head level
		 * leftSideLow - left of center, at shin level rightSideHigh - right of center,
		 * at shoulder/head level rightSideLow - right of center, at shin level topSide
		 * - horizontal center, at tip of head These 6 points - 5 plus the original at
		 * the bottom/center - are all that we need to check in order to make sure the
		 * player can't move through blocks in the world. This works because the block
		 * sizes (World.GRID_UNIT_SIZE) aren't small enough to fit between the cracks of
		 * those collision points checked.
		 */

		// used as probes to detect running into walls, ceiling
		PVector leftSideHigh, rightSideHigh, leftSideLow, rightSideLow, topSide;
		leftSideHigh = new PVector();
		rightSideHigh = new PVector();
		leftSideLow = new PVector();
		rightSideLow = new PVector();
		topSide = new PVector();

		// update wall probes

		// left edge of player
		leftSideHigh.x = leftSideLow.x = position.x - wallProbeDistance;

		// right edge of player
		rightSideHigh.x = rightSideLow.x = position.x + wallProbeDistance;

		// shin high
		leftSideLow.y = rightSideLow.y = (float) (position.y - 0.2 * playerHeight);

		// shoulder high
		leftSideHigh.y = rightSideHigh.y = (float) (position.y - 0.8 * playerHeight);

		topSide.x = position.x; // center of player
		topSide.y = position.y - ceilingProbeDistance; // top of player

		// if any edge of the player overlaps a kill block, reset the round
		if (theWorld.isKillBlockAt(topSide) || theWorld.isKillBlockAt(leftSideHigh)
				|| theWorld.isKillBlockAt(leftSideLow) || theWorld.isKillBlockAt(rightSideHigh)
				|| theWorld.isKillBlockAt(rightSideLow) || theWorld.isKillBlockAt(position)) {
	
			if (lifeJustLost == false) {
				lives--;
				return 2;
			}

			
		}
		// TODO: uncomment to reset player when it hits the kill block
		// 2 to reset game and/or player
		// any other possible collisions would be irrelevant, exit function now



		PVector centerOfPlayer = new PVector(position.x, position.y - playerHeight / 2);

		// TODO: check for player reaching goal
		// only check center of player because player should
		// more visually overlap goal block before game win
		
		if (theWorld.worldSquareAt(centerOfPlayer) == theWorld.FLAGRED) {
			return 1; //1 to indicate win
			}

		// the following conditionals just check for collisions with each bump
		// probe
		// depending upon which probe has collided with a block, we push the
		// player back the opposite direction

		if (theWorld.isSolidTileAt(topSide)) {
               
			// TODO: if player's head bumped a question block specifically
			// place a power-up above the question mark block
			// then turn question mark block into a regular solid block


			if (theWorld.worldSquareAt(topSide) == theWorld.BOXITEM) {
				// turn question block into another solid block
				theWorld.setSquareAtToThis(topSide, theWorld.BOXITEM_DISABLED);

				// place gem above question block
				PVector blockAboveTopSide = new PVector(topSide.x, topSide.y - theWorld.GRID_UNIT_SIZE);
				// if there is nothing above the question block, place a gem power-up there
				// this means that maps should be designed so that there's nothing on top
				// of a question mark!!!!!
				if (theWorld.worldSquareAt(blockAboveTopSide) == theWorld.TILE_EMPTY)
					theWorld.setSquareAtToThis(blockAboveTopSide, theWorld.CHERRY);
			}
			if (theWorld.isSolidTileAt(position)) {
				position.sub(velocity);
				velocity.x = (float) 0.0;
				velocity.y = (float) 0.0;

			} else {
				position.y = theWorld.bottomOfSquare(topSide) + ceilingProbeDistance;
				if (velocity.y < 0) {
					velocity.y = (float) 0.0;
				}
			}

		}

		if (theWorld.isSolidTileAt(leftSideLow) || theWorld.isSolidTileAt(leftSideHigh)) {

			if (theWorld.isSolidTileAt(leftSideLow)) {
				position.x = theWorld.rightOfSquare(leftSideLow) + wallProbeDistance;
				if (velocity.x < 0) {
					velocity.x = 0;
				}
			}

			if (theWorld.isSolidTileAt(leftSideHigh)) {
				position.x = theWorld.rightOfSquare(leftSideHigh) + wallProbeDistance;
				if (velocity.x < 0) {
					velocity.x = 0;
				}
			}
		}

		if (theWorld.isSolidTileAt(rightSideLow)) {

			position.x = theWorld.leftOfSquare(rightSideLow) - wallProbeDistance;
			if (velocity.x > 0) {
				velocity.x = 0;
			}
		}

		if (theWorld.isSolidTileAt(rightSideHigh)) {
			position.x = theWorld.leftOfSquare(rightSideHigh) - wallProbeDistance;
			if (velocity.x > 0) {
				velocity.x = 0;
			}
		}


		// return 0 for normal bumping or movement (no kill block and no goal block
		// overlap)
		return 0;
	}

	public void checkForItemGetting() {
		PVector centerOfPlayer;
		// we use this to check for coin overlap in center of player
		// (remember that "position" is keeping track of bottom center of feet)

		centerOfPlayer = new PVector(position.x, position.y - playerHeight / 2);

		if (theWorld.worldSquareAt(centerOfPlayer) == theWorld.STAR) {
			theWorld.setSquareAtToThis(centerOfPlayer, theWorld.TILE_EMPTY);
			// TODO: increment coinsCollected and score
			coinsCollected++;
			score = score + 50;
		}
		if (theWorld.worldSquareAt(centerOfPlayer) == theWorld.CHERRY) {
			theWorld.setSquareAtToThis(centerOfPlayer, theWorld.TILE_EMPTY);

			lives++;

			}

		// TODO: check for gem/power-up getting

	}

	public void checkForFalling() {

		// If we're standing on an empty, coin, or other item tile, we're not standing
		// on
		// anything.
		// Fall!
		if (theWorld.isEmptyTileAt(position)) {
			isOnGround = false;
		}

		if (isOnGround == false) { // not on ground
			if (theWorld.isSolidTileAt(position)) { // landed on solid square
				isOnGround = true;
				position.y = theWorld.topOfSquare(position);
				velocity.y = 0;
			} else { // fall
				velocity.y += gravity;
			}
		}
	}

	// TODO: add method that checks when player collides with enemy
	// called checkForEnemyCollision()
	public boolean checkForEnemyCollision() {

		float playerLeftEdge = position.x - playerWidth / 2;
		float playerRightEdge = position.x + playerWidth / 2;
		float playerTopEdge = position.y - playerHeight;
		float playerBottomEdge = position.y;

		// test if any part of the enemy overlaps any part of the player
		// enemy position is defined as the center of the enemy
		for (Enemy e : theWorld.enemyList) {
			float enemyLeftEdge = e.position.x - e.enemyWidth / 2;
			float enemyRightEdge = e.position.x + e.enemyWidth / 2;
			float enemyTopEdge = e.position.y - e.enemyHeight / 2;
			float enemyBottomEdge = e.position.y + e.enemyHeight / 2;

			if (enemyRightEdge > playerLeftEdge && enemyLeftEdge < playerRightEdge && // overlap on x axis
					enemyBottomEdge > playerTopEdge && enemyTopEdge < playerBottomEdge // overlap on y axis
			) {
				if (!lifeJustLost) { // temporary immunity if life just lost
					lives--;
					score = score - 10; // change me!
					return true;
				}
			}
		}

		return false;
	}

	// move() returns the *state* of the player after the checks
	// 2 = reset the whole game
	// right now that's because the player hit a kill block
	// later we will only return 2 if lives = 0 and NOT if lives > 0
	// (note that '2' means something *different* for move() and
	// checkForWallBumping() )
	// 1 = player overlapped goal block
	// 0 = else. hit or didn't hit wall (doesn't matter)
	public int move() {
		position.add(velocity);

		// this method fixes y position if player goes below a solid tile
		// this method must be called before checkForWallBumping() which assumes that
		// a player's feet is not overlapping a solid tile
		checkForFalling();

		// checkForWallBumping returns the *state* of the player after the checks
		// 2 = player overlapped kill block
		// 1 = player overlapped goal block
		// 0 = else. hit or didn't hit wall (doesn't matter)
		int gameResetState = checkForWallBumping();

		checkForItemGetting();

		// TODO: check for enemy collision

		boolean enemyCollision = checkForEnemyCollision();

		if (enemyCollision) // if player hit an enemy
			gameResetState = 2; // game should act like player hit a kill block
		// TODO: when gameResetState = 2, that means player just hit a kill block
		// that means we need to start the 'life just lost' animation

		


		if (lifeJustLost == false && lives > 0 && gameResetState == 2) {
			lifeJustLost = true;
			initialFrameCount = p.frameCount; // save time animation started

			velocity.y = -JUMP_POWER;
			isOnGround = false;

			return 0;
		} else
			return gameResetState;
		
	

	}

	public void draw() {

		if (velocity.x < -TRIVIAL_SPEED) {
			facingRight = false;
		} else if (velocity.x > TRIVIAL_SPEED) {
			facingRight = true;
		}

		p.pushMatrix(); // lets us compound/accumulate translate/scale/rotate
						// calls, then undo them all at once

		p.translate(position.x, position.y);
		if (facingRight == false) {
			p.scale(-1, 1); // flip horizontally by scaling horizontally by
							// -100%
		}
		// translate (0,0) of canvas to upper left corner of where player will be drawn
		p.translate(-playerWidth / 2, -playerHeight);

		// TODO: after a life is lost, player image is only shown every other call to
		// draw(). This creates a blinking animation.

		if (lifeJustLost && showPlayerImage) {
			p.image(player, 0, 0, playerWidth, playerHeight);
			showPlayerImage = false;
			if (p.frameCount - initialFrameCount > 40) {
				lifeJustLost = false;
			}
		} else if (lifeJustLost && showPlayerImage == false) {
			showPlayerImage = true;
		} else { // normal state
			p.image(player, 0, 0, playerWidth, playerHeight);
		}
		
		p.popMatrix(); // undoes all translate/scale/rotate calls since the
						// pushMatrix earlier in this function

	}
}
