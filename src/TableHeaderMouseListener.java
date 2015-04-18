import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
 
import javax.swing.JOptionPane;
import javax.swing.JTable;
 
/**
 * A mouse listener class which is used to handle mouse clicking event
 * on column headers of a JTable.
 * @author www.codejava.net
 *
 */
public class TableHeaderMouseListener extends MouseAdapter {
     
    private JTable table;
     
    public TableHeaderMouseListener(JTable table) {
        this.table = table;
    }
     
    @Override
    public void mouseClicked(MouseEvent event) {
        int rowClicked = table.rowAtPoint(event.getPoint());
        int colClicked = table.columnAtPoint(event.getPoint());
         
        JOptionPane.showMessageDialog(table, "Column " + colClicked + " and Row " + rowClicked + " clicked");
         
        // do your real thing here...
    }
}