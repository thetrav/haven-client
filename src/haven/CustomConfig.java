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
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

public class CustomConfig {

	static class FilteredTextField extends JTextField {

	    String defbadchars = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";
	    String badchars = defbadchars;
	    boolean noLetters = false;
	    boolean noNumbers = false;
	    int maxCharacters = 0;

	    public void processKeyEvent(KeyEvent ev) {

	        char c = ev.getKeyChar();

	        if(Character.isDigit(c) && noNumbers && !ev.isAltDown() || badchars.indexOf(c) > -1)
	        {
	            ev.consume();
	            return;
	        }
	        if(Character.isLetter(c) && noLetters && !ev.isAltDown() || badchars.indexOf(c) > -1)
	        {
	        	ev.consume();
	        	return;
	        }
	        if(	   getText().length() >= maxCharacters
	        	&& maxCharacters > 0
	        	&& ev.getKeyCode() != KeyEvent.VK_BACK_SPACE
	        	&& ev.getKeyCode() != KeyEvent.VK_LEFT
	        	&& ev.getKeyCode() != KeyEvent.VK_RIGHT
	        	&& ev.getKeyCode() != KeyEvent.VK_HOME
	        	&& ev.getKeyCode() != KeyEvent.VK_END)
	        {
	        	ev.consume();
	        	return;
	        }
	        super.processKeyEvent(ev);

	    }
	    public void setBadChars(String badchars)
	    {
	    	this.badchars = badchars;
	    }
	    public void setMaxCharacters(int maxChars)
	    {
	    	maxCharacters = maxChars;
	    }
	    public void setDefaultBadChars()
	    {
	    	badchars = defbadchars;
	    }
	    public void setNoNumbers(boolean state)
	    {
	    	noNumbers = state;
	    }
	    public void setNoLetters(boolean state)
	    {
	    	noLetters = state;
	    }
	}
	public static Coord windowSize = new Coord(800, 600);
	public static int sfxVol = 100;
	public static int musicVol = 100;
	public static String ircServerAddress = "irc.synirc.net";
	public static List<Listbox.Option> ircChannelList = new ArrayList<Listbox.Option>();
	public static String ircDefNick = "";
	public static String ircAltNick = "";
	public static int wdgtID = 1000;
	public static boolean isMusicOn = true;
	public static boolean isSoundOn = true;
	public static boolean isIRCOn = true;
	public static boolean hasNightVision = false;

	private static Hashtable tags;

	public static void setDefaults()
	{
		windowSize = new Coord(800,600);
		sfxVol = 100;
		musicVol = 100;
		ircServerAddress = "irc.synirc.net";
		ircChannelList.clear();
		ircChannelList.add(new Listbox.Option("#Haven", ""));
		ircDefNick = "";
		ircAltNick = "";
		isMusicOn = true;
		isSoundOn = true;
		hasNightVision = false;
	}
    public static boolean load() {
    	BufferedReader reader = null;
    	try{
    		SAXParserFactory spFactory = SAXParserFactory.newInstance();
	    	SAXParser saxParser = spFactory.newSAXParser();

	    	XMLReader xmlReader = saxParser.getXMLReader();
		    xmlReader.setContentHandler(new DefaultHandler(){
		    	private boolean ircElementActive = false;
		    	public void startDocument() throws SAXException
			    {
			    	tags = new Hashtable();
			    }
			    public void startElement(String namespaceURI, String localName,
			    							String qName, Attributes atts) throws SAXException
			    {
			    	String value;
			    	String key = qName.toUpperCase().trim();
			    	if(key.equals("SCREENSIZE")){
			    		value = atts.getValue("width") == null ? "1024" : atts.getValue("width");
			    		windowSize.x = Integer.parseInt(value);

			    		value = atts.getValue("height") == null ? "1024" : atts.getValue("height");
			    		windowSize.y = Integer.parseInt(value);
			    	}else if(key.equals("SOUND")){
			    		value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
			    		isSoundOn = Boolean.parseBoolean(value);

			    		value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
			    		sfxVol = Integer.parseInt(value);
			    	}else if(key.equals("MUSIC")){
			    		value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
			    		isMusicOn = Boolean.parseBoolean(value);

			    		value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
			    		musicVol = Integer.parseInt(value);
			    	}else if(key.equals("IRC")){
			    		ircElementActive = true;
			    		ircChannelList.clear();
			    		value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
			    		isIRCOn = Boolean.parseBoolean(value);

			    		value = atts.getValue("server") == null ? "irc.synirc.net" : atts.getValue("server");
			    		ircServerAddress = value;

			    		value = atts.getValue("default-nick") == null ? "" : atts.getValue("default-nick");
			    		ircDefNick = value;

			    		value = atts.getValue("alternate-nick") == null ? "" : atts.getValue("alternate-nick");
			    		ircAltNick = value;
			    	}else if(key.equals("CHANNEL") && atts.getValue("name") != null && ircElementActive){
			    		value = atts.getValue("password") == null ? " " : " " + atts.getValue("password");
			    		Listbox.Option chan = new Listbox.Option(atts.getValue("name"),value);
			    		ircChannelList.add(chan);
			    	}
			    }
			    public void endElement(String namespaceURI, String localName,
			    						String qName) throws SAXException
			    {
			    	if(ircElementActive && localName == "IRC")
			    	{
			    		ircElementActive = false;
			    	}
			    }
			});
			xmlReader.parse("config.xml");
    		if(windowSize.x < 800 || windowSize.y < 600)
    		{
    			System.out.println("Window size must be at least 800x600");
    			windowSize = new Coord(800,600);
    		}
    		return true;
    	}catch (FileNotFoundException fileNotFound){
    		System.out.println("Config file not found, creating a new one");
    	}catch (IOException IOExcep){
    		IOExcep.printStackTrace();
    	}catch (NullPointerException NPExcep)
    	{
    		System.out.println("Wrong config file format, creating a new one");
    	}catch (NumberFormatException NFExcep)
    	{
    		System.out.println("Wrong config file format, creating a new one");
    	}catch (ParserConfigurationException pcExcep){
    		pcExcep.printStackTrace();
    	}catch (SAXException saxExcep){
	    	saxExcep.printStackTrace();
	    }finally{
    		try
    		{
    			reader.close();
    		}catch(Exception e){}
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
    			File cfg = new File("config.xml");
    			BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
    			writer.write("<?xml version=\"1.0\" ?>\n");
    			writer.write("<CONFIG>");
    			writer.write("\t<SCREENSIZE width=\"" + windowSize.x + "\" height=\"" + windowSize.y + "\"/>\n");
    			writer.write("\t<SOUND enabled=\"" + Boolean.toString(isSoundOn)
    						+ "\" volume=\"" + Integer.toString(sfxVol) + "\"/>\n");
    			writer.write("\t<MUSIC enabled=\"" + Boolean.toString(isMusicOn)
    						+ "\" volume=\"" + Integer.toString(musicVol) + "\"/>\n");
    			writer.write("\t<IRC enabled=\"" + Boolean.toString(isIRCOn)
    						+ "\" server=\"" + ircServerAddress
    						+ "\" default-nick=\"" + ircDefNick
    						+ "\" alternate-nick=\"" + ircAltNick + "\">\n");
    			for(Listbox.Option channel : ircChannelList)
    			{
    				writer.write("\t\t<CHANNEL name=\"" + channel.name + "\" password=\"" + channel.disp + "\"/>\n");
    			}
    			writer.write("\t</IRC>\n");
    			writer.write("</CONFIG>");
    			writer.close();
    	}catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }
    public static void main(final String args[])
    {
    	if(!load())
    	{
    		setDefaults();
    		final JFrame configFrame = new JFrame("Screen Size");
    		Container contentPane = configFrame.getContentPane();
    		final JPanel clientSettingsPanel = new JPanel(new GridBagLayout(), true);
    		final JPanel ircSettingsPanel = new JPanel(new GridBagLayout(), true);
    		JButton startBtn = new JButton("Start!");
    		GridBagConstraints constraints;
    		final FilteredTextField xField = new FilteredTextField();
    		final FilteredTextField yField = new FilteredTextField();
    		final FilteredTextField ircDefNickField = new FilteredTextField();
    		final FilteredTextField ircAltNickField = new FilteredTextField();
    		final JRadioButton typeStandard = new JRadioButton("Standard resolution:", true);
    		final JRadioButton typeCustom = new JRadioButton("Custom resolution:", false);
    		final JComboBox stdRes = new JComboBox(new Coord[]{
    			new Coord(800, 600),
    			new Coord(1024, 768),
    			new Coord(1280, 720),
    			new Coord(1280, 768),
    			new Coord(1280, 800)
    		});

    		configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    		stdRes.setSelectedIndex(0);
    		stdRes.setEditable(false);

    		xField.setNoLetters(true);
    		yField.setNoLetters(true);
    		xField.setColumns(4);
    		yField.setColumns(4);
    		xField.setText("800");
    		yField.setText("600");
    		xField.setEditable(false);
    		yField.setEditable(false);

    		ircDefNickField.setBadChars("@#$%^~&? ");
    		ircAltNickField.setBadChars("@#$%^~&? ");
    		ircDefNickField.setColumns(10);
    		ircAltNickField.setColumns(10);
    		ircDefNickField.setMaxCharacters(30);
    		ircAltNickField.setMaxCharacters(30);

    		contentPane.setLayout(new GridBagLayout());

    		//	Adding client components
    		constraints = new GridBagConstraints();
    		constraints.anchor = GridBagConstraints.WEST;

    		constraints.gridx = 0;
    		constraints.gridy = 0;
    		clientSettingsPanel.add(typeStandard, constraints);

    		constraints.gridx = 1;
    		constraints.gridwidth = 2;
    		clientSettingsPanel.add(stdRes, constraints);

    		constraints.gridx = 0;
    		constraints.gridy = 1;
    		constraints.gridwidth = 1;
    		clientSettingsPanel.add(typeCustom, constraints);

    		constraints.gridx = 1;
    		clientSettingsPanel.add(xField, constraints);

    		constraints.gridx = 2;
    		clientSettingsPanel.add(yField, constraints);

    		//	Adding irc components
    		constraints.gridx = 0;
    		constraints.gridy = 0;
    		ircSettingsPanel.add(new JLabel("Default IRC Nickname:"), constraints);

    		constraints.gridy = 1;
    		ircSettingsPanel.add(new JLabel("Alternate Nickname:"), constraints);

    		constraints.gridx = 1;
    		constraints.gridy = 0;
    		ircSettingsPanel.add(ircDefNickField, constraints);

    		constraints.gridy = 1;
    		ircSettingsPanel.add(ircAltNickField, constraints);

    		//	Adding panel components
    		constraints.anchor = GridBagConstraints.NORTH;
    		constraints.gridx = 0;
    		constraints.gridy = 0;
    		contentPane.add(clientSettingsPanel, constraints);

    		constraints.gridx = 2;
    		constraints.gridy = 0;
    		contentPane.add(ircSettingsPanel, constraints);

    		constraints.gridx = 2;
    		constraints.gridy = 2;
    		constraints.insets.top = 10;
    		clientSettingsPanel.add(startBtn, constraints);

    		typeStandard.addChangeListener(new ChangeListener(){
    			public void stateChanged(ChangeEvent e)
    			{
    				if(!typeStandard.isSelected() && !typeCustom.isSelected())
    				{
    					typeStandard.setSelected(true);
    				}
    				if(typeStandard.isSelected())
	    			{
	    				stdRes.setEnabled(true);
	    				typeCustom.setSelected(false);
	    			} else
	    			{
	    				stdRes.setEnabled(false);
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
    					stdRes.disable();
    				} else
    				{
    					xField.setEditable(false);
    					yField.setEditable(false);
    					xField.disable();
    					yField.disable();
    					stdRes.enable();
    				}
    			}
    		});
    		xField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				try{
    					if(Integer.parseInt(xField.getText()) < 800)
	    				{
	    					xField.setText("800");
	    				}
    				} catch(NumberFormatException NFExcep)
    				{
    					xField.setText("800");
    				}
    			}
    		});
    		yField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				try{
    					if(Integer.parseInt(yField.getText()) < 600)
    					{
    						yField.setText("600");
    					}
    				} catch(NumberFormatException NFExcep)
    				{
    					yField.setText("600");
    				}
    			}
    		});
    		ircDefNickField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				if(!ircDefNickField.getText().trim().equals(""))
    				{
    					ircDefNick = ircDefNickField.getText().trim();
    				}
    				if(ircAltNickField.getText().trim().equals(""))
    				{
    					ircAltNick = ircDefNickField.getText().trim() + "|C";
    				}
    			}
    		});
    		ircAltNickField.addFocusListener(new FocusListener(){
    			public void focusGained(FocusEvent e){}
    			public void focusLost(FocusEvent e)
    			{
    				if(!ircAltNickField.getText().trim().equals(""))
    				{
    					if(ircDefNickField.getText().trim().equals(""))
    					{
    						ircDefNickField.setText(ircAltNickField.getText().trim());
    						ircDefNick = ircDefNickField.getText();
    						ircAltNickField.setText(ircDefNick + "|C");
    						return;
    					}
    					ircAltNick = ircAltNickField.getText().trim();
    					return;
    				}
    				if(!ircDefNickField.getText().trim().equals(""))
    				{
    					ircAltNick = ircDefNickField.getText().trim()+ "|C";
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
    				configFrame.dispose();
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