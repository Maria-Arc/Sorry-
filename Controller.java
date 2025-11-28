import java.util.HashMap;
//import java.util.Scanner;

public class Controller {


// Runs the game loop
// Keeps track of whose turn
// Draws card
// Applies card rules
// Validates moves
// Checks win conditions

   
    private HashMap<Integer, Player.Color>slide = new HashMap<>();      //holds index ofslides and color, all slides move
    CardDeck deck;
    Board board;
    Player[] players;
    boolean win = false;

    public Controller(int numPlayers){
        Player.Color[] options = {Player.Color.RED, Player.Color.BLUE, Player.Color.GREEN, Player.Color.YELLOW};
        
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++)
            players[i] = new Player(i, options[i]);
        deck = new CardDeck();
        board = new Board(numPlayers);


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

   //bruh need to handle outercase, inner case kms
   void move(Pawn pawn, CardDeck.Card card){
    int index = pawn.getIndex();
    int nextIndex = index + card.getValue();
    if (canSlide(pawn, nextIndex)){
        //slide
    }
    else{

    }

   }

   //if a 1/2 can select any pawn, else can only select pawns outside or loose turn
   void play(Player player){
    CardDeck.Card drawn = deck.Draw();
    int p = drawn.getValue();
    System.out.println("Move " + p);
    System.out.println("Pawn positions");
        System.out.print("Pawn 0" + (player.getPawn(0)).getIndex()  );
        System.out.print("Pawn 1" + (player.getPawn(1)).getIndex()  );
        System.out.print("Pawn 2" + (player.getPawn(2)).getIndex()  );
        System.out.println("Pawn 3" + (player.getPawn(3)).getIndex()  );
    //Scanner sc = new Scanner(System.in);
    System.out.println("pick a pawn");
   // p = sc.nextInt();
    Pawn pawn = player.getPawn(p);
    
    //need to add like a move
    move(pawn,drawn);

   }

   void runGame(){
       while (!win){

        System.out.print("player one started");





       }

   }

}
