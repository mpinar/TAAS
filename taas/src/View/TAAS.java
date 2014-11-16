package View;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import Helper.*;
import Model.*;

public class TAAS {

	private JFrame frame;
	private JTextField tfUsername;
	private JTextField tfPassword;
	private DatabaseHelper dbh;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TAAS window = new TAAS();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TAAS() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblPassword = new JLabel("Password : ");
		lblPassword.setBounds(85, 119, 71, 16);
		frame.getContentPane().add(lblPassword);
		
		tfUsername = new JTextField();
	
		tfUsername.setBounds(168, 72, 218, 28);
		tfUsername.setColumns(17);
		frame.getContentPane().add(tfUsername);
		
		JLabel lblUsername = new JLabel("Username : ");
		lblUsername.setBounds(82, 78, 74, 16);
		frame.getContentPane().add(lblUsername);
		
		tfPassword = new JPasswordField();
	
		tfPassword.setBounds(168, 113, 218, 28);
		tfPassword.setColumns(10);
		frame.getContentPane().add(tfPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				String username = tfUsername.getText();
				String plainPassword = tfPassword.getText();
				//System.out.println("Username = "+tfUsername.getText()+"\nPassword = "+tfPassword.getText());
				dbh = new DatabaseHelper();
				
				if(dbh.authorizeUser(username, plainPassword)){
					
					Person p = dbh.getAuthorizedPerson(username);
					System.out.println(p);
					
					if(p.job == JobType.INSTRUCTOR || p.job == JobType.SUPER_INSTRUCTOR){
						Instructor ins = dbh.getInstructorFromID(p.id);
						ins.setSuperFields(p.name, p.surname, p.username, p.isAdmin);
						InstructorView iv = new InstructorView(ins);
						iv.setVisible(true);
						frame.dispose();
					}
					
				}else {
					JOptionPane.showMessageDialog(frame,
						    "\tInvalid username/password.\n\tPlease try again.",
						    "Login Error",
						    JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnLogin.setBounds(218, 153, 117, 29);
		frame.getContentPane().add(btnLogin);

		
		
	}
}
