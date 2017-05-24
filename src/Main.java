import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Main extends StateBasedGame{
   
   public static final String gamename = "Tanks!";
   public static final int menu = 0;
   public static final int xSize = 800;
   public static final int ySize = 600;
   
   public Main(String gamename){
      super(gamename);
      this.addState(new Menu(xSize, ySize));
      this.addState(new Game(xSize, ySize));
   }
   
   public void initStatesList(GameContainer gc) throws SlickException{
      this.enterState(menu);
   }
   
   public static void main(String[] args) {
      AppGameContainer appgc;
      try{
         appgc = new AppGameContainer(new Main(gamename));
         appgc.setDisplayMode(xSize, ySize, false);
         appgc.setTargetFrameRate(60);
         appgc.setAlwaysRender(true);
         appgc.start();
      }catch(SlickException e){
         e.printStackTrace();
      }
   }
}