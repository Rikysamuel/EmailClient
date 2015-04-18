import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

/**
 * A Swing program demonstrates how to handle mouse clicking event 
 * on column headers of a JTable component.
 * @author www.codejava.net
 *
 */
public class JTableHeaderMouseClickDemo extends JFrame {
	
	private JTable table;
	
	public JTableHeaderMouseClickDemo() {
		super("JTable Column Header Mouse Click Demo");
	
		// constructs the table
		String[] columnNames = new String[] {"Title", "Author", "Published Date"};
		String[][] rowData = new String[][] {
			{"Spring in Action", "Craig Walls", "June 29th 2011"},	
			{"Struts 2 in Action", "Donald Brown", "May 1st 2008"},	
			{"Hibernate Made Easy", "Cameron Wallace McKenzie", "April 25th 2008"},	
		};
				
		table = new JTable(rowData, columnNames);
		table.setAutoCreateRowSorter(true);
		
		JTableHeader header = table.getTableHeader(); 
		header.addMouseListener(new TableHeaderMouseListener(table));
		
		add(new JScrollPane(table));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 150);
		setLocationRelativeTo(null);		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JTableHeaderMouseClickDemo().setVisible(true);
			}
		});
	}
}