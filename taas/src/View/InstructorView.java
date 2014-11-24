package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Helper.DatabaseHelper;
import Model.Course;
import Model.Instructor;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class InstructorView extends JFrame {

	private JPanel contentPane;
	private Instructor instructor;
	private DatabaseHelper dbh;
	private JTextField tfAdditionalRequest;
	private int messageCount = 0;

	/**
	 * Create the frame.
	 */
	public InstructorView(Instructor inst) {
		
		dbh = new DatabaseHelper();
		instructor = inst;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 445);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIdentification = new JLabel("New label");
		lblIdentification.setBounds(344, 6, 248, 16);
		lblIdentification.setHorizontalAlignment(SwingConstants.CENTER);
		lblIdentification.setText(instructor.name + " " + instructor.surname );
		contentPane.add(lblIdentification);
		
		
		JLabel lblCourses = new JLabel("Courses");
		lblCourses.setBounds(28, 53, 72, 16);
		contentPane.add(lblCourses);
		
		// get teaching information of Instructor.
		// and set them to the combobox
		ArrayList<Course> teachingList = instructor.getTeachingInformationFromDB();
		int size = teachingList.size()+1;
		String[] arr = new String[size];
		for(int i=0; i<size; i++){
			String s ="";
			if(i!=0){
				Course c = teachingList.get(i-1);
				s = c.code + " " + c.number;
			}
				arr[i] = s;
		}
		
		
		final JComboBox cbTeaching = new JComboBox(arr);
		cbTeaching.setBounds(112, 49, 178, 27);
		contentPane.add(cbTeaching);
		
		JLabel lblAdditionalRequest = new JLabel("Additional Request :");
		lblAdditionalRequest.setBounds(28, 377, 460, 16);
		contentPane.add(lblAdditionalRequest);
		
		tfAdditionalRequest = new JTextField();
		tfAdditionalRequest.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(messageCount == 0){
				JOptionPane.showMessageDialog(getParent()
						, "The requests should be seperated with commas(,).\nExample: Java, INDR");
				messageCount++;
				}
			}
		});
		tfAdditionalRequest.setBounds(172, 371, 316, 28);
		contentPane.add(tfAdditionalRequest);
		tfAdditionalRequest.setColumns(10);
	
		//createRequestPanel(); // selected dersin max asst sayisi
		
		JButton btnSaveAdditionalRequest = new JButton("Save");
		btnSaveAdditionalRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Create course;
				String s = (String) cbTeaching.getSelectedItem();
				String[] sarr = s.split(" ");
				Course c = new Course(sarr[0], Integer.parseInt(sarr[1]));
			
				String additionalReq = tfAdditionalRequest.getText();
				boolean updated = instructor.setAdditionalRequestForCourse(c,additionalReq);
				
				if(updated){
					JOptionPane.showMessageDialog(getParent(), "Your request is saved","Success",JOptionPane.INFORMATION_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(getParent(),
						    "An error ocurred while saving your request. Try again.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSaveAdditionalRequest.setBounds(533, 372, 117, 29);
		btnSaveAdditionalRequest.setToolTipText("Saves addtional request.");
		contentPane.add(btnSaveAdditionalRequest);
	}
	
	private void createRequestPanel(int max){
		
		JPanel panel = new JPanel();
		panel.setBounds(39, 105, 611, 235);
		contentPane.add(panel);
		panel.setLayout(null);
		
		
		for (int i=0; i<max; i++){
			int y = 50 + 35*i;

			JComboBox departmentBox = new JComboBox();
			departmentBox.setBounds(6, y, 188, 25);
			panel.add(departmentBox);
			
			JComboBox assistantBox = new JComboBox();
			assistantBox.setBounds(226, y, 238, 255);
			panel.add(assistantBox);
			
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnAdd.setBounds(476, y, 117, 30);
			panel.add(btnAdd);
		}
	}
}
