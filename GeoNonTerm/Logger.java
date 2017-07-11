package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.sat4j.core.VecInt;

public class Logger {

    /**
     * a {@link FileWriter FileWriter} for test and output purposes.
     */
    private FileWriter fw;

    /**
     * a {@link BufferedWriter BufferedWriter} for test and output purposes
     * using the {@link FileWriter FileWriter fw}
     */
    private BufferedWriter bw;

    /**
     * the default path of a file to print into
     */
    private String path = "/home/timo/Downloads/GeoNonTerm-ausgabe.txt";

    /**
     * the default constructor calling the {@link #init()}-method
     */
    protected Logger() {
	init();
    }

    /**
     * Initialization of the {@link FileWriter FileWriter fw} and the
     * {@link BufferedWriter BufferedWriter bw}
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
     * printing into the file stored in {@link #path} and jumping into a new
     * line. If an {@link IOException} occurs the method will <u>not</u> be
     * executed.
     * 
     * @param toPrint
     *            the String that should be printed
     */
    public void writeln(String toPrint) {
	try {
	    bw.write(toPrint);
	    bw.newLine();
	} catch (IOException e) {
	    // Ignored
	}
    }

    /**
     * printing an object into the file stored in {@link #path} using
     * {@link #writeln(String)} using it's {@link #toString()} method.
     * 
     * @param o
     *            the {@link Object}, which should be printed
     */
    public void writeln(Object o) {
	this.writeln(o.toString());
    }

    /**
     * Jumping into a new line. If an {@link IOException} occurs the method will
     * <u>not</u> be executed.
     */
    public void newLine() {
	try {
	    bw.newLine();
	} catch (IOException e) {
	    // ignored
	}
    }

    /**
     * closing the current output stream. If an {@link IOException} occurs the
     * method will <u>not</u> be executed.
     */
    public void close() {
	try {
	    bw.close();
	    fw.close();
	} catch (IOException e) {
	    // Ignored
	}
    }

    /**
     * writes a special pattern to mark the start of output of a special class,
     * like {@link Stem} or {@link Loop}
     * 
     * @param name
     *            the name of the class
     * 
     * @see #endClassOutput(String)
     */
    public void startClassOutput(String name) {
	this.writeln("########################");
	this.writeln("######### " + name + " #########");
	this.writeln("########################");
    }

    /**
     * writes a special pattern to mark the end of output of a special class,
     * like {@link Stem} or {@link Loop}
     * 
     * @param name
     *            the name of the class
     */
    public void endClassOutput(String name) {
	this.writeln("------------------------");
	this.writeln("--------- " + name + " ---------");
	this.writeln("------------------------");
    }

    /**
     * prints an array a using the form: {a_1, a_2, ..., a_n} <br>
     * using {@link #arrayToString(Object[])}
     * 
     * @param array
     *            the array that should be printed
     */
    public <T> void printArray(T[] array) {
	this.writeln(this.arrayToString(array));
    }

    /**
     * converts an array s into a String of the form: {a_1,a_2, ...,a_n}
     * 
     * @param array
     *            the array that should be converted
     * @return the array in the presented form
     */
    public <T> String arrayToString(T[] array) {
	StringBuilder sb = new StringBuilder();

	sb.append("{ ");
	for (int i = 0; i < array.length; i++) {
	    sb.append(array[i].toString());
	    if (i < array.length - 1)
		sb.append(", ");
	}
	sb.append("}");

	return sb.toString();
    }

    public String[] VecIntToArray(VecInt vec) {
	String[] items = new String[vec.size()];
	for (int i = 0; i < items.length; i++) {
	    items[i] = vec.get(i) + "";
	}
	return items;
    }
}
