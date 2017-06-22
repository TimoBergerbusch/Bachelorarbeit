package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import sun.awt.windows.ThemeReader;

public class Logger {
	
	/**
	 * a {@link FileWriter FileWriter} for test and output purposes.
	 */
	private FileWriter fw;
	
	/**
	 * a {@link BufferedWriter BufferedWriter} for test and output purposes using the {@link FileWriter FileWriter fw}
	 */
	private BufferedWriter bw;
	
	/**
	 * the default path of a file to print into
	 */
	private String path="/home/timo/Downloads/GeoNonTerm-ausgabe.txt";
	
	
	/**
	 * the default constructor calling the {@link #init()}-method
	 */
	protected Logger(){
		init();
	}

	/**
	 * Initialization of the {@link FileWriter FileWriter fw} and the {@link BufferedWriter BufferedWriter bw}
	 */
	private void init() {
		
			try {
				fw = new FileWriter(path);
				bw = new BufferedWriter(fw);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to load Logger File");
			}		
	}
	
	/**
	 * printing into the file stored in {@link #path} and jumping into a new line.
	 * If an {@link IOException} occurs the method will <u>not</u> be executed.
	 * 
	 * @param toPrint the String that should be printed
	 */
	public void writeln(String toPrint){
		try {
			bw.write(toPrint);
			bw.newLine();
		} catch (IOException e) {
			//Ignored
		}
	}
	
	/**
	 * Jumping into a new line.
	 * If an {@link IOException} occurs the method will <u>not</u> be executed.
	 */
	public void newLine(){
		try {
			bw.newLine();
		} catch (IOException e) {
			//ignored
		}
	}
	
	/**
	 * closing the current output stream.
	 * If an {@link IOException} occurs the method will <u>not</u> be executed.
	 */
	public void close(){
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			//Ignored
		}
	}
	
}
