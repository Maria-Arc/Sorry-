public class Pawn {
    //Pawn - stores location, and if in start/game/home
        public enum State {
            START, MAIN, HOME, FINISHED
        }

        private State state = State.START;
        private int index = -1;
        private Player owner;
        private int pawnId;

        public Pawn(Player o, int i){
            owner = o;
            pawnId = i;
        }
        
        public Pawn(Pawn other) {
            this.owner = other.owner;  // same Player reference is fine
            this.pawnId = other.pawnId;
            this.state = other.state;
            this.index = other.index;
        }


        public State getState(){ return state; }

        public void setState(State s){ 
            state = s;
        }

        public int getIndex(){ return index;}

        public void setIndex(int i) {index = i;}

        public Player getOwner() { return owner;}

    }
