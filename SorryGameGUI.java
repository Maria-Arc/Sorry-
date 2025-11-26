import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.*;

public class SorryGameGUI extends JFrame 
{
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    private Game game;
    
    public SorryGameGUI() 
    {
        setTitle("Sorry!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        //start new game with 4 players
        game = new Game(4);
        
        //we gonna set up two main panels. The first one is the board on the left. the second one is the controls on the right
        gamePanel= new GamePanel(game);
        controlPanel = new ControlPanel(game, gamePanel);
        
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        pack(); //size windwo to fit everything
        setLocationRelativeTo(null);    //center on screen
        setVisible(true);
    }
    
    public static void main(String [] args) 
    {
        SwingUtilities.invokeLater(()->new SorryGameGUI());
    }
}


//this will draw the game board and handle pawn selection
class GamePanel extends JPanel 
{
    private Game game;
    private BufferedImage boardImage;   //board picture
    private static int  boardSize = 800;
    private static int pawnSize  = 20;
    
    //store positions for all spaces on the board
    private Point[] outerPath;  //60 spaces sorrounding the board
    private Point[][] homePath; //PLayers safe zone that is 6 spaces
    private Point[][] startAreas;   //pawn starting point
    
    //this will map each player color t oactual RGB color valus
    private static final Map<Player.Color, Color> colors = new HashMap<>();
    static 
    {
        colors.put(Player.Color.RED, new Color(200, 50, 30));      // Red
        colors.put(Player.Color.BLUE, new Color(60, 130, 220));    // Blue
        colors.put(Player.Color.YELLOW, new Color(255, 215, 0));   // Yellow
        colors.put(Player.Color.GREEN, new Color(80, 180, 100));   // Green
    }
    
    private Pawn selectedPawn = null;   //currently selected pawn
    
    public GamePanel(Game game) 
    {
        this.game = game;
        setPreferredSize(new Dimension(boardSize, boardSize)); //set panel size (w, h)
        setBackground(Color.WHITE);
        
        loadBoardImage();
        setupBoardPos();

        //listens for mouse clicks to select pawns
        addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                PawnClick(e.getPoint());
            }
        });
    }
    
    //load board image TODO: MAybe get rid of and just load
    private void loadBoardImage() 
    {
        try 
        {
            boardImage = ImageIO.read(new File("sorry-board-image.jpg"));
            System.out.println("Board image loaded");
        } 
        catch (Exception e)
        {
            System.out.println("ERROR:couldnt find sorry-board-image.jpg");
        }
    }
    
    //set up corrds for all spaces on board
    private void setupBoardPos() 
    {
        outerPath = new Point[60];
        homePath = new Point[4][6];
        startAreas = new Point[4][4];
        int h = getHeight();
        int w = getWidth();
        
        //the outer path (60 spaces around the board)
        
        //bottom edge -> 0-14. Red's side
        for (int i = 0; i<=14; i++) 
        {
            outerPath[i] = new Point(750 - i* 47 ,   750);
        }
        
        //left edge->15-29 going up
        for (int i = 15; i <= 29;i++) 
        {
            outerPath[i] = new Point(50, 800- (i- 14) * 47);
        }
        
        //top edge->30-44. L ->r
        for (int i = 30; i<= 44; i++) 
        {
            outerPath[i] = new Point((i -29) * 47, 50);
        }
        
        //left edge ->45-59 going down
        for (int i =45; i<= 59; i++) 
        {
            outerPath[i] = new Point(750, (i-44) * 47);
        }
        

        Point temp = new Point(outerPath[17].getLocation());
        //home paths -> the safe spaces leading to home (6 spaces)
        //reds home path horizontal going right
        //wrong this is blues home path
        for (int i = 0;i <6; i++) 
        {
            //homePath[0][i] = new Point(150 + i * 40, 650);
            
            if ( i == 5)
                homePath[0][i] = new Point( (int)temp.getX() + (i+1) * 50, (int)temp.getY() );
            else
               homePath[0][i] = new Point( (int)temp.getX() + (i+1) * 46, (int)temp.getY() );
        }
        
        //blues home path, start at 17 and go up
        //this is actually reds
        temp = new Point(outerPath[2].getLocation());
        for (int i = 0; i < 6; i++) 
        {   if ( i == 5)
                homePath[1][i] = new Point((int) temp.getX() , (int)temp.getY() -46 - i* 50);
            else
                 homePath[1][i] = new Point((int) temp.getX() , (int)temp.getY() -46 - i* 46);
        }
        
        //yellows home path horizontal going left
        //this is green

        temp = new Point(outerPath[47].getLocation());
        for (int i = 0; i < 6; i++) 
        {   if ( i == 5)
                homePath[2][i] = new Point((int) temp.getX()-46 - i* 50 , (int)temp.getY() );
            else
                 homePath[2][i] = new Point((int) temp.getX() -46 - i* 46 , (int)temp.getY());
        }
        
        //greens home path start and 47 go down
        //yellow
        temp = new Point(outerPath[32].getLocation());
        for (int i = 0; i < 6; i++) 
        {   if ( i == 5)
                homePath[3][i] = new Point((int) temp.getX() , (int)temp.getY() +46 + i* 50);
            else
                 homePath[3][i] = new Point((int) temp.getX() , (int)temp.getY() +46 + i* 46);
        }
        
        //start areas/where the pawns begin
        //red starts bottom right ---NEEDS WORK
    
        startAreas[0][0] = new Point(550,670);
        startAreas[0][1] = new Point(580, 670);
        startAreas[0][2] = new Point(550,700);
        startAreas[0][3] = new Point(580,700);
        
        //blue start bottom left ---NEEDS WORK
        startAreas[1][0] = new Point(100,550);
        startAreas[1][1]= new Point(130,550);
        startAreas[1][2]= new Point(100,580);
        startAreas[1][3] = new Point(130, 580);
        
        //yellow start top left --NEEDS WORK
        startAreas[2][0]= new Point(250,95);
        startAreas[2][1]= new Point(220, 95);
        startAreas[2][2]= new Point(250,130);
        startAreas[2][3] = new Point(220, 130);
        
        //green start top right --NEEDS WORK
        startAreas[3][0] = new Point(670,215);
        startAreas[3][1] = new Point(670,245);
        startAreas[3][2] = new Point(695,215);
        startAreas[3][3] =new Point(695,245);
    }
    
    //check if user clicked on a pawn and select it
    private void PawnClick(Point click) 
    {
        //check only clicking current players pawn
        Player currPlayer = game.getCurrentPlayer();
        for(Player player : game.getPlayers())
        {
            for (Pawn pawn : currPlayer.getPawns()) //loops through each player's 4 pawns
            {
                Point pawnPos= getPawnPos(pawn);
                if (pawnPos !=null) 
                {
                    //calc distance between click and pawn center
                    double distance = click.distance(pawnPos); //distance uses the pythag theorem
                    //if click is within pawn rad
                    if (distance <pawnSize) 
                    {
                        if(pawn.getOwner() == currPlayer)
                        {
                            selectedPawn = pawn;
                            game.setSelectedPawn(pawn);
                            repaint();  //redrew to show selection
                        }

                        if(opponentClickListener != null)
                        {
                            opponentClickListener.onOpponentClicked(pawn);
                        }
                        return;
                    }
                }
            }
        }   
    }

    //what is this nonsense
    public interface OppClickListener
    {
        void onOpponentClicked(Pawn pawn);
    }
    private OppClickListener opponentClickListener = null;
    public void setOppClickListener(OppClickListener listener)
    {
        this.opponentClickListener = listener;
    }
    
    //find out where to draw pawn
    private Point getPawnPos(Pawn pawn) 
    {
        switch (pawn.getState()) //checks what state pawn is in
        {
            case START: //pawn is in start area
                int playerId = pawn.getOwner().getId(); //gets which player owns this pawn
                Pawn[] pawns =pawn.getOwner().getPawns();  //gets all 4 pawns for this player
                for (int i =0; i< pawns.length; i++)  //find out which pawn it is 
                {
                    if (pawns[i] == pawn) 
                    {
                        return startAreas[playerId][i];
                    }
                }
                break;
            case MAIN:  //pawn is in main outer path. return its pos in the outer path arr
                return outerPath[pawn.getIndex()];
            case HOME:  //pawn is in home path
                return homePath[pawn.getOwner().getId()][pawn.getIndex()];  //homePath is [playerid][pos in home path]
            case FINISHED:  //pawn made it back home
                return homePath[pawn.getOwner().getId()][5];
        }
        return null;
    }
    
    //draws everything
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;       
        
        //draw board image
        if (boardImage != null) 
        {
            g2.drawImage(boardImage, 0, 0, boardSize, boardSize, null);
        } 
       
        //draw pawns on top
        drawPawns(g2);

        //show whose turn it is
        //showCurrPlayer(g2);
    }
      
    //draw pawns on da board
    private void drawPawns(Graphics2D g2) 
    {
        for (Player player : game.getPlayers()) //loop through each player
        {
            Color color =colors.get(player.getColor()); //get this player's color
            
            for (Pawn pawn : player.getPawns()) //draw each of this players pawns
            {
                Point pos = getPawnPos(pawn);
                if (pos != null) 
                {
                    //draw shadow so it is easier to identify
                    g2.setColor(color.darker());
                    g2.fillOval(pos.x - pawnSize/2 +2, pos.y- pawnSize/2 +2, pawnSize, pawnSize);
                    
                    //draw pawn bod
                    g2.setColor(color);
                    g2.fillOval(pos.x - pawnSize/2, pos.y - pawnSize/2, pawnSize, pawnSize);

                    //add highlight gives a 3d effect
                    g2.setColor(color.brighter());
                    g2.fillOval(pos.x- pawnSize/2 + 3,pos.y -pawnSize/2 + 3, pawnSize/3, pawnSize/3);
                    
                    //draw border. yellow if pawn is selected, black if not selected
                    if (pawn == selectedPawn) 
                    {
                        g2.setColor(Color.YELLOW);
                        g2.setStroke(new BasicStroke(3));   //thick line
                    } 
                    else 
                    {
                        g2.setColor(Color.BLACK);
                        g2.setStroke(new BasicStroke(2));   //normie line
                    }
                    g2.drawOval(pos.x -pawnSize/2, pos.y - pawnSize/2, pawnSize,pawnSize);
                    g2.setStroke(new BasicStroke(1)); //reset line thickness
                }
            }
        }
    }
    
    //show whose turn it is in corner 
    private void showCurrPlayer(Graphics2D g2)
    {
        Player curr = game.getCurrentPlayer();
        Color color = colors.get(curr.getColor());

        //draw colored rectangle
        g2.setColor(color);
        g2.fillRoundRect(10, 10, 180, 45,10,10);

        //draw border
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(10, 10, 180, 45, 10, 10);

        //draw text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 17));
        g2.drawString("Current: "+ curr.getColor(), 20, 38);

    }
    
    //refresh display
    public void refresh() 
    {
        repaint();
    }
    

    //update selected pawn
    public void setSelectedPawn(Pawn pawn) 
    {
        this.selectedPawn = pawn;
        repaint();
    }
}

//control paenl on right side w/ btns and game log
class ControlPanel extends JPanel 
{
    private Game game;
    private GamePanel gamePanel;
    private JButton drawCardBtn;
    private JButton movePawnBtn;
    private JButton endTurnBtn;
    private JTextArea log;
    private JLabel currCardLabel;
    private int currCard = -1;
    
    private boolean waitingForOppSwap = false;
    private Pawn myPawnSwap = null;
    

    //panel with buttons and game log on right side  
    public ControlPanel(Game game, GamePanel gamePanel) 
    {
        this.game = game;
        this.gamePanel = gamePanel;
        
        setPreferredSize(new Dimension(280, 800));  //set size (w,h)
        setBackground(new Color(240, 240, 245));


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));   //boxlayout to stack things vertically
        
        setupControls();
    }
    
    //set up all buttons and display area
    private void setupControls() 
    {
        //title at top
        JLabel title = new JLabel("Game Controls");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);    //centers horizontally
        add(title);
        add(Box.createVerticalStrut(20));   //for spacing 
        
        //card display box
        JPanel cardBox = new JPanel();
        //cardBox.setLayout(new BorderLayout());
        cardBox.setMaximumSize(new Dimension(250, 120));
        cardBox.setBackground(Color.WHITE);
        
        currCardLabel = new JLabel("Draw a card");
        currCardLabel.setFont(new Font("Arial", Font.BOLD, 42));
        //currCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardBox.add(currCardLabel);
        add(cardBox);
        add(Box.createVerticalStrut(20));   //for spacing
        
        //draw card button
        drawCardBtn =makeButton("Draw Card", new Color(70, 130, 180));
        drawCardBtn.addActionListener(e -> drawCard());
        add(drawCardBtn);
        add(Box.createVerticalStrut(10));   //for spacing
        
        // Move Pawn button
        movePawnBtn = makeButton("Move Pawn", new Color(60, 179, 113));
        movePawnBtn.setEnabled(false);
        movePawnBtn.addActionListener(e -> movePawn());
        add(movePawnBtn);
        add(Box.createVerticalStrut(10));   //for spacing
        
        //end turn btn. disabled until move is made
        endTurnBtn = makeButton("End Turn", new Color(220, 100, 70));
        endTurnBtn.setEnabled(false);
        endTurnBtn.addActionListener(e ->endTurn());
        add(endTurnBtn);
        add(Box.createVerticalStrut(20));   //for spacing
        
        //game log
        log =new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(250, 450));
        add(scroll);
        
        //start messages 
        log.append("SORRY! Game Started\n");
        log.append("Red's turn.\n");
    }
    
    //create stylish button
    private JButton makeButton(String text, Color color) 
    {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 45));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        
        //little tiny hover effect
        btn.addMouseListener(new MouseAdapter() 
        {
            public void mouseEntered(MouseEvent e) 
            {
                if (btn.isEnabled()) 
                {
                    btn.setBackground(color.brighter());
                }
            }
            public void mouseExited(MouseEvent e) 
            {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    //handles draw card button click
    private void drawCard() 
    {
        currCard = game.drawCard();
        String text;
        if (currCard == 0)
        { 
            text = "SORRY!";
        } 
        else
        {
            text = String.valueOf(currCard); //converts int to string
        }
        currCardLabel.setText(text);
        
        if (currCard == 0) 
        {
            currCardLabel.setForeground(Color.RED);
        } 
        else 
        {
            currCardLabel.setForeground(Color.BLACK);
        }
        
        log.append(game.getCurrentPlayer().getColor() + " drew: " + text + "\n");
        

        //check if player can from from START. can only leave with 1, 2, or SORRY (0)
        if(currCard != 1 && currCard != 2 && currCard != 0)
        {
            boolean allInStart = true;
            for(Pawn pawn : game.getCurrentPlayer().getPawns())
            {
                if(pawn.getState() != Pawn.State.START)
                {
                    allInStart = false;
                    break;
                }
            }
            if(allInStart)
            {
                log.append("Cannot leave START with " + text + ". Need 1 or 2.\n");
            }
        }
        drawCardBtn.setEnabled(false);
        movePawnBtn.setEnabled(true);
        endTurnBtn.setEnabled(true);
    }
    
    //pawn move click
    private void movePawn() 
    {
        if (game.getSelectedPawn()== null) 
        {
            log.append("Error: Select pwan first\n");

            //show popup message. I swear we cannot have this many 
            JOptionPane.showMessageDialog(this, "click on pawn first");
            return;
        }
        Pawn pawn = game.getSelectedPawn();

        //checks start rule - can only leave start area with 1, 2, or sorry
        if(pawn.getState() == Pawn.State.START)
        {
            if(currCard != 1 && currCard != 2 && currCard != 0)
            {
                log.append("cant leave start with " + currCard + ".\n");
                JOptionPane.showMessageDialog(this, "can only leave start with a 1, 2, or SORRY! card");
                return;
            }
        }
        boolean success= game.movePawn(pawn);

        //check if card requires a choice
        if(game.hasPendingChoice())
        {
            handleCardChoice(pawn);
            return;
        }
        if (success) 
        {
            log.append("Pawn moved\n");
            gamePanel.refresh();
            
            //check for win
            if (game.isGameOver()) 
            {
                Player winner = game.getWinner();
                log.append(winner.getColor() + " WINS\n");

                //show win popup
                JOptionPane.showMessageDialog(this, winner.getColor() + " wins!");
            }
        } 
        else 
        {
            log.append("Invalid move\n");

            //show error popup
            JOptionPane.showMessageDialog(this, "cannot move that pawn with this card.");
        }
        
        movePawnBtn.setEnabled(false);
    }
    

    private void handleCardChoice(Pawn pawn)
    {
        Game.CardChoice choice = game.getPendingChoice();
        switch (choice)
        {
            case SORRY_START_OR_SWAP:
                handleSorryChoice(pawn);
                break;
            case TEN_FORWARD_OR_BACK:
                handleTenChoice(pawn);
                break;
            case ELEVEN_FORWARD_OR_SWAP:
                handleElevenChoice(pawn);
                break;
            default:
                break;
        }
    }
    private void handleSorryChoice(Pawn pawn)
    {
        boolean canStart;
        boolean hasOpp = false;
        if(pawn.getState()==Pawn.State.START)
        {
            canStart = true;
        }
        else
        {
            canStart = false;
        }
        for(Player player : game.getPlayers())
        {
            if(player != game.getCurrentPlayer())
            {
                for(Pawn oppPawn : player.getPawns())
                {
                    if(oppPawn.getState() == Pawn.State.MAIN)
                    {
                        hasOpp = true;
                        break;
                    }
                }
            }
        }

        if(canStart && hasOpp)  //both options are available
        {
            int choice = JOptionPane.showOptionDialog(this, 
                "Choose your move:",
                "SORRY!",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Move from Start", "Swap with Opponent", "Cancel"},
                "Move from start");
            if(choice == 0)
            {

                if(game.sorryFromStart(pawn))
                {
                    log.append("Moved from start and bumped opp\n");
                    gamePanel.refresh();
                    movePawnBtn.setEnabled(false);
                }
                else
                {
                    log.append("no opp to bump\n");
                }
            }
            else if (choice ==1)
            {
                log.append("Select opp pawn to swap with.\n");
                myPawnSwap = pawn;
                gamePanel.setOppClickListener(opponentPawn -> {
                    if(opponentPawn.getOwner() != game.getCurrentPlayer())
                    {
                        if(game.sorrySwap(myPawnSwap, opponentPawn))
                        {
                            log.append("swapped with opponent\n");
                            gamePanel.refresh();
                            movePawnBtn.setEnabled(false);
                            gamePanel.setOppClickListener(null);
                            myPawnSwap = null;
                        }
                        else
                        {
                            log.append("Swap failed\n");
                        }
                    }
                    else
                    {
                        log.append("Must click an opponent pawn\n");
                    }
                });
            }
        }
        else if(canStart)
        {
            if(game.sorryFromStart(pawn))
            {
                log.append("moved from start and bumped opp\n");
                gamePanel.refresh();
                movePawnBtn.setEnabled(false);
            }
            else
            {
                log.append("no opp to bump\n");
            }
        }
        else if(hasOpp && pawn.getState() == Pawn.State.MAIN)
        {
            log.append("click an opp pawn to swap with\n");
            myPawnSwap = pawn;
            gamePanel.setOppClickListener(opponentPawn -> {
                if(opponentPawn.getOwner()!=game.getCurrentPlayer())
                {
                    if(game.sorrySwap(myPawnSwap, opponentPawn))
                    {
                        log.append("swapped w/ opp\n");
                        gamePanel.refresh();
                        movePawnBtn.setEnabled(false);
                        gamePanel.setOppClickListener(null);
                        myPawnSwap = null;
                    }
                    else
                    {
                        log.append("swap failed\n");
                    }
                }
                else
                {
                    log.append("must click an opp pawn\n");
                }
            });
        }
        else
        {
            log.append("no valid SORRY moves\n");
        }
    }
    private void handleTenChoice(Pawn pawn) 
    {
        int choice = JOptionPane.showOptionDialog(this,
            "Card 10 - Choose:",
            "Card 10",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Forward 10", "Backward 1"},
            "Forward 10");
            
        boolean success = (choice == 0) ? game.moveForwardTen(pawn) : game.moveBackwardOne(pawn);
        
        if (success) 
        {
            log.append("Pawn moved!\n");
            gamePanel.refresh();
        }
    }
    
    private void handleElevenChoice(Pawn pawn) 
    {
        // Check if opponent exists
        boolean hasOpponent = false;
        for (Player player : game.getPlayers()) 
        {
            if (player != game.getCurrentPlayer()) 
            {
                for (Pawn opponentPawn : player.getPawns()) 
                {
                    if (opponentPawn.getState() == Pawn.State.MAIN) 
                    {
                        hasOpponent = true;
                        break;
                    }
                }
            }
        }
        
        if (hasOpponent) 
        {
            int choice = JOptionPane.showOptionDialog(this,
                "Card 11 - Choose your move:",
                "Card 11",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Forward 11", "Swap with Opponent", "Cancel"},
                "Forward 11");
                
            if (choice == 0) 
            {
                if (game.moveForwardEleven(pawn)) 
                {
                    log.append("Pawn moved forward 11!\n");
                    gamePanel.refresh();
                    movePawnBtn.setEnabled(false);
                }
            } 
            else if (choice == 1) 
            {
                log.append("Click an opp pawn to swap with.\n");
                myPawnSwap = pawn;
                gamePanel.setOppClickListener(opponentPawn -> {
                    if (opponentPawn.getOwner() != game.getCurrentPlayer()) 
                    {
                        if (game.swapWithOpponent(myPawnSwap, opponentPawn)) 
                        {
                            log.append("Swapped with opp!\n");
                            gamePanel.refresh();
                            movePawnBtn.setEnabled(false);
                            gamePanel.setOppClickListener(null);
                            myPawnSwap = null;
                        } 
                        else 
                        {
                            log.append("Swap failed!\n");
                        }
                    } 
                    else 
                    {
                        log.append("Must click an opp pawn!\n");
                    }
                });
            }
        } 
        else 
        {
            //No opponent just move forward
            if (game.moveForwardEleven(pawn)) 
            {
                log.append("Pawn moved forward 11!\n");
                gamePanel.refresh();
                movePawnBtn.setEnabled(false);
            }
        }
    }
    //handles end turn button
    private void endTurn() 
    {
        game.nextTurn();
        currCard = -1;
        log.append("----------------\n");
        log.append(game.getCurrentPlayer().getColor() + "'s turn\n");
        
        //reset display
        currCardLabel.setText("Draw a card");
        currCardLabel.setForeground(Color.BLACK);
        
        //reset buttons
        drawCardBtn.setEnabled(true);
        movePawnBtn.setEnabled(false);
        endTurnBtn.setEnabled(false);
        
        //clear selection
        gamePanel.setSelectedPawn(null);
        gamePanel.refresh();
    }
}
