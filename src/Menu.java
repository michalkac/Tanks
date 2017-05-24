import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
//Menu State, displays welcome message and control instruction.
public class Menu extends BasicGameState {

	public static boolean newgame;
	
	Image background;
	
	
	public Menu(int xSize, int ySize) {
	
	}

	
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		
		background = new Image("res/background.png");
	}

	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics gr)
			throws SlickException {

		gc.setShowFPS(false);
		background.draw(0,0);
		gr.setColor(Color.white);
		gr.drawString("Welcome in tanks game!",295,30);
		gr.drawString("Press enter to start!",300,50);
		gr.drawString("Controls:",10,130);
		gr.drawString("Moving:A,D",10,150);
		gr.drawString("Barrel Angle:W,S",10,170);
		gr.drawString("Missle Power: L. Shift, L. Ctrl",10,190);
		gr.drawString("Shooting: Enter",10,210);
	
	}

	
	public void update(GameContainer gc, StateBasedGame sbg, int i)
			throws SlickException {

		if(gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
	    	newgame = true;
	    	sbg.enterState(1);
	        }
        
	}

	
	public int getID() {
		return 0;
	}
}