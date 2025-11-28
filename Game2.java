import java.util.HashMap;

public class Game2 {

    private Player[] players;
    private Board board;
    private CardDeck deck;
    private Controller controller;
    private int currentPlayerIndex = 0;
    private int currentCardValue = -1;
    private Pawn selectedPawn = null;
    private int numberPlayers;
    private HashMap<Integer, Player.Color>slide = new HashMap<>();   
    
    // For card choices
    public enum CardChoice {
        NONE,
        SORRY,    // SORRY: start from START or swap
        TEN_FORWARD_OR_BACK,    // 10: forward 10 or back 1
        ELEVEN_FORWARD_OR_SWAP  // 11: forward 11 or swap
    }
    
    private CardChoice pendingChoice = CardChoice.NONE;
    
    public Game2(int numPlayers) { 
        numberPlayers = numPlayers;
        players = new Player[numPlayers]; // Always create 4 players
        
        
        Player.Color[] colors = {Player.Color.RED, Player.Color.BLUE, 
                                  Player.Color.YELLOW, Player.Color.GREEN};
        
        // Initialize all players but mark only the selected ones as active
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(i, colors[i]);
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
    
    // Get only active players
    public Player[] getActivePlayers() {
       return players.clone();
    }
    
    // Check if a player is active
    // public boolean isPlayerActive(Player player) {
    //     return activePlayers[player.getId()];
    // }
    
    public Board getBoard() {
        return board;
    }
    
    
    public CardDeck.Card drawCard() {
        CardDeck.Card card = deck.Draw();
         currentCardValue = card.getValue();
         pendingChoice = CardChoice.NONE;
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
    
    public CardChoice getPendingChoice() {
        return pendingChoice;
    }
    
    public boolean hasPendingChoice() {
        return pendingChoice != CardChoice.NONE;
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
            return 3;
        case 9: case 24: case 39: case 54:
            return 4;
        default:
            return -1;
    }
   }
    
    // Main move function
    public boolean movePawn(Pawn pawn) {
        if (pawn == null || currentCardValue == -1) {
            return false;
        }
        
        if (pawn.getOwner() != getCurrentPlayer()) {
            return false;
        }
        
        return executeMove(pawn, currentCardValue);
    }
    
    // For SORRY card: choose to start from START
    public boolean sorryFromStart(Pawn myPawn) {
        if (currentCardValue != 0 || pendingChoice != CardChoice.SORRY) {
            return false;
        }
        
        if (myPawn.getState() != Pawn.State.START) {
            return false;
        }
        
        for (Player player : players) {
            if (player != myPawn.getOwner() ) {
                for (Pawn opponentPawn : player.getPawns()) {
                    if (opponentPawn.getState() == Pawn.State.MAIN) {
                        int targetPos = opponentPawn.getIndex();
                        board.movePawnOuter(myPawn, targetPos);
                        pendingChoice = CardChoice.NONE;
                        return true;
                    }
                }
            }
        }
        
        return false; // No opponent to bump
    }
    
    // For SORRY 
    public boolean sorrySwap(Pawn myPawn, Pawn opponentPawn) {
        
        if (myPawn.getState() != Pawn.State.MAIN || opponentPawn.getState() != Pawn.State.MAIN) {
            return false;
        }

        
        // Swap positions
        int myPos = myPawn.getIndex();
        int theirPos = opponentPawn.getIndex();
        
        board.movePawnOuter(myPawn, theirPos);
        board.movePawnOuter(opponentPawn, myPos);
        
        pendingChoice = CardChoice.NONE;
        return true;
    }
    
    // For card 10: move backward 1
    public boolean moveBackwardOne(Pawn pawn) {
        if (currentCardValue != 10 || pendingChoice != CardChoice.TEN_FORWARD_OR_BACK) {
            return false;
        }
        
        pendingChoice = CardChoice.NONE;
        return handleMoveBackward(pawn, 1);
    }
    
    // For card 10: move forward 10
    public boolean moveForwardTen(Pawn pawn) {
        if (currentCardValue != 10 || pendingChoice != CardChoice.TEN_FORWARD_OR_BACK) {
            return false;
        }
        
        pendingChoice = CardChoice.NONE;
        return handleMoveForward(pawn, 10);
    }
    
    // For card 11: swap with opponent
    public boolean swapWithOpponent(Pawn myPawn, Pawn opponentPawn) {
       
        if (myPawn.getState() != Pawn.State.MAIN || opponentPawn.getState() != Pawn.State.MAIN) {
            return false;
        }
        
        if (opponentPawn.getOwner() == getCurrentPlayer()) {
            return false;
        }

        // Swap positions
        int myPos = myPawn.getIndex();
        int theirPos = opponentPawn.getIndex();
        
        board.movePawnOuter(myPawn, theirPos);
        board.movePawnOuter(opponentPawn, myPos);
        
        pendingChoice = CardChoice.NONE;
        return true;
    }
    
    // For card 11: move forward 11
    public boolean moveForwardEleven(Pawn pawn) {
        if (currentCardValue != 11 || pendingChoice != CardChoice.ELEVEN_FORWARD_OR_SWAP) {
            return false;
        }
        
        pendingChoice = CardChoice.NONE;
        return handleMoveForward(pawn, 11);
    }
    
    private boolean executeMove(Pawn pawn, int cardValue) {
        switch (cardValue) {
            case 0: // SORRY
                return handleSorryCard(pawn);
            case 1:
                return handleCard1(pawn);
            case 2:
                return handleCard2(pawn);
            case 3:
                return handleMoveForward(pawn, 3);
            case 4:
                return handleMoveBackward(pawn, 4);
            case 5:
                return handleMoveForward(pawn, 5);
            case 7:
                return handleMoveForward(pawn, 7);
            case 8:
                return handleMoveForward(pawn, 8);
            case 10:
                return handleCard10(pawn);
            case 11:
                return handleCard11(pawn);
            case 12:
                return handleMoveForward(pawn, 12);
            default:
                return false;
        }
    }
    
   
    private boolean handleCard1(Pawn pawn) {
        if (pawn.getState() == Pawn.State.START) {
            int startPos = getPlayerStartPosition(pawn.getOwner());
            board.movePawnOuter(pawn, startPos);
            return true;
        } else if (pawn.getState() == Pawn.State.MAIN) {
            return handleMoveForward(pawn, 1);
        }
        return false;
    }
    
    // CARD 2: Start a pawn OR move forward 2
    private boolean handleCard2(Pawn pawn) {
        if (pawn.getState() == Pawn.State.START) {
            int startPos = getPlayerStartPosition(pawn.getOwner());
            board.movePawnOuter(pawn, startPos);
            return true;
        } else if (pawn.getState() == Pawn.State.MAIN) {
            return handleMoveForward(pawn, 2);
        }
        return false;
    }
    
    // SORRY: Ask player for choice
    private boolean handleSorryCard(Pawn pawn) {
        // Check what options are available
        boolean canStartFromStart = (pawn.getState() == Pawn.State.START);
        boolean canSwap = (pawn.getState() == Pawn.State.MAIN);
        
        // Check if active opponent exists
        boolean opponentExists = false;
        for (Player player : players) {
            if (player != pawn.getOwner() ){
                for (Pawn opponentPawn : player.getPawns()) {
                    if (opponentPawn.getState() == Pawn.State.MAIN) {
                        opponentExists = true;
                        break;
                    }
                }
            }
        }
        
        if (canStartFromStart && opponentExists) {
            // Has option: set pending choice
            pendingChoice = CardChoice.SORRY;
            return false; // Don't execute yet, wait for choice
        } else if (canStartFromStart) {
            // Only option: start from START
            return sorryFromStart(pawn);
        } else if (canSwap && opponentExists) {
            // Only option: need to select opponent
            pendingChoice = CardChoice.SORRY;
            return false;
        }
        
        return false; // No valid moves
    }
    
    // CARD 10: Ask for choice
    private boolean handleCard10(Pawn pawn) {
        if (pawn.getState() != Pawn.State.MAIN) {
            return false;
        }
        
        // Set pending choice - GUI will ask
        pendingChoice = CardChoice.TEN_FORWARD_OR_BACK;
        return false; // Wait for user choice
    }
    
    // CARD 11: Ask for choice
    private boolean handleCard11(Pawn pawn) {
        if (pawn.getState() != Pawn.State.MAIN) {
            return false;
        }
        
        // Check if active opponent exists
        boolean opponentExists = false;
        for (Player player : players) {
            if (player != pawn.getOwner() ) {
                for (Pawn opponentPawn : player.getPawns()) {
                    if (opponentPawn.getState() == Pawn.State.MAIN) {
                        opponentExists = true;
                        break;
                    }
                }
            }
        }
        
        if (opponentExists) {
            // Has choice
            pendingChoice = CardChoice.ELEVEN_FORWARD_OR_SWAP;
            return false; // Wait for user choice
        } else {
            // No opponent, just move forward
            return handleMoveForward(pawn, 11);
        }
    }
    
    // Move forward on main path
    private boolean handleMoveForward(Pawn pawn, int moves) {
        if (pawn.getState() != Pawn.State.MAIN) {
            return false;
        }
        
        int currentIndex = pawn.getIndex();
        int targetIndex = (currentIndex + moves) % 60; //to wrap around
        
        // Check if entering home
        int homeEntrance = getPlayerHomeEntrance(pawn.getOwner());
        
        // Calculate if we pass through home entrance
        boolean crossesHome = false;
        for (int i = 1; i <= moves; i++) {
            int checkPos = (currentIndex + i) % 60;
            if (checkPos == homeEntrance) {
                crossesHome = true;
            }
        }
        
        if (crossesHome) {
            int stepsToHome = (homeEntrance - currentIndex + 60) % 60;
            int stepsIntoHome = moves - stepsToHome;
            
            if (stepsIntoHome >= 0 && stepsIntoHome <= 6) {
                board.movePawnInner(pawn, stepsIntoHome);
                if (stepsIntoHome == 6) {
                    pawn.setState(Pawn.State.FINISHED);
                }
                return true;
            } else if (stepsIntoHome > 6) {
                return false; // Can't overshoot
            }
        }
        
        // Regular move on outer path
        board.movePawnOuter(pawn, targetIndex);
        
        // Check for slide
        if (canSlide(pawn, targetIndex)) {
            int slideLen = slideLength(targetIndex);
            int finalIndex = (targetIndex + slideLen) % 60;
            board.movePawnOuter(pawn, finalIndex);
        }
        
        return true;
    }
    
    // Move backward on main path
    private boolean handleMoveBackward(Pawn pawn, int spaces) {
        if (pawn.getState() != Pawn.State.MAIN) {
            return false;
        }
        
        int currentIndex = pawn.getIndex();
        int targetIndex = (currentIndex - spaces + 60) % 60;
        board.movePawnOuter(pawn, targetIndex);
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
    
    public void nextTurn() {
        // reset
        
        currentPlayerIndex = (currentPlayerIndex + 1) % numberPlayers;
        currentCardValue = -1;
        selectedPawn = null;
        pendingChoice = CardChoice.NONE;
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
        // Only active players can win
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
}


