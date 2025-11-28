
public class Board {
    //The layout: list/array of tiles
	//Where each pawn is

    private Pawn[] outerPath = new Pawn[60];
    private Pawn[][] homePath;
        // okay so how this works is that its and array of [playerId][home board slot]
    


    public Board(int numPlayers){
        for (int i = 0; i < 60; i++){
            outerPath[i] = null;
        }

        homePath = new Pawn[numPlayers][6];
        for (int i = 0; i < numPlayers; i++){
            for (int j = 0; j < 6; j++){

                homePath[i][j] = null;
            }
        }
   
    }
    
    

    public void movePawnOuter(Pawn p, int index){

        if (outerPath[index] == null){              //if spot is empty set the outerpath to reference that pawn and 
            outerPath[index] = p;                   //change the data inside the pawn itself to correspond
            p.setIndex(index);
            p.setState(Pawn.State.MAIN);
        }
        else{                                            //if spot is occupied send pawn to home and then put pawn there
            outerPath[index].setState(Pawn.State.START);
            outerPath[index].setIndex(-1);

            outerPath[index] = p;
            p.setIndex(index);
            p.setState(Pawn.State.MAIN);
        }
    }
    
    public void movePawnInner(Pawn p, int index){
        int ownerId = (p.getOwner()).getId();
        if (homePath[ownerId][index] == null){
            homePath[ownerId][index] = p;
            p.setState(Pawn.State.HOME);
            p.setIndex(index);
        }

    }

    public void movePawnStart(Pawn p){
        p.setState(Pawn.State.START);
        p.setIndex(-1);
    }
    

    public boolean isOuterSlotEmpty(int index){
        if (outerPath[index] == null)
            return true;
        return false;
    }

    public boolean isHomeSlotEmpty(int playerId, int index){
        if (homePath[playerId][index] == null)
            return true;
        return false;
    }



}
