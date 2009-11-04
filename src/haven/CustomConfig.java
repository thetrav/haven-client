 /**
 * @(#)CustomConfig.java
 *
 *
 * @author 
 * @version 1.00 2009/10/19
 */

package haven;
import java.io.*;

public class CustomConfig {
	
	public static Coord windowSize = new Coord(1024, 768);
	public static int sfxVol = 100;
	public static String ircServerAddress = "irc.synirc.net";
	public static String ircChannelList = "#Haven";
	public static int wdgtID = 1000;
	
    public CustomConfig() {
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
    	}catch (FileNotFoundException fileNotFound){
    		saveSettings();
    	}catch (IOException IOExcep){
    	}
    }
    public static double getSFXVolume()
    {
    	return (double)sfxVol/100;
    }
    public static void saveSettings()
    {
    	try{
    			BufferedWriter writer = new BufferedWriter(new FileWriter("config.cfg"));
    			writer.write(windowSize.x + " " + windowSize.y + "\n");
    			writer.write(Integer.toString(sfxVol) + "\n");
    			writer.write(ircServerAddress + "\n");
    			writer.write(ircChannelList + "\n");
    			writer.close();
    	}catch(IOException e){}
    }
}