package GUI;  //Venkat: package game.gui  //lowercase for package names

import game.MasterMind;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.*;

// ORTIZ: THIS COMMENT IS ONLY TO TEST SVN COMMIT

class MastermindSwing extends Canvas implements MouseListener, ActionListener {
    MasterMind masterMind = new MasterMind();
    private static final int ROWS = 20;
    private int drawWidth, drawHeight, currentColor;
    private int currentRow=1;
    List<Color> availableColor = masterMind.getAvailableColors();
    Color[] colors = availableColor.toArray(new Color[10]);
    private Color colorGuesses[][] = new Color[ROWS][4];
    private Color[][] response = new Color[ROWS][4];
    List<Color> userInput = new ArrayList<>();

    public void  mousePressed (MouseEvent e)
    {
        int clickXPosition = e.getX();
        int clickYPosition = e.getY();
        if(clickYPosition < drawHeight*(ROWS+1)+drawHeight/2)
        {
            clickXPosition = (clickXPosition - drawWidth) / drawWidth;
            colorGuesses[currentRow][clickXPosition] = colors[currentColor];
            userInput.add(colors[currentColor]);
            repaint();
            System.out.println( "column is " + clickXPosition );
        }
        else
        {
            currentColor = (clickXPosition - drawWidth/2) / drawWidth;
            System.out.println( "color is " + currentColor );
        }
    }
    public void mouseEntered(  MouseEvent e ) { }
    public void mouseExited(   MouseEvent e ) { }
    public void mouseClicked(  MouseEvent e ) { }
    public void mouseReleased( MouseEvent e ) { }

    public void paint(Graphics g){
        drawWidth = (int) getSize().width / 7;
        drawHeight = (int) getSize().height / (ROWS+3);
        fillBoard(g);
        drawLayout(g);
    }

    private void fillBoard(Graphics g) {
        for(int row = 0; row <= currentRow; row++)
        {
            for(int colum =0; colum < 4; colum++)
            {
                g.setColor(colorGuesses[row][colum]);
                g.fillRect(drawWidth*(colum+1), drawHeight*(row), drawWidth, drawHeight);
            }
        }
        for(int row = 0; row <= currentRow; row++)
        {
            for(int colum =0; colum < 4; colum++)
            {
                g.setColor(response[row][colum]);
                g.fillRect(5*drawWidth+colum%2*drawWidth/2, drawHeight*(row+1)+colum/2*drawHeight/2, drawWidth/2, drawHeight/2);
            }
        }
    }

    private void drawLayout( Graphics g ) {
        g.setColor(Color.BLACK);

        // draw rows
        for (int i=0; i < ROWS+1; i++)
            g.drawLine( drawWidth, drawHeight*(i+1), 6*drawWidth, drawHeight*(i+1) );
        // draw columns
        for (int i=0; i <  6; i++)
            g.drawLine( drawWidth*(i+1), drawHeight, drawWidth*(i+1), (ROWS+1)*drawHeight );
        // draw horizontal dividers
        for (int i=0; i < ROWS; i++)
            g.drawLine( drawWidth*5, (i+1)*drawHeight+drawHeight/2, drawWidth*6, (i+1)*drawHeight+drawHeight/2 );
        // draw vertical divider
        g.drawLine( drawWidth*5+drawWidth/2, drawHeight, drawWidth*5+drawWidth/2, drawHeight*(ROWS+1) );
        // draw color palette
        for (int i=0; i < 10; i++) {
            g.setColor( colors[i] );
            g.fillRect( i*drawWidth+drawWidth/2, drawHeight*(ROWS+1)+drawHeight/2, drawWidth, drawHeight );
        }
    }

    public MastermindSwing() {
        for(int row =0; row < colorGuesses.length; row++)
        {
<<<<<<< .mine
            colors[i] = masterMind.getAvailableColors().get(i);
        }
        for(int row =0; row < 4; row++)
        {
||||||| .r1173
            colors[i] = masterMind.getAvailableColors(i);
        }
        for(int row =0; row < 4; row++)
        {
=======
>>>>>>> .r1308
            for(int colum = 0; colum < 4; colum++)
            {
                colorGuesses[row][colum] = Color.WHITE;
                response[row][colum] = Color.GRAY;
            }
        }
        JFrame frame = new JFrame( "Mastermind" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setBackground( Color.WHITE );

        JButton submit = new JButton("Submit Guess");
        submit.addActionListener(this);
        addMouseListener(this);

        frame.getContentPane().add( this );
        frame.getContentPane().add(submit, BorderLayout.SOUTH);

        frame.setSize( new Dimension( 220, 450 ) );
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new MastermindSwing();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Map<MasterMind.Response, Long> userResponse = masterMind.guess(userInput);
        for(int row = 0; row != userResponse.get(MasterMind.Response.MATCH); row++)
        {
            response[currentRow][row] = Color.BLACK;
        }
        for(int row = 0; row != userResponse.get(MasterMind.Response.NO_MATCH); row++)
        {
            response[currentRow][row] = Color.WHITE;
        }
        currentRow++;
        repaint();
    }
}
