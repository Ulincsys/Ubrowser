package ulincsys.pythonics.io;

import static ulincsys.pythonics.Pythonics.Str;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/** Pythonic IO formatter for the Ulincsys Pythonics library.
 * Requires a wrapper function to operate the read and write functionality.
 * Attempts to extract a filename and various format options from a
 * given Object[] array, and assumes standard IO and format options
 * otherwise.
 * @apiNote Line separator set with "sep=string". Default "space"
 * @apiNote Line end set with "end=string". Default "\n"
 * @apiNote IO file set with "file=filename". Default, system.in/out
 * @apiNote Buffer flush set with "flush=boolean". Default true
 * @apiNote File append set with "append=boolean". Default true
 * @author Ulincsys
 */
public class IOFormatter {
	private String end;
	private String separator;
	private String filename;
	private String escapeSequence;
	private BufferedWriter fileOut;
	private BufferedReader fileIn;
	private File file;
	private boolean flushed;
	private boolean append;
	private boolean standard;
	private Object[] outBuffer;
	private ArrayList<String> inBuffer;
	

	/** Default IOFormatter, initialized for standard out.
	 */
	public IOFormatter() {
		init();
	}
	
	/** Creates a populated IOFormatter Object.
	 * @param end A string representing the endline character sequence.
	 * @param separator A string representing the Object separation character sequence.
	 * @param filename The filename to write or read, pass null for system.in/out.
	 * @param flushed A boolean value representing whether or not the writer is flushed.
	 * @param append A boolean value representing whether or not the writer appends to the output file.
	 * NOTE: Setting this to false will cause the writer to overwrite the contents of the given file.
	 */
	public IOFormatter(String end, String separator, String filename, boolean flushed, boolean append) {
		init();
		this.separator = separator;
		this.filename = filename;
		this.flushed = flushed;
		this.append = append;
		this.end = end;
		
		standard = makeFile();
	}
	
	/** IOFormatter initialized with Object[] array. Will search the last 5
	 * array elements for format options.
	 */
	public IOFormatter(Object[] parse) {
		init();
		int limit = (parse.length > 5) ? parse.length - 5 : 0;
		
		for(int i = (parse.length - 1); i >= limit; --i) {
			if(Str(parse[i]).startsWith("sep=")) {
				separator = Str(parse[i]).replace("sep=", "");
				parse[i] = null;
			} else if(Str(parse[i]).startsWith("end=")) {
				end = Str(parse[i]).replace("end=", "");
				parse[i] = null;
			} else if(Str(parse[i]).startsWith("file=")) {
				filename = Str(parse[i]).replace("file=", "");
				parse[i] = null;
			} else if(Str(parse[i]).startsWith("flush=")) {
				flushed = Str(parse[i]).contains("false") ? false : true;
				parse[i] = null;
			} else if(Str(parse[i]).startsWith("append=")) {
				append = Str(parse[i]).contains("false") ? false : true;
				parse[i] = null;
			}
		}
		
		outBuffer = parse;
		
		if(filename != null) {
			makeFile();
		}
	}
	
	public static IOFormatter valueOf(Object... parse) {
		return new IOFormatter(parse);
	}
	
	// initializer, calls makeStandard and assumes all default values
	private void init() {
		inBuffer = new ArrayList<String>();
		separator = " ";
		end = "\n";
		append = true;
		flushed = true;
		escapeSequence = "_EOF";
		makeStandard();
	}
	
	// attempts to initialize the BufferedWriter to a file, else calls makeStandard
	private boolean makeFile() {
		try {
			file = new File(filename);
			if(!file.exists()) {
				file.createNewFile();
			}
			fileOut = new BufferedWriter(new FileWriter(file, append));
			fileIn = new BufferedReader(new FileReader(file));
			standard = false;
			return true;
		} catch (Exception e) {
			makeStandard();
			return false;
		}
	}
	
	// initializes the buffered IO streams for this print formatter to standard IO.
	private void makeStandard() {
		standard = true;
		filename = null;
		fileOut = new BufferedWriter(new OutputStreamWriter(System.out));
		fileIn = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/** Writes the give String to the bufferedWriter initialized in this IOFormatter.
	 * Does NOT insert a newline afterwards.
	 * @param out String to write in the output stream.
	 */
	public void write(String out) {
		try {
			fileOut.write(out);
			tryFlush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the give String to the bufferedWriter initialized in this IOFormatter,
	 * and inserts a newline afterwards.
	 * @param out String to write in the output stream.
	 */
	public void writeLine(String out) {
		try {
			fileOut.write(out + "\n");
			tryFlush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLines(ArrayList<String> out) {
		boolean didFlush = flushed;
		flushed = false;
		
		for(String line : out) {
			writeLine(line);
		}
		
		flushed = didFlush;
		tryFlush();
	}
	
	/** Reads the next line from the bufferedReader initialized in this IOFormatter.
	 * Returns null if there is no next line, or if the read failed for another reason.
	 */
	public String read() {
		try {
			return fileIn.readLine();
		} catch(Exception e) {
			return null;
		}
	}
	
	/** Reads all remaining lines in the bufferedReader initialized in this IOFormatter.
	 * If read() or readLines() has already been called, any remaining lines on the input,
	 * if any, are added to the existing inBuffer
	 * @apiNote If the bufferedReader initialized in this IOFormatter is currently reading
	 * from System.in, it will continually prompt for input until the user enters the escape
	 * sequence, "_EOF" by default.
	 * @apiNote The the reader will also stop if the escape sequence is encountered in a file.
	 * @apiNote Any line containing the escape sequence will NOT be recorded in the inBuffer.
	 */
	public ArrayList<String> readLines() {
		if(standard && escapeSequence.contains("_EOF")) {
			writeLine("Warning: You are reading continuously from standard in!\n"
					+ "To break out of continuous reading, enter the escape sequence "
					+ "'_EOF'. To disable this warning, use setEscapeSequence() to "
					+ "change the default escapeSequence character sequence.");
		}
		String buffer;
		try {
			Iterator<String> lines = fileIn.lines().iterator();
			while(lines.hasNext()) {
				buffer = lines.next();
				if(buffer.contains(escapeSequence)) {
					break;
				}
				inBuffer.add(buffer);
			}
		} catch(Exception e) {
			return null;
		}
		return inBuffer;
	}
	
	/** Closes open file streams and reopens the bufferedReader/Writers with the
	 * existing filename.
	 */
	public void reset() {
		try {
			inBuffer = new ArrayList<String>();
			fileIn.close();
			fileOut.close();
			makeFile();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Attempts to flush the bufferedWriter initialized in this IOFormatter.
	 * will flush if the 'flushed' option is set to true (default).
	 */
	public void tryFlush() {
		if(flushed) {
			try {
				fileOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Will flush the bufferedWriter initialized in this IOFormatter regardless of the
	 * 'flushed' option.
	 */
	public void flush() {
		try {
			fileOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** will reset all internal data, close any open files and reinitialize the
	 * IO to standard in/out.
	 */
	public void close() {
		try {
			fileIn.close();
			fileOut.close();
			init();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getFilename() {
		return filename;
	}
	
	/** Accepts a filename in the form of a string,
	 * and will attempt to create the given file if not found.
	 * Given file will be used for IO operations with this IOFormatter.
	 * Pass null to set output to system.in/out
	 */
	public void setFilename(String filename) {
		this.filename = filename;
		makeFile();
	}
	
	public boolean isFlushed() {
		return flushed;
	}

	public void setFlushed(boolean flushed) {
		this.flushed = flushed;
	}

	public Object[] getOutBuffer() {
		return outBuffer;
	}

	public ArrayList<String> getInBuffer() {
		return inBuffer;
	}

	public String getEscapeSequence() {
		return escapeSequence;
	}

	public void setEscapeSequence(String escapeSequence) {
		this.escapeSequence = escapeSequence;
	}
	
	public boolean isStandard() {
		return standard;
	}

}











