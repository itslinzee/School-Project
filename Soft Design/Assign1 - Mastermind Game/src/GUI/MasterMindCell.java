package GUI;

import javax.swing.*;

public class MasterMindCell extends JButton {

    public final int position;

    public MasterMindCell(int thePosition) {
        position = thePosition;
        setSize(50, 50);
    }
}
