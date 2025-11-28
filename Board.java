
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
    
    
    public boolean movePawnOuter(Pawn p, int index)
    {
        //check who is at target position
        Pawn target = outerPath[index];
        
        if (target !=null && target.getOwner()== p.getOwner()) 
        {
            //your own pawn is there, move fails. pawn stays where it is
            return false;
        }
        
        //clear old pawns position
        if (p.getState() == Pawn.State.MAIN && p.getIndex() >= 0) {
            outerPath[p.getIndex()] = null;
        }
        
        if (target == null) 
        {
            //the space is empty
            outerPath[index] = p;
            p.setIndex(index);
            p.setState(Pawn.State.MAIN);
            return true;
        }
        else 
        {
            //opponent pawn is there. bump them back to START
            target.setState(Pawn.State.START);
            target.setIndex(-1);
            
            //move your pawn to that location
            outerPath[index] =p;
            p.setIndex(index);
            p.setState(Pawn.State.MAIN);
            return true;
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
