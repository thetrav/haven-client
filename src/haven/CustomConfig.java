 /**
 * @(#)CustomConfig.java
 *
 *
 * @author
 * @version 1.00 2009/10/19
 */

package haven;
import java.io.*;
import java.text.NumberFormat;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CustomConfig {

	static class IntegerTextField extends JTextField {

	    final static String badchars
	       = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";

	    public void processKeyEvent(KeyEvent ev) {

	        char c = ev.getKeyChar();

	        if((Character.isLetter(c) && !ev.isAltDown())
	           || badchars.indexOf(c) > -1) {
	            ev.consume();
	            return;
	        }
	        if(getText().length() >= this.getColumns() && KeyEvent.VK_BACK_SPACE != ev.getKeyCode())
	        {
	        	ev.consume();
	        	return;
	        }
	        else super.processKeyEvent(ev);

	    }
	}
	public static Coord windowSize = new Coord(1024, 768);
	public static int sfxVol = 100;
	public static String ircServerAddress = "irc.synirc.net";
	public static String ircChannelList = "#Haven";
	public static int wdgtID = 1000;
    public static boolean load() {
    	try{
    		BufferedReader reader = new BufferedReader(new FileReader("config.cfg"));
    		String[] data;
    		data = reader.readLine().split(" ", 2);
    		windowSize.x = Integer.parseInt(data[0].trim());
    		windowSize.y = Integer.parseInt(data[1].trim());
    		sfxVol = Integer.parseInt(reader.readLine().trim());
    		ircServerAddress = reader.readLine().trim();
    		ircChannelList = reader.readLine().trim();
    		reader.close();
    		if(windowSize.x < 800 || windowSize.y < 600)
    		{
    			System.out.println("Window size must be at least 800x600");
    			windowSize = new Coord(800,600);
    		}
    		return true;
    	}catch (FileNotFoundException fileNotFound){
    	}catch (IOException IOExcep){
    		IOExcep.printStackTrace();
    	}
    	return false;
    }
    public static double getSFXVolume()
    {
    	return (double)sfxVol/100;
    }
    public static void saveSettings()
    {
    	try{
    			File cfg = new File("config.cfg");
    			BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
    			writer.write(windowSize.x + " " + windowSize.y + "\n");
    			writer.write(Integer.toString(sfxVol) + "\n");
    			writer.write(ircServerAddress + "\n");
    			writer.write(ircChannelList + "\n");
    			System.out.println(cfg.getAbsolutePath());
    			writer.close();
    	}catch(IOException e){}
    }
    public static void main(final String args[])
    {
    	if(!load())
    	{
    		final JFrame configFrame = new JFrame("Screen Size");
    		Container contentPane = configFrame.getContentPane();
    		JButton startBtn = new JButton("Start!");
    		final IntegerTextField xField = new IntegerTextField();
    		final IntegerTextField yField = new IntegerTextField();
    		final JRadioButton typeStandard = new JRadioButton("Standard resolution:", true);
    		final JRadioButton typeCustom = new JRadioButton("Custom resolution:", false);
    		final JComboBox stdRes = new JComboBox(new Coord[]{
    			new Coord(800, 600),
    			new Coord(1024, 768),
    			new Coord(1280, 720),
    			new Coord(1280, 800)
    		});

    		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		stdRes.setSelectedIndex(0);
    		stdRes.setEditable(false);
    		xField.setColumns(4);
    		yField.setColumns(4);
    		xField.setText("800");
    		yField.setText("600");
    		xField.setEditable(false);
    		yField.setEditable(false);
    		contentPane.setLayout(new FlowLayout());
    		contentPane.add(typeStandard);
    		contentPane.add(stdRes);
    		contentPane.add(typeCustom);
    		contentPane.add(xField);
    		contentPane.add(yField);
    		contentPane.add(startBtn);

    		typeStandard.addChangeListener(new ChangeListener(){
    			public void stateChanged(ChangeEvent e)
    			{
    				if(!typeStandard.isSelected() && !typeCustom.isSelected())
    				{
    					typeStandard.setSelected(true);
    				}
    				if(typeStandard.isSelected())
	    			{
	    				stdRes.enable();
	    				typeCustom.setSelected(false);
	    			} else
	    			{
	    				stdRes.disable();
	    			}
    			}
    		});
    		typeCustom.addChangeListener(new ChangeListener(){
    			public void stateChanged(ChangeEvent e)
    			{
    				if(!typeStandard.isSelected() && !typeCustom.isSelected())
    				{
    					typeCustom.setSelected(true);
    				}
    				if(typeCustom.isSelected())
    				{
    					xField.enable();
    					yField.enable();
    					xField.setEditable(true);
    					yField.setEditable(true);
    					typeStandard.setSelected(false);
    				} else
    				{
    					xField.setEditable(false);
    					yField.setEditable(false);
    					xField.disable();
    					yField.disable();
    				}
    			}
    		});
    		xField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				if(Integer.parseInt(xField.getText()) < 800)
	    			{
	    				xField.setText("800");
	    			}
    			}
    		});
    		yField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				if(Integer.parseInt(yField.getText()) < 600)
    				{
    					yField.setText("600");
    				}
    			}
    		});
    		startBtn.addActionListener(new ActionListener(){
	   			public void actionPerformed(ActionEvent e)
    			{
    				windowSize.x = stdRes.isEnabled() ? ((Coord)stdRes.getSelectedItem()).x
    												  : Integer.parseInt(xField.getText());
    				windowSize.y = stdRes.isEnabled() ? ((Coord)stdRes.getSelectedItem()).y
    												  : Integer.parseInt(yField.getText());
    				saveSettings();
    				Thread mainThread = new Thread(){
    					public void run()
    					{
    						MainFrame.main(args);
    					}
    				};
    				mainThread.start();
    				configFrame.disable();
    				configFrame.setVisible(false);
    			}
    		});
    		configFrame.pack();
    		Toolkit toolkit = Toolkit.getDefaultToolkit();
    		configFrame.setLocation((int)(toolkit.getScreenSize().getWidth() - configFrame.getWidth())/2,
    								(int)(toolkit.getScreenSize().getHeight() - configFrame.getHeight())/2);
    		configFrame.setVisible(true);
    	} else
    	{
    		MainFrame.main(args);
    	}
    }
}