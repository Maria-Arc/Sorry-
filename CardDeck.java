import java.util.*;

public class CardDeck {
    private LinkedList<Card> deck = new LinkedList<>();
    
    public CardDeck(){
        addCards();
    }

    public Card Draw(){
        Card c = deck.poll();
        if (deck.size() == 0)
            addCards();
        return c;
    }

    private void addCards(){
                deck.add(new Card(1));
        for (int i = 0; i < 13; i++){
            if ( i != 6 && i != 9){
                for (int j = 0; j < 4; j++){
                    deck.add(new Card(i));
                }
            }
        }

        Collections.shuffle(deck);
    }

    public class Card{
        private int value;
        private String rule;

        public Card(int v){
            value = v;

            switch (value){
                case 1:
                    rule = "Move from Start or move forward 1";
                    break;
                case 2:
                    rule = "Move from Start or move forward 2";
                    break;
                case 3:
                    rule = "Move forward 3";
                    break;
                case 4:
                    rule = "Move backward 4";
                    break;
                case 5:
                    rule = "Move forward 5";
                    break;
                case 7:
                      rule = "Move forward 7 or split between two pawns";
                    break;
                 case 8:
                    rule = "Move forward 8";
                    break;
                case 10:
                    rule = "Move forward 10 or backward 1";
                    break;
                case 11:
                    rule = "Move forward 11, or swap with with opponents pawn";
                    break;
                case 12:
                    rule = "Move forward 12";
                    break;
                case 0:
                    rule = "Move from Start and switch places with an oppnent, whom you bump back to Start";

            }
        }
        
        public int getValue() {
        return value;
        }

        public String getRule(){
            return rule;
        }

        @Override
        public String toString() {
            return value == 0 ? "SORRY!" : String.valueOf(value);
        }

    }
}