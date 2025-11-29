public class Player {
    public enum Color { RED, BLUE, GREEN, YELLOW }

    private Pawn[] pawns;
    private int playerId;
    private Color color;
    int numberPawns;

    Player(int id, Player.Color c,  int numPawns){
        pawns = new Pawn[numPawns];
        playerId = id;
        for (int i = 0; i < numPawns; i++){
            pawns[i] = new Pawn(this, i);
        }
        color = c;
        numberPawns = numPawns;
    }

    public Pawn[] getPawns() {
    return pawns;
    }

    // public Pawn[] getOuterPawns(){
    //     int x = 0;
    //     for(int i = 0; i < 4; i++){
    //         if (pawns[i].getState() == Pawn.State.MAIN) 
    //             x++;
    //     }
    //     Pawn[] arr = new Pawn[x];
    //     for(int i = 0; i < x; i++){
    //         if (pawns[i].getState() == Pawn.State.MAIN) 
    //             arr[i] = pawns[i];     //THIS IS WRONG FIGURE IT OUT
    //     }
    // }

    public Pawn getPawn( int x){
        return pawns[x];
    }

    public int getId(){ return playerId;}

    public Color getColor(){ return color;}

  
    

}
