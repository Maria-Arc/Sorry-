import java.util.HashMap;

public class Controller {
   
    private HashMap<Integer, Player.Color>slide = new HashMap<>();      //holds index ofslides and color, all slides move
    

    public Controller(){







        //initialize slide combinations
        slide.put(1, Player.Color.RED);
        slide.put(9, Player.Color.RED);
        slide.put(16, Player.Color.BLUE);
        slide.put(24, Player.Color.BLUE);
        slide.put(31, Player.Color.YELLOW);
        slide.put(39, Player.Color.YELLOW);
        slide.put(46, Player.Color.GREEN);
        slide.put(54, Player.Color.GREEN);
    }

       public boolean canSlide( Pawn p, int index){
    //YOU CANNOT SLIDE ON UR OWN COLOR
    if (slide.containsKey(index) && slide.get(index) != (p.getOwner()).getColor() ){    //if ur on a slide index and the slide isnt ur color
        return true;
    }
    return false;
   }

   //lowkey hardcodedslide length
   public int slideLength(int index){
    switch(index){
        case 1: case 14: case 31: case 46:
            return 4;
        case 9: case 24: case 39: case 54:
            return 5;
        default:
            return -1;


    }
   }


}
