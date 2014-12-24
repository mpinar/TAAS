package View;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import Helper.*;
import Model.*;

public class TAAS {

	private JFrame frame;
	private JTextField tfUsername;
	private JTextField tfPassword;
	private DatabaseHelper dbh;
	
	private Image bg;
	
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
	 * @throws IOException 
	 */
	public TAAS() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 350);

			BufferedImage image = ImageIO.read(new File("taas_bg.jpg"));
			frame.setContentPane(new JLabel(new ImageIcon(image)));
			frame.setSize(image.getWidth()	,image.getHeight());

	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// Logo
		JLabel logo = new JLabel("Username : ");
		logo.setIcon(new ImageIcon("Images/ku_logo.jpg"));
		logo.setBounds(10, 10, 150, 50);
		frame.getContentPane().add(logo);
		
		// Set username area
		JLabel lblUsername = new JLabel("Username : ");
		lblUsername.setIcon(new ImageIcon("Images/username_icon.jpg"));
		lblUsername.setBounds(230, 60, 30, 30);
		frame.getContentPane().add(lblUsername);

		tfUsername = new JTextField();
		tfUsername.setBounds(260, 60, 200, 30);
		tfUsername.setColumns(17);
		frame.getContentPane().add(tfUsername);
		// Set password area
		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setIcon(new ImageIcon("Images/passwordIcon.jpg"));
		lblPassword.setBounds(230, 105, 30, 30);
		frame.getContentPane().add(lblPassword);
		
		tfPassword = new JPasswordField();
		tfPassword.setBounds(260, 105, 200, 30);
		tfPassword.setColumns(10);
		frame.getContentPane().add(tfPassword);

		JButton btnLogin = new JButton("Login");
		ImageIcon io = new ImageIcon("Images/button-red-login.jpg");
		btnLogin.setIcon(io);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String username = tfUsername.getText();
				String plainPassword = tfPassword.getText();
				//System.out.println("Username = "+tfUsername.getText()+"\nPassword = "+tfPassword.getText());
				dbh = new DatabaseHelper();

				// ISADMIN
				if(username.compareToIgnoreCase("admin") == 0  && plainPassword.compareTo("admintest") == 0){
					AdminView av = new AdminView();
					av.setVisible(true);
					frame.dispose();
				}else{

					if(dbh.authorizeUser(username, plainPassword)){

						Instructor ins = dbh.getAuthorizedInstructor(username);
						//System.out.println(ins);
						InstructorView iv = new InstructorView(ins);
						frame.dispose();
						
						try {
							Thread.sleep(1750);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						iv.setVisible(true);
					}

				}
			}
		});
		
		btnLogin.setSize(io.getIconWidth()-16, io.getIconHeight());
		btnLogin.setLocation(315, 145);
		frame.getContentPane().add(btnLogin);



	}

	 public void paintComponent(Graphics g)
	    {
	        // Draws the img to the BackgroundPanel.
	        g.drawImage(bg, 0, 0, null);
	    }

}
