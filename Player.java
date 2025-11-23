public class Player {
    public enum Color { RED, BLUE, GREEN, YELLOW }

    private Pawn[] pawns = new Pawn[4];
    private int playerId;
    private Color color;

    Player(int id, Color c){
        playerId = id;
        for (int i = 0; i < 4; i++){
            pawns[i] = new Pawn(this);
        }
        color = c;
    }

    public Pawn[] getPawns() {
    return pawns;
    }

    public int getId(){ return playerId;}

    public Color getColor(){ return color;}

  
    

}
