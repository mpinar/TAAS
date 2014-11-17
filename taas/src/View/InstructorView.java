package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Model.Instructor;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

public class InstructorView extends JFrame {

	private JPanel contentPane;
	private Instructor instructor;
	

	/**
	 * Create the frame.
	 */
	public InstructorView(Instructor inst) {
		
		instructor = inst;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIdentification = new JLabel("New label");
		lblIdentification.setHorizontalAlignment(SwingConstants.CENTER);
		lblIdentification.setBounds(266, 21, 178, 16);
		lblIdentification.setText(instructor.name + " " + instructor.surname );
		contentPane.add(lblIdentification);
		
		JLabel lblCourses = new JLabel("Courses");
		lblCourses.setBounds(28, 53, 72, 16);
		contentPane.add(lblCourses);
		
		JComboBox cbTeaching = new JComboBox();
		cbTeaching.setBounds(112, 49, 178, 27);
		contentPane.add(cbTeaching);
	}
}
