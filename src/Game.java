import org.newdawn.slick.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends BasicGameState {
	//Arrays that contains terrain level and inclination data.
	int[] terrainArray = new int[800];
	double[] inclinationArray = new double[800];
	
	protected float x, y;//Missile position for drawing and damage check.
	
	
	boolean firing;//Variable that blocks keyboard input and some display elements when shooting.
	
	String winner;
	
    List<Float> x1;
    List<Float> y1;
    
	float redLife;
	float blueLife;
    
	
	float missileAngle;
	float missilePower;
	float missileMultipler = 0.125f;
	float gravityConstant = 9.81f;
	
	int movingLimit;//Limited player movement per round.
	
	int playerRedXPosition;
	float redBarrelRotation;
	float redMissilePower;
	
	int playerBlueXPosition;
	float blueBarrelRotation;
	float blueMissilePower;
	
	int player;//Defines round, 1 for red player round, 2 for blue player round, enters 3 if there is a winner.
	
	
	//Variables used in terrain level array fill.
	double a1;
	double b1;
	double c1;
	double d1;
	
	double a2;
	double b2;
	double c2;
	double d2;
	
	double a3;
	double b3;
	double c3;	
	double d3;
	
	int terrainColor;//Terrain is randomly colored, there are 3 variants: yellow (sand), green(grass), and grey (rock).

		
	Image background;
	
	Image redtank;
	Image redbarrel;
	
	Image bluetank;
	Image bluebarrel;
	
	
	Image missle;
	
	
	public Game(int xSize, int ySize) {
	}
	
	public void init(GameContainer gc, StateBasedGame sbg)
		throws SlickException {
		
			background = new Image("res/background.png");
		
			redtank = new Image("res/RedTank.png");
			redbarrel = new Image("res/RedBarrel.png");
		
			bluetank = new Image("res/BlueTank.png");
			bluebarrel = new Image("res/BlueBarrel.png");
		
			missle =new Image("res/Missle.png");
		}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		if (Menu.newgame == true){
			newgame();
			Menu.newgame = false;
		}
		gc.setShowFPS(false);
		background.draw(0,0);
		
		if(firing==false){//those parameters are hidden if firing missile.
			
			g.setColor(Color.red);
			g.drawString("RED PLAYER",10,10);
			g.setColor(Color.white);
			g.drawString("LIFE:" + redLife,10,30);
			g.drawString("ANGLE:" + (Math.round((redBarrelRotation*10.0))/10.0),10,50);
			g.drawString("POWER:" + ((Math.round(redMissilePower*10.0))/10.0),10,70);
		
			g.setColor(Color.blue);
			g.drawString("BLUE PLAYER",690,10);
			g.setColor(Color.white);
			g.drawString("LIFE:" + blueLife,690,30);
			g.drawString("ANGLE:" + (Math.round((blueBarrelRotation*10.0))/10.0),690,50);
			g.drawString("POWER:" + (Math.round((blueMissilePower*10.0))/10.0),690,70);
		
		
			
		
			if (player == 1 && winner == null){
				g.drawString("MOVE LEFT:" + movingLimit, 340, 30);
				g.setColor(Color.red);
				g.drawString("RED ROUND", 355, 10);
			}else if(player == 2 && winner == null){
				g.drawString("MOVE LEFT:" + movingLimit, 340, 30);
				g.setColor(Color.blue);
				g.drawString("BLUE ROUND", 355, 10);
			}else{
				player = 3;
				if (winner == "RED"){
					g.setColor(Color.red);
					g.drawString("WINNER:RED", 360, 10);
				}else if (winner == "BLUE"){
					g.setColor(Color.blue);
					g.drawString("WINNER:BLUE", 360, 10);
				}
				g.setColor(Color.white);
				g.drawString("Press Enter or Escape to return.", 270, 30);
				
			}
		}
		
		if (terrainColor == 1){//Terrain color set.
			g.setColor(Color.green);
		}else if(terrainColor == 2){
			g.setColor(Color.white);
		}else if(terrainColor == 3){
			g.setColor(Color.gray);
		}
		
		
		
		for (int w = 0; w<800; w++){//Terrain drawing (using vertical lines).
			g.drawLine(w, terrainArray[w], w, 600);
		}
		
		//Setting centers of rotation for tanks and barrels.
		redtank.setCenterOfRotation(redtank.getWidth() / 2, redtank.getHeight() / 2);
		redbarrel.setCenterOfRotation(redbarrel.getWidth() / 2, (redbarrel.getHeight() / 2)+5);
		redtank.setRotation((float) inclinationArray[playerRedXPosition+15]-90);
		redbarrel.setRotation(redBarrelRotation);
			
		bluetank.setCenterOfRotation(bluetank.getWidth() / 2, bluetank.getHeight() / 2);
		bluebarrel.setCenterOfRotation(bluebarrel.getWidth() / 2, (bluebarrel.getHeight() / 2)+5);
		bluetank.setRotation((float) inclinationArray[playerBlueXPosition+15]-90);
		bluebarrel.setRotation(blueBarrelRotation);
		
		//Drawing tanks and barrels.
		redtank.draw(playerRedXPosition, terrainArray[playerRedXPosition+13]-5 );
		redbarrel.draw (playerRedXPosition+9, terrainArray[playerRedXPosition+13]-16);
		
		bluetank.draw(playerBlueXPosition, terrainArray[playerBlueXPosition+13]-5 );
		bluebarrel.draw (playerBlueXPosition+9, terrainArray[playerBlueXPosition+13]-16);
		
		
        try{//drawing missile trajectory.
            if( x1.isEmpty() == false ) drawshoot();//drawing missile trajectory.             
            else firing = false;
                       
        }catch(NullPointerException ex){ 
        }
						
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		
		if( gc.getInput().isKeyPressed(Input.KEY_ENTER)){//Shooting, player Life updating, tour switching, win checking.
			if (player == 1 && firing == false){
				shoot(redMissilePower,(redBarrelRotation*-1)+90);
				movingLimit = 100;
				player = 2;
				missleDamageAndWinCheck();
			}else if (player == 2  && firing == false){
				shoot(blueMissilePower,(blueBarrelRotation*-1)+90);
				movingLimit = 100;
				player = 1;	
				missleDamageAndWinCheck();
			}else if (player == 3) sbg.enterState(0);
			
			
		}
		
		if (gc.getInput().isKeyDown(Input.KEY_ESCAPE)) {//Abort current game.
			sbg.enterState(0);
			
		}
		
		if (gc.getInput().isKeyDown(Input.KEY_D)) {//Player moving.
			if (player == 1  && firing == false && playerRedXPosition < 764 && movingLimit > 0){
				playerRedXPosition++;
				movingLimit--;
				barrelAngleLimitRed();
			}else if (player == 2 && firing == false && playerBlueXPosition < 764 && movingLimit>0){
				playerBlueXPosition++;
				movingLimit--;
				barrelAngleLimitBlue();
			}
		}
		if (gc.getInput().isKeyDown(Input.KEY_A)) {
			
			if (player == 1 && firing == false && playerRedXPosition > 10 && movingLimit > 0){
				playerRedXPosition--;
				movingLimit--;
				barrelAngleLimitRed();
				
			}else if (player == 2 && firing == false && playerBlueXPosition > 10 && movingLimit > 0){
				playerBlueXPosition--;
				movingLimit--;
				barrelAngleLimitBlue();
			}	
					
		}
		
		
		if (gc.getInput().isKeyDown(Input.KEY_W)) {//Barrel angle changing.
			if (player == 1 && firing == false && redBarrelRotation < (inclinationArray[playerRedXPosition+15]-1))
				redBarrelRotation += 0.2f;
			else if (player == 2 && firing == false && blueBarrelRotation < (inclinationArray[playerBlueXPosition+15]-1))
				blueBarrelRotation += 0.2f;
			
		}
		if (gc.getInput().isKeyDown(Input.KEY_S)) {
			if (player == 1 && firing == false && redBarrelRotation > (inclinationArray[playerRedXPosition+15]-179))
				redBarrelRotation -= 0.2f;
			else if (player == 2 && firing == false && blueBarrelRotation > (inclinationArray[playerBlueXPosition+15]-179))
				blueBarrelRotation -= 0.2f;
		}
		
		
		if (gc.getInput().isKeyDown(Input.KEY_LSHIFT)) {//Missile power change.
			if (player == 1 && firing == false && redMissilePower < 99.9){
				redMissilePower += 0.1;
			}else if (player == 2 && firing == false && blueMissilePower < 99.9){
				blueMissilePower += 0.1;
			}
			
		}
		
		if (gc.getInput().isKeyDown(Input.KEY_LCONTROL)) {
			if (player == 1 && firing == false && redMissilePower > 0.1){
				redMissilePower -= 0.1;
			}else if (player == 2 && firing == false && blueMissilePower > 0.1){
				blueMissilePower -= 0.1;
			}
			
		}
		

	}
    
	public void newgame(){//Setting initial values for variables and generating new terrain for new game.
		firing = false;
		
		winner = null;
		
		redLife = 100;
		blueLife = 100;
		
		redBarrelRotation = 0;
		blueBarrelRotation = 0;
		
		redMissilePower = 50;
		blueMissilePower = 50;
		
		movingLimit = 100;
		
		//Random start positions with minimum distance of 100 pixels between players and 25 pixels from game board (player X position is specified by left side of tank, tank is 26 pixels wide) .
		playerRedXPosition = (int )(Math.random() * 305 + 25);
		playerBlueXPosition = (int )(Math.random() * 305 + 450);
		
		
		
		//Random first player turn.
		player = (int) Math.ceil( Math.random()*2 ); 
		
		//Random terrain generation.
		d1 = (Math.random() * 50 + 30);
		c1 = (Math.random() * 45 + 20);
		b1 = Math.random() * 6.28;
		d2 = (Math.random() * 50 + 30);
		c2 = (Math.random() * 25 + 20);
		b2 = Math.random() * 6.28;
		d3 = (Math.random() * 50 + 40);
		c3 = (Math.random() * 25 + 20);
		b3 = Math.random() * 6.28;
		
		terrainColor = (int) Math.ceil(Math.random()*3);
			
		//Filling terrain level array.
		for (int n=0; n<800; n++ ){
			a1 = n/d1;
			a2 = n/d2;
			a3 = n/d3;
			double e = (c1*(Math.sin(a1+b1)))+ (c2*(Math.sin(a2+b2))) + (c3*(Math.sin(a3+b3)));
			terrainArray[n] = 450 + (int) e;
		}
		
		//Filling terrain inclination array, measures angle between two points of terrain. 
		for (int t=5; t<800; t++){
			inclinationArray[t]= Math.toDegrees(Math.atan2(5, terrainArray[t-5] - terrainArray[t]));
		}
	}
	
	//These two methods keeps tanks barrel angles when tanks moving between terrain inclination and terrain inclination -180*. 
	public void barrelAngleLimitRed(){
		if(redBarrelRotation >inclinationArray[playerRedXPosition+15]){
			redBarrelRotation=(float) (inclinationArray[playerRedXPosition+15]);
		}else if(redBarrelRotation<inclinationArray[playerRedXPosition+15]-179){
			redBarrelRotation=(float) (inclinationArray[playerRedXPosition+15]-179);
		}
	}
	
	public void barrelAngleLimitBlue(){
		if(blueBarrelRotation >inclinationArray[playerBlueXPosition+15]){
			blueBarrelRotation=(float) (inclinationArray[playerBlueXPosition+15]);
		}else if(blueBarrelRotation<inclinationArray[playerBlueXPosition+15]-179){
			blueBarrelRotation=(float) (inclinationArray[playerBlueXPosition+15]-179);
		}
	}
	
	//This method fills shoot trajectory.
	public void shoot( float v, float theta) {
	    firing = true;
	    x1 = new ArrayList<Float>();
	    y1 = new ArrayList<Float>(); 
	    
	    float t = (float) 0.125;
	    final float g = (float) 9.81;     // Refresh rate and gravity constant
	    int i = 0;
	    
	    while(true){
	        
	        float tempX = (float) (v*Math.cos(Math.toRadians(theta))*t);
	        float tempY = (float) (v*Math.sin(Math.toRadians(theta))*t - (.5*g*(t*t)));
	        x1.add(tempX);
	        y1.add(tempY);
	
	       //Stop calculating missile trajectory if it hits ground or is going beyond the game area.         
	        if(player == 1 && (
	        		(((terrainArray[playerRedXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))))) < 10) || //Missile crosses upper game boarder.
	        		((Float.valueOf(String.valueOf(x1.get(i)))+playerRedXPosition+12) < 10) || //Missile crosses left game boarder
	        		((Float.valueOf(String.valueOf(x1.get(i)))+playerRedXPosition+12) > 790) || //Missile crosses right game boarder
	        		((terrainArray[playerRedXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))))) > (terrainArray[ (int) (Float.valueOf(String.valueOf(x1.get(i)))+playerRedXPosition+12)]+6) //Missile hits terrain.
	        		)){
	        	//Filling missile last coordinates for damage check, they will be overwrite before missile trajectory drawing. 
	            x = (Float.valueOf(String.valueOf(x1.get(i)))+playerRedXPosition+12);
	            y = (terrainArray[playerRedXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))));
	            break;
	        }else if(player == 2 && (
	        		(((terrainArray[playerBlueXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))))) < 10) ||
	        		((Float.valueOf(String.valueOf(x1.get(i)))+playerBlueXPosition+12) < 10) ||
	        		((Float.valueOf(String.valueOf(x1.get(i)))+playerBlueXPosition+12) > 790) ||
	        		((terrainArray[playerBlueXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))))) > (terrainArray[ (int) (Float.valueOf(String.valueOf(x1.get(i)))+playerBlueXPosition+12)]+6)
	        		)){
	        	
	            x = (Float.valueOf(String.valueOf(x1.get(i)))+playerBlueXPosition+12);
	        	y = (terrainArray[playerBlueXPosition+12]+5) - (Float.valueOf(String.valueOf(y1.get(i))));
	            break;
	        }
	        i++;
	        t += (float) .125;
	        
	    }
	}
	
	//Missile trajectory drawing method.		
	public void drawshoot(){
	    if (player == 2){
	    	x = x1.get(0)+playerRedXPosition+12;
	    	y = 600 - y1.get(0) -( 600 - terrainArray[playerRedXPosition+12]+5);
	    	missle.draw(x, y, .4f);
	    	x1.remove(0);
	    	y1.remove(0);
	    }
	    else if(player == 1){
	    	x = x1.get(0)+playerBlueXPosition+12;
	    	y = 600 - y1.get(0) -( 600 - terrainArray[playerBlueXPosition+12]+5);
	    	missle.draw(x, y, .4f);
	    	x1.remove(0);
	    	y1.remove(0);
	    }
	}
	
	//Quite primitive damage calculation based on distance between last missile position and center of tank position, tank is transparent for missile.
	public void missleDamageAndWinCheck()
	{
		if ((Math.abs(playerRedXPosition+12-x)<12 && (Math.abs(terrainArray[playerRedXPosition+12]+5-y))<10)){
			redLife -= (int) (12-(Math.abs(playerRedXPosition+12-x))+(10-Math.abs(terrainArray[playerRedXPosition+12]+5-y)));
		}else if ((Math.abs(playerBlueXPosition+12-x)<12 && (Math.abs(terrainArray[playerBlueXPosition+12]+5-y))<10)){
			blueLife -= (int) (12-(Math.abs(playerBlueXPosition+12-x))+(10-Math.abs(terrainArray[playerBlueXPosition+12]+5-y)));
		}
		
		if(blueLife<=0){//Simple win condition.
			winner = "RED";
		}else if(redLife<=0){
			winner = "BLUE";
		}
	}
	


	public int getID() {
		return 1;
	}
}