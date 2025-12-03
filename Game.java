import java.util.HashMap;

public class Game {

    private Player[] players;
    private Board board;
    private CardDeck deck;
    private int currentPlayerIndex = 0;
    private int currentCardValue = -1;
    private Pawn selectedPawn = null;
    private int numberPlayers;
    private HashMap<Integer, Player.Color>slide = new HashMap<>();   
    private int needsChoice = -1;
        //-1 for none, 0 for sorry(swap), 1 for 1(move out of start or not),
        //  2 for 2 (move out of start or not), 3 for 7 (move 2 pawns) 4 for ten (forw/back), 5 for 11 (for/swap), 


    public Game(int numPlayers, int numPawns) { 
        numberPlayers = numPlayers;
        players = new Player[numPlayers]; 
        
        
        Player.Color[] colors = {Player.Color.RED, Player.Color.BLUE, 
                                  Player.Color.YELLOW, Player.Color.GREEN};
        
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(i, colors[i], numPawns);
        }
        
        board = new Board(numPlayers); // Board always has 4 home paths
        deck = new CardDeck();


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
    
    public Player[] getPlayers() {
        return players;
    }
    
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }
    
    public Board getBoard() {
        return board;
    }

    public boolean hasPendingChoice(){
       return needsChoice != -1;
    }
    
    public CardDeck.Card drawCard() {
        CardDeck.Card card = deck.Draw();
        currentCardValue = card.getValue();
        switch(currentCardValue){
            case 0:
                needsChoice = 0;
                break;

            case 7:
                needsChoice = 3;
                break;
            case 10:
                needsChoice = 4;
                break;
            case 11:
                needsChoice = 5;
                break;
            default:
                needsChoice = -1;
        }
        return card;
    }
    
    public void setSelectedPawn(Pawn p) {
        if (p.getOwner() == getCurrentPlayer()) {
            selectedPawn = p;
        }
    }
    
    public Pawn getSelectedPawn() {
        return selectedPawn;
    }
    
    public int getPendingChoice() {
        return needsChoice;
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
        case 1: case 16: case 31: case 46:
            return 3;
        case 9: case 24: case 39: case 54:
            return 4;
        default:
            return -1;
    }
   }
    
    
    public boolean movePawn(Pawn pawn) {
        if (pawn == null || currentCardValue == -1 || pawn.getOwner() != getCurrentPlayer()) {
            return false;
        }
        
        switch (currentCardValue) {
            case 1:
                return card1(pawn);
            case 2:
                return card2(pawn);
            case 3:
                return MoveForward(pawn, 3);
            case 4:
                return MoveBackward(pawn, 4);
            case 5:
                return MoveForward(pawn, 5);
            case 8:
                return MoveForward(pawn, 8);
            case 12:
                return MoveForward(pawn, 12);
            default:
                return false;
        }
    }
        
        private boolean card1(Pawn pawn){
            if (pawn.getState() == Pawn.State.START) {
                int startPos = getPlayerStartPosition(pawn.getOwner());
                board.movePawnOuter(pawn, startPos);
                return true;
            } else  {
                return MoveForward(pawn, 1);
            }
        }

        private boolean card2(Pawn pawn){
            if (pawn.getState() == Pawn.State.START) {
                int startPos = getPlayerStartPosition(pawn.getOwner());
                board.movePawnOuter(pawn, startPos);
                return true;
            } else  {
                return MoveForward(pawn, 2);
            }
        }

        public boolean card7(Pawn p, Pawn p2, int i){
           if ( MoveForward(p, i)){
                if (MoveForward(p2, 7-i)) return true;
                else  MoveBackward(p, i);
           }
            return false;
        }

        public boolean card10(Pawn p, boolean forward){
            if (forward){
                return MoveForward(p, 10);
            }
            else
                return MoveBackward(p, 1);
           
        }

        public boolean card11(Pawn p, boolean forward, Pawn p2){
            if (forward)
                return MoveForward(p,11);
            else
                if (p.getState() != Pawn.State.START) {
                    return false;
                }
                return swap(p, p2);
        }

        private boolean MoveForward(Pawn pawn, int num){
            int index = pawn.getIndex();

            if (pawn.getState() == Pawn.State.HOME){
                if (index + num < 6){
                    board.movePawnInner(pawn, index + num);
                    if (index + num == 5)
                        pawn.setState(Pawn.State.FINISHED);
                }
                else return false;
                return true;
            }

            else if (pawn.getState() == Pawn.State.MAIN){
                int targetIndex = (index + num) % 60; //to wrap around
                int homeEntrance = getPlayerHomeEntrance(pawn.getOwner());

                int spacesToHome = (homeEntrance-index+60) %60;
          
                if (num > spacesToHome + 5) 
                    return false;
                else if (spacesToHome == num)
                {
                     board.movePawnOuter(pawn, targetIndex);
                    return true;
                }
                else if (spacesToHome < num){
                    int homeInd = num - spacesToHome -1;
                    if (homeInd > 5) return false;
                    board.clearOuterPath(pawn);
                    board.movePawnInner(pawn, homeInd);
                    if (spacesToHome == 5)
                        pawn.setState(Pawn.State.FINISHED);
                    return true;
                } 
                else {
                    // Regular move on outer path
                    board.movePawnOuter(pawn, targetIndex);
                    
                    // Check for slide
                    if (canSlide(pawn, targetIndex)) {
                        int slideLen = slideLength(targetIndex);
                        int finalIndex = (targetIndex + slideLen) % 60;
                        for (int i = targetIndex; i < targetIndex + slideLen; i++)
                            board.movePawnOuter(pawn, i);
                        return true;
                    }
                    else return true;
                }
            }
            else return false;
            
        }

        private boolean MoveForward(Pawn pawn, int num, Board b){

            int index = pawn.getIndex();

            if (pawn.getState() == Pawn.State.HOME){
                if (index + num < 6){
                    b.movePawnInner(pawn, index + num);
                    if (index + num == 5)
                        pawn.setState(Pawn.State.FINISHED);
                }
                else return false;
                return true;
            }

            else if (pawn.getState() == Pawn.State.MAIN){
                int targetIndex = (index + num) % 60; //to wrap around
                int homeEntrance = getPlayerHomeEntrance(pawn.getOwner());

                int spacesToHome = (homeEntrance-index+60) %60;
          
                if (num > spacesToHome + 5) 
                    return false;
                else if (spacesToHome == num)
                {
                     b.movePawnOuter(pawn, targetIndex);
                    return true;
                }
                else if (spacesToHome < num){
                    int homeInd = num - spacesToHome -1;
                    if (homeInd > 5) return false;
                    board.clearOuterPath(pawn);
                    b.movePawnInner(pawn, homeInd);
                    if (spacesToHome == 5)
                        pawn.setState(Pawn.State.FINISHED);
                    return true;
                } 
                else {
                    // Regular move on outer path
                    b.movePawnOuter(pawn, targetIndex);
                    
                    // Check for slide
                    if (canSlide(pawn, targetIndex)) {
                        int slideLen = slideLength(targetIndex);
                        int finalIndex = (targetIndex + slideLen) % 60;
                        for (int i = targetIndex; i < targetIndex + slideLen; i++)
                        b.movePawnOuter(pawn, finalIndex);
                        return true;
                    }
                    else return true;
                }
            }
            else return false;
        }


        private boolean MoveBackward(Pawn pawn, int num){
            if (pawn.getState() == Pawn.State.HOME) {
                int currentIndex = pawn.getIndex();
                int targetIndex = currentIndex - num; 

                
                if (targetIndex >= 0) {
                    board.movePawnInner(pawn, targetIndex);
                } else {
                    return false; 
                }
            }
        

            if (pawn.getState() != Pawn.State.MAIN) {
            return false;
            }
        
            int currentIndex = pawn.getIndex();
            int targetIndex = (currentIndex - num + 60) % 60;
            board.movePawnOuter(pawn, targetIndex);
            return true;
        }

        private boolean MoveBackward(Pawn pawn, int num, Board b){
            if (pawn.getState() == Pawn.State.HOME) {
                int currentIndex = pawn.getIndex();
                int targetIndex = currentIndex - num; 

                
                if (targetIndex >= 0) {
                    b.movePawnInner(pawn, targetIndex);
                } else {
                    return false; 
                }
            }
        

            if (pawn.getState() != Pawn.State.MAIN) {
            return false;
            }
        
            int currentIndex = pawn.getIndex();
            int targetIndex = (currentIndex - num + 60) % 60;
            b.movePawnOuter(pawn, targetIndex);
            return true;
        }
     
     
        
      private int getPlayerStartPosition(Player player) {
        // Exact positions as specified
        switch (player.getColor()) {
            case RED: return 4;     // Red exits at 4
            case BLUE: return 19;   // Blue exits at 19
            case YELLOW: return 34; // Yellow exits at 34
            case GREEN: return 49;  // Green exits at 49
            default: return 0;
        }
    }

    
    
    private int getPlayerHomeEntrance(Player player) {
        // Home entrance is 2 spaces before starting position
        switch (player.getColor()) {
            case RED: return 2;     // 2 before 4
            case BLUE: return 17;   // 2 before 19
            case YELLOW: return 32; // 2 before 34
            case GREEN: return 47;  // 2 before 49
            default: return 0;
        }
    }
    public boolean swap(Pawn myPawn, Pawn opPawn){
          if (opPawn.getState() != Pawn.State.MAIN) {
            return false;
        }

        
        if (opPawn.getOwner() == getCurrentPlayer()) {
            return false;
        }

        if (myPawn.getState() == Pawn.State.START){
            int theirPos = opPawn.getIndex();
            
            board.movePawnOuter(myPawn, theirPos);
            //board.movePawnHome(opPawn);
        }
        else{

        
        // Swap positions
        int myPos = myPawn.getIndex();
        int theirPos = opPawn.getIndex();
        
        board.movePawnOuter(myPawn, theirPos);
        board.movePawnOuter(opPawn, myPos);
        }
        
        needsChoice = -1;

        return true;
    }

     public boolean swap(Pawn myPawn, Pawn opPawn, Board b){
        if (opPawn.getState() != Pawn.State.MAIN) {
            return false;
        }

        if (myPawn.getState() != Pawn.State.START) {
            return false;
        }
        
        if (myPawn.getOwner() == opPawn.getOwner()) {
            return false;
        }

        // Swap positions
        int myPos = myPawn.getIndex();
        int theirPos = opPawn.getIndex();
        
        b.movePawnOuter(myPawn, theirPos);
        b.movePawnOuter(opPawn, myPos);
        

        return true;
    }
    
    
    
    public void nextTurn() {
        // reset
        if (currentCardValue != 2)
            currentPlayerIndex = (currentPlayerIndex + 1) % numberPlayers;

        currentCardValue = -1;
        selectedPawn = null;
        needsChoice = -1;
    }
     public boolean isGameOver() {
        // Only check active players for win condition
        for (Player player : players) {
                boolean allFinished = true;
                for (Pawn pawn : player.getPawns()) {
                    if (pawn.getState() != Pawn.State.FINISHED) {
                        allFinished = false;
                        break;
                    }
                }
                if (allFinished) {
                    return true;
                }
            
        }
        return false;
    }
    
    public Player getWinner() {
        for (Player player : players) {
                boolean allFinished = true;
                for (Pawn pawn : player.getPawns()) {
                    if (pawn.getState() != Pawn.State.FINISHED) {
                        allFinished = false;
                        break;
                    }
                }
                if (allFinished) {
                    return player;
                }
            
        }
        return null;
    }

    

    //currentCardValue
    //check if any pawn can move
    public boolean canMove(Player p) {
    Board testBoard = new Board(board);

    for (Pawn pawn : p.getPawns()) {
        Pawn testPawn = new Pawn(pawn);

        switch (currentCardValue) {
            case 1:
            case 2:
                return true; 
            case 3:
            case 5:
            case 8:
            case 12:
                // Only return true if MoveForward actually succeeds
               
                if(!anyInMain(p)) return false;
                else return currentCardValue <= maxMove(pawn);

            case 4:
                if(allInStart()) return false;
                else return true;

            case 7:
                if (allInStart()) return false;

                // Try every pair of pawns: (p1, p2)
                for (Pawn p1 : p.getPawns()) {
                    int max1 = maxMove(p1);
                    if (max1 == 0) continue;

                    for (Pawn p2 : p.getPawns()) {
                        if (p1 == p2) continue;

                        int max2 = maxMove(p2);
                        if (max2 == 0) continue;

                        // Try all splits i + j = 7
                        for (int i = 1; i <= 6; i++) {
                            int j = 7 - i;

                            if (i <= max1 && j <= max2) return true;
                        }
                    }
                }

    return false;

            case 10:
                if(!anyInMain(p)) return false;
                if (MoveForward(testPawn, 10, testBoard) || MoveBackward(testPawn, 1, testBoard)) return true;
                break;

            case 11:
                if(!anyInMain(p)) return false;
                else 
                    return 11 >= maxMove(testPawn);
                
                // if (MoveForward(testPawn, 11, testBoard)) return true;

                // System.out.print("seeing if this is ever reached");

                // // Check if swap with any opponent pawn is possible
                // for (Player other : players) {
                //     if (other != p) {
                //         for (Pawn op : other.getPawns()) {
                //             Pawn testOp = new Pawn(op);
                //             if (swap(testPawn, testOp, testBoard)) return true;
                //         }
                //     }
                // }
                // break;

            case 0: // Sorry card
                for (Player other : players) {
                    if (other != p) {
                        if (anyInMain(other)) return true;
                    }
                }
                return false;
            default:
                break;
        }
    }

    // If none of the pawns could legally move, return false
    return false;
}
    private boolean allInStart(){
        boolean all = true;
        for(Pawn paw : getCurrentPlayer().getPawns())
        {
            if(paw.getState() != Pawn.State.START)
            {
                all = false;
                
            }
        }
        return all;
    }

    private boolean anyInMain(Player p){
        boolean all = false;
        for(Pawn paw : p.getPawns())
        {
            if(paw.getState() == Pawn.State.MAIN)
            {
                all = true;
                
            }
        }
        return all;
    }

    private int maxMove(Pawn p){
        if (p.getState() == Pawn.State.HOME){
            int index = p.getIndex();
            return 5-index;
        }
        else if (p.getState() == Pawn.State.FINISHED || p.getState() == Pawn.State.START)
            return 0;
        else{
            int index = p.getIndex();
 
                int homeEntrance = getPlayerHomeEntrance(p.getOwner());
                int spacesToHome = (homeEntrance-index+60) %60;
          
                return spacesToHome + 6;
        

        }

    }



}