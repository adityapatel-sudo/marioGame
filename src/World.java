import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class World {

	int coinsInStage;

	int TILE_EMPTY = -1;
	int BOXCRATE_DOUBLE = 5;
	int CACTUS = 16;
	int CLOUD1 = 23;
	int FLAGRED = 18;
	int GRASSCENTER = 22;
	int GRASSCLIFFLEFT = 3;
	int GRASSCLIFFRIGHT = 2;
	int GRASSMID = 7;
	int LAVATOP_HIGH = 0;
	int P1_FRONT = 13;
	int P1_HURT = 14;
	int PLANT = 19;
	int SANDCLIFFLEFT = 8;
	int SANDCLIFFRIGHT = 9;
	int SANDMID = 10;
	int SIGNRIGHT = 4;
	int SLIMEWALK1 = 12;
	int SPIKES = 17;
	int STAR = 20;
	int TALLSHROOM_RED = 15;
	int TOCHLIT = 11;
	int GRASS = 6;
	int BOXITEM = 24;
	int BOXITEM_DISABLED=25;
	int CHERRY = 26;

	
	int GRID_UNIT_SIZE = 60; //size of each grid square
	int GRID_UNITS_WIDE; //number of columns
	int GRID_UNITS_TALL;  //number of rows
	
	PImage boxCrate_double;
	PImage cactus;
	PImage cloud1;
	PImage flagred;
	PImage grassCenter;
	PImage grassCliffLeft;
	PImage grassCliffRight;
	PImage grassMid;
	PImage lavaTop_high;
	PImage p1_front;
	PImage p1_hurt;
	PImage plant;
	PImage sandCliffLeft;
	PImage sandCliffRight;
	PImage sandMid;
	PImage signRight;
	PImage slimeWalk1;
	PImage spikes;
	PImage star;
	PImage tallShroom_red;
	PImage tochLit;
	PImage grass;
	PImage boxItem;
	PImage boxItem_disabled;
	PImage cherry;

	//TODO: add PImage objects for every tile image in the world

	


	int[][] worldGrid;
	int[][] start_Grid;
	
	//TODO: create ArrayList of enemies
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	
	PApplet p;
	
	
	public World(PApplet startp, String[] lines) {
		p = startp;
		GRID_UNITS_TALL = 100; //rows
		String[] cc = lines[0].split(",");
	    GRID_UNITS_WIDE = cc.length; //columns
	    
	    start_Grid = new int[GRID_UNITS_TALL][GRID_UNITS_WIDE];
	    worldGrid = new int[GRID_UNITS_TALL][GRID_UNITS_WIDE];
	    
	    for (int i = 0; i < lines.length; i++) {
	        String line = lines[i];
	        String[] chars = line.split(",");
	        for (int j=0; j<chars.length; j++) {
	          start_Grid[i][j] = Integer.parseInt(chars[j].trim());
	        }
	      }
	    

	    //TODO: initialize PImage objects with corresponding image for eveyr tile in the world
		boxCrate_double = p.loadImage("images/boxCrate_double.png");
		cactus = p.loadImage("images/cactus.png");
		cloud1 = p.loadImage("images/cloud1.png");
		flagred = p.loadImage("images/flagred.png");
		grassCenter = p.loadImage("images/grassCenter.png");
		grassCliffLeft = p.loadImage("images/grassCliffLeft.png");
		grassCliffRight = p.loadImage("images/grassCliffRight.png");
		grassMid = p.loadImage("images/grassMid.png");
		lavaTop_high = p.loadImage("images/lavaTop_high.png");
		p1_front = p.loadImage("images/p1_front.png");
		p1_hurt = p.loadImage("images/p1_hurt.png");
		plant = p.loadImage("images/plant.png");
		sandCliffLeft = p.loadImage("images/sandCliffLeft.png");
		sandCliffRight = p.loadImage("images/sandCliffRight.png");
		sandMid = p.loadImage("images/sandMid.png");
		signRight = p.loadImage("images/signRight.png");
		slimeWalk1 = p.loadImage("images/slimeWalk1.png");
		spikes = p.loadImage("images/spikes.png");
		star = p.loadImage("images/star.png");
		tallShroom_red = p.loadImage("images/tallShroom_red.png");
		tochLit = p.loadImage("images/tochLit.png");
		grass = p.loadImage("images/grass.png");
		boxItem = p.loadImage("images/boxItem.png");
		boxItem_disabled = p.loadImage("images/boxItem_disabled.png");
		cherry = p.loadImage("images/cherry.png");




		
		//enemy images are loaded in the Enemy constructor, not here!
	}
	
	  //copying start grid to world grid
	  public void reload() {
		  
		    coinsInStage = 0; // we count them while copying in level data

		    for (int i=0; i<GRID_UNITS_WIDE; i++) {
		      for (int ii=0; ii<GRID_UNITS_TALL; ii++) {
		        if (start_Grid[ii][i] == P1_FRONT) { // player start position
		          worldGrid[ii][i] = TILE_EMPTY; // put an empty tile in that spot
		        } 
		        //TODO: if the tile is an enemy start tile, put empty tile in world grid
		        else if (start_Grid[ii][i] == SLIMEWALK1) { // enemy start position
		        		worldGrid[ii][i] = TILE_EMPTY; // put an empty tile in that spot
		        		}
		        else {
		          if (start_Grid[ii][i]==STAR) {
		            coinsInStage++; //count total coins
		          }
		          worldGrid[ii][i] = start_Grid[ii][i];
		        }
		      }
		    }
		  }
	  
	  
	  
	  public PVector getPlayerStartingPosition() {
		  PVector playerPosition = new PVector();
		    for (int i=0; i<GRID_UNITS_WIDE; i++) {
			      for (int ii=0; ii<GRID_UNITS_TALL; ii++) {
			        if (start_Grid[ii][i] == P1_FRONT) { // player start position
			          playerPosition.x = i*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2);
			          playerPosition.y = ii*GRID_UNIT_SIZE+(GRID_UNIT_SIZE/2);
			          return playerPosition;
			        } 
			      }
			    }
		 
		  return null;
	  }
	  
	  
	  //TODO: write getEnemyStartingPositions() method
	public ArrayList getEnemyStartingPositions() {
		for (int i = 0; i < GRID_UNITS_WIDE; i++) {
			for (int ii = 0; ii < GRID_UNITS_TALL; ii++) {
				PVector enemyPosition = new PVector();
				enemyPosition.x = i * GRID_UNIT_SIZE + (GRID_UNIT_SIZE / 2);
				enemyPosition.y = ii * GRID_UNIT_SIZE + (GRID_UNIT_SIZE / 2);

				if (start_Grid[ii][i] == SLIMEWALK1) { // enemy 1 start position
					Enemy en = new Enemy(p, this, enemyPosition, "images/slimeWalk1.png",                                                                            p.random(0.4f, 0), -35);
					enemyList.add(en);
				}
			}
		}
		return enemyList;
	}

	

	  
	  
	
	//called from draw() in Platformer.java
	//this means (x,y) coordinates have been translated and are based 
	//on the world grid rather than the screen/canvas
	public void drawWorld() {
		
		//draw a white rectangle on the entire world background
		//otherwise translucent corners of tile images will show residues 
		//, 
		//from previous calls to draw
        p.fill(255); // white
        p.rect(0, 0, GRID_UNIT_SIZE*GRID_UNITS_WIDE, GRID_UNIT_SIZE*GRID_UNITS_TALL - 1); 
		
		  for (int i=0; i<GRID_UNITS_WIDE; i++) { // for each column
		      for (int j=0; j<GRID_UNITS_TALL;j++) { // for each tile (or row) in that column
		    	  int value = worldGrid[j][i]; //get tile type
		    	  if (value == BOXCRATE_DOUBLE) {
		    		  p.image(boxCrate_double, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == CACTUS) {
		    		  p.image(cactus, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == CLOUD1) {
		    		  p.image(cloud1, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == FLAGRED) {
		    		  p.image(flagred, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == GRASSCENTER) {
		    		  p.image(grassCenter, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == GRASSCLIFFLEFT) {
		    		  p.image(grassCliffLeft, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == GRASSCLIFFRIGHT) {
		    		  p.image(grassCliffRight, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == GRASSMID) {
		    		  p.image(grassMid, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == LAVATOP_HIGH) {
		    		  p.image(lavaTop_high, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == P1_FRONT) {
		    		  p.image(p1_front, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == P1_HURT) {
		    		  p.image(p1_hurt, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == PLANT) {
		    		  p.image(plant, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SANDCLIFFLEFT) {
		    		  p.image(sandCliffLeft, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SANDCLIFFRIGHT) {
		    		  p.image(sandCliffRight, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SANDMID) {
		    		  p.image(sandMid, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SIGNRIGHT) {
		    		  p.image(signRight, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SLIMEWALK1) {
		    		  p.image(slimeWalk1, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == SPIKES) {
		    		  p.image(spikes, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == STAR) {
		    		  p.image(star, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == TALLSHROOM_RED) {
		    		  p.image(tallShroom_red, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == TOCHLIT) {
		    		  p.image(tochLit, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == GRASS) {
		    		  p.image(grass, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == BOXITEM) {
		    		  p.image(boxItem, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }else if (value == BOXITEM_DISABLED) {
		    		  p.image(boxItem_disabled, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);
		    	  }else if (value == CHERRY) {
		    		  p.image(cherry, i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, GRID_UNIT_SIZE, GRID_UNIT_SIZE);

		    	  }
		    	  
		    	  //TODO: ***ADD EXTRA ELSE IFs HERE*** for each different type of tile
		    	  
		    	  else { //empty tile
		    		  p.stroke(245); // faint light outline. set to 255 (white) to remove entirely.
		              p.fill(255); // white
		              p.rect(i*GRID_UNIT_SIZE, j*GRID_UNIT_SIZE, // x,y of top left corner to draw rectangle
		                GRID_UNIT_SIZE-1, GRID_UNIT_SIZE-1); 
		    	  }
		    	  
		    	  
		    	  //draw coins, if any
		    	  if (value == STAR) {
		    		  p.fill(255,255,255);
		    		  p.stroke(255,223,0);
		    		  p.strokeWeight(10);
		    		  p.ellipse( i * GRID_UNIT_SIZE+GRID_UNIT_SIZE/2, j*GRID_UNIT_SIZE+GRID_UNIT_SIZE/2, GRID_UNIT_SIZE/2, GRID_UNIT_SIZE/2);
		    		  //clue: one call to fill and one call to ellipse
		    		  //then fill in the parameters
		    		  p.stroke(0);
		    		  p.strokeWeight(0);

		    		  
		    	  }
		    	  
		    	  
		    	  
		    	  
		      }
		      
		      
		  }
		
	}
	
	
	
	
	
	public boolean isSolidTileAt(PVector thisPosition) {
		
		int unknownTile = worldSquareAt(thisPosition);

		//TODO: add any other solid tiles in the world to this array
		int[] solidTiles = { BOXITEM_DISABLED, BOXCRATE_DOUBLE, GRASSCENTER,GRASSCLIFFLEFT, GRASSCLIFFRIGHT,GRASSMID, LAVATOP_HIGH,SANDCLIFFLEFT,SANDCLIFFRIGHT,SANDMID,SLIMEWALK1,GRASS, BOXITEM
				};

		for (int tile : solidTiles) {
			if (tile == unknownTile)
				return true;
		}
		
		// unknown tile not found in array
		return false;

	}

	public boolean isKillBlockAt(PVector thisPosition) {
		
		int tile = worldSquareAt(thisPosition);		
		
		//TODO: add to this if statement for each kill block in the world
		if (tile == SPIKES ) {
			return true;
		}else if(tile == LAVATOP_HIGH) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isEmptyTileAt(PVector thisPosition) {
		
		int tile = worldSquareAt(thisPosition);		
		
		//TODO: add to this if statement for each tile that a player can pass over
		//generally this is every item in the game
		if (tile == TILE_EMPTY || tile == STAR || tile == FLAGRED) {
			return true;
		} else {

			return false;
		}
	}

	
	
	//TODO: write an isOnSand() method to identify sand tiles
	
	
	
	
	// returns what type of tile is at a given pixel coordinate
	public int worldSquareAt(PVector thisPosition) {
	    float gridSpotX = thisPosition.x/GRID_UNIT_SIZE;
	    float gridSpotY = thisPosition.y/GRID_UNIT_SIZE;

	    // first a boundary check, to avoid looking outside the grid
	    // if check goes out of bounds, treat it as a solid tile (wall)
	    //grass_mid is arbitrary; it just needs to be a solid block
	    if (gridSpotX<0) {
	      return GRASSCENTER;
	    }
	    if (gridSpotX>=GRID_UNITS_WIDE) {
	      return GRASSCENTER;
	    }
	    if (gridSpotY<0) {
	      return GRASSCENTER ;
	    }
	    if (gridSpotY>=GRID_UNITS_TALL) {
	      return GRASSCENTER;
	    }

	    return worldGrid[(int)gridSpotY][(int)gridSpotX];
	  }

	
	
	// changes the tile at a given pixel coordinate to be a new tile type
	// currently used to replace TILE_COIN tiles with TILE_EMPTY tiles once collected
	public void setSquareAtToThis(PVector thisPosition, int newTile) {
	    int gridSpotX = (int) (thisPosition.x/GRID_UNIT_SIZE);
	    int gridSpotY = (int) (thisPosition.y/GRID_UNIT_SIZE);

	    if (gridSpotX<0 || gridSpotX>=GRID_UNITS_WIDE || 
	      gridSpotY<0 || gridSpotY>=GRID_UNITS_TALL) {
	      return; // can't change grid units outside the grid
	    }

	    worldGrid[gridSpotY][gridSpotX] = newTile;
	}

	
	
	  
	//these helper functions help us correct for the player moving into a world tile
	public float topOfSquare(PVector thisPosition) {
	    int thisY = (int) thisPosition.y;
	    thisY /= GRID_UNIT_SIZE;
	    return (float)thisY*GRID_UNIT_SIZE;
	}
	  
	  
	public float bottomOfSquare(PVector thisPosition) {
		if (thisPosition.y<0) {
		   return 0;
		}
		return topOfSquare(thisPosition)+GRID_UNIT_SIZE;
	}
	  
	  
	public float leftOfSquare(PVector thisPosition) {
		int thisX = (int)thisPosition.x;
		thisX /= GRID_UNIT_SIZE;
		return (float)(thisX*GRID_UNIT_SIZE);
	}
	  
	public float rightOfSquare(PVector thisPosition) {
		if (thisPosition.x<0) {
		    return 0;
		}
		return leftOfSquare(thisPosition)+GRID_UNIT_SIZE;
	}
}
