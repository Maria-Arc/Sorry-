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

    private class Card{
        int value;

        public Card(int v){
            value = v;
        }
        
        public int getValue() {
        return value;
        }

        @Override
        public String toString() {
            return value == 0 ? "SORRY!" : String.valueOf(value);
        }

    }
}