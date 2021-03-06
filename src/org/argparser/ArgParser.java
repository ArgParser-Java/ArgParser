/*
 * Copyright 2004 by John E. Lloyd
 * Copyright 2011-2013 by Andreas Draeger, Clemens Wrzodek and Florian Mittag
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.argparser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * ArgParser is used to parse the command line arguments for a java
 * application program. It provides a compact way to specify options and match
 * them against command line arguments, with support for
 * <a href=#rangespec>range checking</a>,
 * <a href=#multipleOptionNames>multiple option names</a> (aliases),
 * <a href=#singleWordOptions>single word options</a>,
 * <a href=#multipleOptionValues>multiple values associated with an option</a>,
 * <a href=#multipleOptionInvocation>multiple option invocation</a>,
 * <a href=#helpInfo>generating help information</a>,
 * <a href=#customArgParsing>custom argument parsing</a>, and
 * <a href=#argsFromAFile>reading arguments from a file</a>. The
 * last feature is particularly useful and makes it
 * easy to create ad-hoc configuration files for an application.
 * 
 * <h3><a name="example">Basic Example</a></h3>
 * 
 * <p>
 * Here is a simple example in which an application has three command line
 * options: {@code -theta} (followed by a floating point value), {@code -file}
 * (followed by a string value), and {@code -debug}, which causes a boolean
 * value to be set.
 * 
 * <pre>
 * 
 * static public void main(String[] args) {
 *   // create holder objects for storing results ...
 *   
 *   DoubleHolder theta = new DoubleHolder();
 *   StringHolder fileName = new StringHolder();
 *   BooleanHolder debug = new BooleanHolder();
 *   
 *   // create the parser and specify the allowed options ...
 *   
 *   ArgParser parser = new ArgParser(&quot;java argparser.SimpleExample&quot;);
 *   parser.addOption(&quot;-theta %f #theta value (in degrees)&quot;, theta);
 *   parser.addOption(&quot;-file %s #name of the operating file&quot;, fileName);
 *   parser.addOption(&quot;-debug %v #enables display of debugging info&quot;, debug);
 *   
 *   // match the arguments ...
 *   
 *   parser.matchAllArgs(args);
 *   
 *   // and print out the values
 *   
 *   System.out.println(&quot;theta=&quot; + theta.value);
 *   System.out.println(&quot;fileName=&quot; + fileName.value);
 *   System.out.println(&quot;debug=&quot; + debug.value);
 * }
 * </pre>
 * <p>
 * A command line specifying all three options might look like this:
 * 
 * <pre>
 * java argparser.SimpleExample -theta 7.8 -debug -file /ai/lloyd/bar
 * </pre>
 * 
 * <p>
 * The application creates an instance of ArgParser and then adds descriptions
 * of the allowed options using {@link #addOption addOption}. The method
 * {@link #matchAllArgs(String[]) matchAllArgs} is then used to match these
 * options against the command line arguments. Values associated with each
 * option are returned in the {@code value} field of special ``holder'' classes
 * (e.g., {@link argparser.DoubleHolder DoubleHolder},
 * {@link argparser.StringHolder StringHolder}, etc.).
 * 
 * <p>
 * The first argument to {@link #addOption addOption} is a string that specifies
 * (1) the option's name, (2) a conversion code for its associated value (e.g.,
 * {@code %f} for floating point, {@code %s} for a string, {@code %v} for a
 * boolean flag), and (3) an optional description (following the {@code #}
 * character) which is used for generating help messages. The second argument is
 * the holder object through which the value is returned. This may be either a
 * type-specific object (such as {@link argparser.DoubleHolder DoubleHolder} or
 * {@link argparser.StringHolder
 * StringHolder}), an array of the appropriate type, or <a
 * href=#multipleOptionInvocation> an instance of {@link Vector}</a>.
 * 
 * <p>
 * By default, arguments that don't match the specified options, are <a
 * href=#rangespec>out of range</a>, or are otherwise formatted incorrectly,
 * will cause {@code matchAllArgs} to print a message and exit the program.
 * Alternatively, an application can use {@link #matchAllArgs(String[],int,int)
 * matchAllArgs(args,idx,exitFlags)} to obtain an array of unmatched arguments
 * which can then be <a href=#customArgParsing>processed separately</a>
 * 
 * <h3><a name="rangespec">Range Specification</a></h3>
 * 
 * The values associated with options can also be given range specifications. A
 * range specification appears in curly braces immediately following the
 * conversion code. In the code fragment below, we show how to specify an option
 * {@code -name} that expects to be provided with one of three string values (
 * {@code john}, {@code mary}, or {@code jane}), an option {@code -index} that
 * expects to be supplied with a integer value in the range 1 to 256, an option
 * {@code -size} that expects to be supplied with integer values of either 1, 2,
 * 4, 8, or 16, and an option {@code -foo} that expects to be supplied with
 * floating point values in the ranges -99 < foo <= -50, or 50 <= foo < 99.
 * 
 * <pre>
 * StringHolder name = new StringHolder();
 * IntHolder index = new IntHolder();
 * IntHolder size = new IntHolder();
 * DoubleHolder foo = new DoubleHolder();
 * 
 * parser.addOption(&quot;-name %s {john,mary,jane}&quot;, name);
 * parser.addOption(&quot;-index %d {[1,256]}&quot;, index);
 * parser.addOption(&quot;-size %d {1,2,4,8,16}&quot;, size);
 * parser.addOption(&quot;-foo %f {(-99,-50],[50,99)}&quot;, foo);
 * </pre>
 * 
 * If an argument value does not lie within a specified range, an error is
 * generated.
 * 
 * <h3><a name="multipleOptionNames">Multiple Option Names</a></h3>
 * 
 * An option may be given several names, or aliases, in the form of a comma
 * seperated list:
 * 
 * <pre>
 * parser.addOption(&quot;-v,--verbose %v #print lots of info&quot;);
 * parser.addOption(&quot;-of,-outfile,-outputFile %s #output file&quot;);
 * </pre>
 * 
 * <h3><a name="singleWordOptions">Single Word Options</a></h3>
 * 
 * Normally, options are assumed to be "multi-word", meaning that any associated
 * value must follow the option as a separate argument string. For example,
 * 
 * <pre>
 * parser.addOption(&quot;-file %s #file name&quot;);
 * </pre>
 * 
 * will cause the parser to look for two strings in the argument list of the
 * form
 * 
 * <pre>
 *    -file someFileName
 * </pre>
 * 
 * However, if there is no white space separting the option's name from it's
 * conversion code, then values associated with that option will be assumed to
 * be part of the same argument string as the option itself. For example,
 * 
 * <pre>
 * parser.addOption(&quot;-file=%s #file name&quot;);
 * </pre>
 * 
 * will cause the parser to look for a single string in the argument list of the
 * form
 * 
 * <pre>
 *    -file=someFileName
 * </pre>
 * 
 * Such an option is called a "single word" option.
 * 
 * <p>
 * In cases where an option has multiple names, then this single word behavior
 * is invoked if there is no white space between the last indicated name and the
 * conversion code. However, previous names in the list will still be given
 * multi-word behavior if there is white space between the name and the
 * following comma. For example,
 * 
 * <pre>
 * parser.addOption(&quot;-nb=,-number ,-n%d #number of blocks&quot;);
 * </pre>
 * 
 * will cause the parser to look for one, two, and one word constructions of the
 * forms
 * 
 * <pre>
 *    -nb=N
 *    -number N
 *    -nN
 * </pre>
 * 
 * <h3><a name="multipleOptionValues">Multiple Option Values</a></h3>
 * 
 * If may be useful for an option to be followed by several values. For
 * instance, we might have an option {@code -velocity} which should be followed
 * by three numbers denoting the x, y, and z components of a velocity vector. We
 * can require multiple values for an option by placing a <i>multiplier</i>
 * specification, of the form {@code X}N, where N is an integer, after the
 * conversion code (or range specification, if present). For example,
 * 
 * <pre>
 * double[] pos = new double[3];
 * 
 * addOption(&quot;-position %fX3 #position of the object&quot;, pos);
 * </pre>
 * 
 * will cause the parser to look for
 * 
 * <pre>
 *    -position xx yy zz
 * </pre>
 * 
 * in the argument list, where {@code xx}, {@code yy}, and {@code zz} are
 * numbers. The values are stored in the array {@code pos}.
 * 
 * Options requiring multiple values must use arrays to return their values, and
 * cannot be used in single word format.
 * 
 * <h3><a name="multipleOptionInvocation">Multiple Option Invocation</a></h3>
 * 
 * Normally, if an option appears twice in the command list, the value
 * associated with the second instance simply overwrites the value associated
 * with the first instance.
 * 
 * However, the application can instead arrange for the storage of <i>all</i>
 * values associated with multiple option invocation, by supplying a instance of
 * {@link Vector} to serve as the value holder. Then every time the option
 * appears in the argument list, the parser will create a value holder of
 * appropriate type, set it to the current value, and store the holder in the
 * vector. For example, the construction
 * 
 * <pre>
 * Vector vec = new Vector(10);
 * 
 * parser.addOption(&quot;-foo %f&quot;, vec);
 * parser.matchAllArgs(args);
 * </pre>
 * 
 * when supplied with an argument list that contains
 * 
 * <pre>
 *    -foo 1.2 -foo 1000 -foo -78
 * </pre>
 * 
 * will create three instances of {@link argparser.DoubleHolder DoubleHolder},
 * initialized to {@code 1.2}, {@code 1000}, and {@code -78}, and store them in
 * {@code vec}.
 * 
 * <h3><a name="helpInfo">Generating help information</a></h3>
 * 
 * ArgParser automatically generates help information for the options, and this
 * information may be printed in response to a <i>help</i> option, or may be
 * queried by the application using {@link #getHelpMessage getHelpMessage}. The
 * information for each option consists of the option's name(s), it's required
 * value(s), and an application-supplied description. Value information is
 * generated automaticlly from the conversion code, range, and multiplier
 * specifications (although this can be overriden, as <a
 * href=#valueInfo>described below</a>). The application-supplied description is
 * whatever appears in the specification string after the optional {@code #}
 * character. The string returned by {@link #getHelpMessage getHelpMessage} for
 * the <a href=#example>first example above</a> would be
 * 
 * <pre>
 * Usage: java argparser.SimpleExample
 * Options include:
 * 
 * --help,-?                displays help information
 * -theta &lt;float&gt;          theta value (in degrees)
 * -file &lt;string&gt;          name of the operating file
 * -debug                  enables display of debugging info
 * </pre>
 * 
 * The options {@code --help} and {@code -?} are including in the parser by
 * default as help options, and they automatically cause the help message to be
 * printed. To exclude these options, one should use the constructor
 * {@link #ArgParser(String,boolean)
 * ArgParser(synopsis,false)}. Help options can also be specified by the
 * application using {@link #addOption addOption} and the conversion code
 * {@code %h}. Help options can be disabled using {@link #setHelpOptionsEnabled
 * setHelpOptionsEnabled(false)}.
 * 
 * <p>
 * <a name=valueInfo> A description of the required values for an option can be
 * specified explicitly by placing a second {@code #} character in the
 * specification string. Everything between the first and second {@code #}
 * characters then becomes the value description, and everything after the
 * second {@code #} character becomes the option description. For example, if
 * the {@code -theta} option above was specified with
 * 
 * <pre>
 * parser.addOption(&quot;-theta %f #NUMBER#theta value (in degrees)&quot;, theta);
 * </pre>
 * 
 * instead of
 * 
 * <pre>
 * parser.addOption(&quot;-theta %f #theta value (in degrees)&quot;, theta);
 * </pre>
 * 
 * then the corresponding entry in the help message would look like
 * 
 * <pre>
 * -theta NUMBER          theta value (in degrees)
 * </pre>
 * 
 * <h3><a name="customArgParsing">Custom Argument Parsing</a></h3>
 * 
 * An application may find it necessary to handle arguments that don't fit into
 * the framework of this class. There are a couple of ways to do this.
 * 
 * <p>
 * First, the method {@link #matchAllArgs(String[],int,int)
 * matchAllArgs(args,idx,exitFlags)} returns an array of all unmatched
 * arguments, which can then be handled specially:
 * 
 * <pre>
 *    String[] unmatched =
 *       parser.matchAllArgs (args, 0, parser.EXIT_ON_ERROR);
 *    for (int i = 0; i < unmatched.length; i++)
 *     { ... handle unmatched arguments ...
 *     }
 * </pre>
 * 
 * For instance, this would be useful for an applicatoon that accepts an
 * arbitrary number of input file names. The options can be parsed using
 * {@code matchAllArgs}, and the remaining unmatched arguments give the file
 * names.
 * 
 * <p>
 * If we need more control over the parsing, we can parse arguments one at a
 * time using {@link #matchArg matchArg}:
 * 
 * <pre>
 *    int idx = 0;
 *    while (idx < args.length)
 *     { try
 *        { idx = parser.matchArg (args, idx);
 *          if (parser.getUnmatchedArgument() != null)
 *           {
 *             ... handle this unmatched argument ourselves ...
 *           }
 *        }
 *       catch (ArgParserException e) 
 *        { // malformed or erroneous argument
 *          parser.printErrorAndExit (e.getMessage());
 *        }
 *     }
 * </pre>
 * 
 * {@link #matchArg matchArg(args,idx)} matches one option at location
 * {@code idx} in the argument list, and then returns the location value that
 * should be used for the next match. If an argument does not match any option,
 * {@link #getUnmatchedArgument getUnmatchedArgument} will return a copy of the
 * unmatched argument.
 * 
 * <h3><a name="argsFromAFile">Reading Arguments From a File</a></h3>
 * 
 * The method {@link #prependArgs prependArgs} can be used to automatically read
 * in a set of arguments from a file and prepend them onto an existing argument
 * list. Argument words correspond to white-space-delimited strings, and the
 * file may contain the comment character {@code #} (which comments out
 * everything to the end of the current line). A typical usage looks like this:
 * 
 * <pre>
 *    ... create parser and add options ...
 * 
 *    args = parser.prependArgs (new File(".configFile"), args);
 * 
 *    parser.matchAllArgs (args);
 * </pre>
 * 
 * This makes it easy to generate simple configuration files for an application.
 */
public class ArgParser {
  
  /**
	 * 
	 */
  private List<Record> matchList;
  //	int tabSpacing = 8;
  /**
   * 
   */
  private String synopsisString;
  /**
	 * 
	 */
  boolean helpOptionsEnabled = true;
  /**
	 * 
	 */
  private Record defaultHelpOption = null;
  /**
	 * 
	 */
  private Record firstHelpOption = null;
  /**
	 * 
	 */
  private PrintStream printStream = System.out;
  /**
	 * 
	 */
  private int helpIndent = 6;
  /**
	 * 
	 */
  private int consoleColumns = 80;
  /**
	 * 
	 */
  private String errMsg = null;
  /**
	 * 
	 */
  private String unmatchedArg = null;
  
  /**
   * 
   */
  private static String validConversionCodes = "iodxcbfsvh";
  
  /**
   * Indicates that the program should exit with an appropriate message in the
   * event of an erroneous or malformed argument.
   */
  public static int EXIT_ON_ERROR = 1;
  
  /**
   * Indicates that the program should exit with an appropriate message in the
   * event of an unmatched argument.
   */
  public static int EXIT_ON_UNMATCHED = 2;
  
  /**
   * Returns a string containing the valid conversion codes. These are the
   * characters which may follow the {@code %} character in the
   * specification string of {@link #addOption addOption}.
   * 
   * @return Valid conversion codes
   * @see #addOption
   */
  public static String getValidConversionCodes() {
    return validConversionCodes;
  }
  
  /**
	 * 
	 */
  static class NameDesc {
    /**
		 * 
		 */
    private String name;
    /**
     * oneWord implies that any value associated with option is concatenated
     * onto the argument string itself
     */
    private boolean oneWord;
    /**
		 * 
		 */
    private NameDesc next = null;
    
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
    
    /**
     * @return the oneWord
     */
    public boolean isOneWord() {
      return oneWord;
    }
    
    /**
     * @return the next
     */
    public NameDesc getNext() {
      return next;
    }
  }
  
  /**
	 * 
	 */
  static class RangePnt {
    /**
		  * 
		  */
    double dval = 0;
    /**
	    * 
	    */
    long lval = 0;
    /**
	    * 
	    */
    String sval = null;
    /**
	    * 
	    */
    boolean bval = true;
    /**
	    * 
	    */
    boolean closed = true;
    
    /**
     * 
     * @param s
     * @param closed
     */
    public RangePnt(String s, boolean closed) {
      sval = s;
      this.closed = closed;
    }
    
    /**
     * 
     * @param d
     * @param closed
     */
    public RangePnt(double d, boolean closed) {
      dval = d;
      this.closed = closed;
    }
    
    /**
     * 
     * @param l
     * @param closed
     */
    public RangePnt(long l, boolean closed) {
      lval = l;
      this.closed = closed;
    }
    
    /**
     * 
     * @param b
     * @param closed
     */
    public RangePnt(boolean b, boolean closed) {
      bval = b;
      this.closed = closed;
    }
    
    /**
     * 
     * @param scanner
     * @param type
     * @throws IllegalArgumentException
     */
    public RangePnt(StringScanner scanner, int type)
      throws IllegalArgumentException {
      String typeName = null;
      try {
        switch (type) {
          case Record.CHAR: {
            typeName = "character";
            lval = scanner.scanChar();
            break;
          }
          case Record.INT:
          case Record.LONG: {
            typeName = "integer";
            lval = scanner.scanInt();
            break;
          }
          case Record.FLOAT:
          case Record.DOUBLE: {
            typeName = "float";
            dval = scanner.scanDouble();
            break;
          }
          case Record.STRING: {
            typeName = "string";
            sval = scanner.scanString();
            break;
          }
          case Record.BOOLEAN: {
            typeName = "boolean";
            bval = scanner.scanBoolean();
            break;
          }
        }
      } catch (StringScanException e) {
        throw new IllegalArgumentException("Malformed " + typeName + " '"
            + scanner.substring(scanner.getIndex(), e.getFailIndex() + 1)
            + "' in range spec");
      }
      //	      this.closed = closed;
    }
    
    /**
     * 
     * @param closed
     */
    public void setClosed(boolean closed) {
      this.closed = closed;
    }
    
    /**
     * 
     * @return
     */
    public boolean getClosed() {
      return closed;
    }
    
    /**
     * 
     * @param d
     * @return
     */
    public int compareTo(double d) {
      if (dval < d) { return -1; }
      return (d == dval) ? 0 : 1;
    }
    
    /**
     * 
     * @param l
     * @return
     */
    public int compareTo(long l) {
      if (lval < l) {
        return -1;
      } else if (l == lval) {
        return 0;
      } else {
        return 1;
      }
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public int compareTo(String s) {
      return sval.compareTo(s);
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public int compareToIgnoreCase(String s) {
      return sval.toUpperCase().compareTo(s.toUpperCase());
    }
    
    /**
     * 
     * @param b
     * @return
     */
    public int compareTo(boolean b) {
      return (b == bval) ? 0 : 1;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "{ dval=" + dval + ", lval=" + lval + ", sval=" + sval + ", bval="
          + bval + ", closed=" + closed + "}";
    }
  }
  
  /**
	 * 
	 */
  class RangeAtom {
    /**
		 * 
		 */
    private RangePnt low = null;
    
    /**
     * @return the low
     */
    public RangePnt getLow() {
      return low;
    }
    
    /**
     * @return the high
     */
    public RangePnt getHigh() {
      return high;
    }
    
    /**
     * @return the next
     */
    public RangeAtom getNext() {
      return next;
    }
    
    /**
	    * 
	    */
    private RangePnt high = null;
    /**
	    * 
	    */
    private RangeAtom next = null;
    
    /**
     * 
     * @param p0
     * @param p1
     * @param type
     * @throws IllegalArgumentException
     */
    public RangeAtom(RangePnt p0, RangePnt p1, int type)
      throws IllegalArgumentException {
      int cmp = 0;
      switch (type) {
        case Record.CHAR:
        case Record.INT:
        case Record.LONG: {
          cmp = p0.compareTo(p1.lval);
          break;
        }
        case Record.FLOAT:
        case Record.DOUBLE: {
          cmp = p0.compareTo(p1.dval);
          break;
        }
        case Record.STRING: {
          cmp = p0.compareTo(p1.sval);
          break;
        }
      }
      if (cmp > 0) { // then switch high and low
        low = p1;
        high = p0;
      } else {
        low = p0;
        high = p1;
      }
    }
    
    /**
     * 
     * @param p0
     * @throws IllegalArgumentException
     */
    public RangeAtom(RangePnt p0) throws IllegalArgumentException {
      low = p0;
    }
    
    /**
     * 
     * @param d
     * @return
     */
    public boolean match(double d) {
      int lc = low.compareTo(d);
      if (high != null) {
        int hc = high.compareTo(d);
        return (lc * hc < 0 || (low.closed && lc == 0) || (high.closed && hc == 0));
      } else {
        return lc == 0;
      }
    }
    
    /**
     * 
     * @param l
     * @return
     */
    public boolean match(long l) {
      int lc = low.compareTo(l);
      if (high != null) {
        int hc = high.compareTo(l);
        return (lc * hc < 0 || (low.closed && lc == 0) || (high.closed && hc == 0));
      } else {
        return lc == 0;
      }
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public boolean match(String s) {
      int lc = low.compareTo(s);
      if (high != null) {
        int hc = high.compareTo(s);
        return (lc * hc < 0 || (low.closed && lc == 0) || (high.closed && hc == 0));
      } else {
        return lc == 0;
      }
    }
    
    public boolean matchIgnoreCase(String s) {
      int lc = low.compareToIgnoreCase(s);
      if (high != null) {
        int hc = high.compareToIgnoreCase(s);
        return (lc * hc < 0 || (low.closed && lc == 0) || (high.closed && hc == 0));
      } else {
        return lc == 0;
      }
    }
    
    /**
     * 
     * @param b
     * @return
     */
    public boolean match(boolean b) {
      return low.compareTo(b) == 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "low=" + (low == null ? "null" : low.toString()) + ", high="
          + (high == null ? "null" : high.toString());
    }
  }
  
  /**
	 * 
	 */
  class Record {
    /**
		  * 
		  */
    private NameDesc nameList;
    
    /**
     * Delimiter for grouping options
     */
    public static final int DELIM = -1;
    /**
	   * 
	   */
    public static final int NOTYPE = 0;
    /**
	    * 
	    */
    public static final int BOOLEAN = 1;
    /**
	    * 
	    */
    public static final int CHAR = 2;
    /**
	    * 
	    */
    public static final int INT = 3;
    /**
	    * 
	    */
    public static final int LONG = 4;
    /**
	    * 
	    */
    public static final int FLOAT = 5;
    /**
	    * 
	    */
    public static final int DOUBLE = 6;
    /**
	    * 
	    */
    public static final int STRING = 7;
    /**
	    * 
	    */
    private int type;
    /**
	    * 
	    */
    private int numValues;
    /**
	    * 
	    */
    private String helpMsg = null;
    /**
	    * 
	    */
    private String valueDesc = null;
    /**
	    * 
	    */
    private String rangeDesc = null;
    
    /**
     * @return the numValues
     */
    public int getNumValues() {
      return numValues;
    }
    
    /**
     * @return the helpMsg
     */
    public String getHelpMsg() {
      return helpMsg;
    }
    
    /**
     * @return the convertCode
     */
    public char getConvertCode() {
      return convertCode;
    }
    
    /**
	    * 
	    */
    private Object resHolder = null;
    /**
	    * 
	    */
    private RangeAtom rangeList = null;
    /**
	    * 
	    */
    private RangeAtom rangeTail = null;
    /**
	    * 
	    */
    private char convertCode;
    /**
	    * 
	    */
    private boolean vval = true; // default value for now
    
    /**
     * Decides whether or not this option should be visible in the command-line
     * help description.
     */
    private boolean isVisible = true;
    
    /**
     * 
     * @return
     */
    public NameDesc firstNameDesc() {
      return nameList;
    }
    
    /**
     * 
     * @return
     */
    public RangeAtom firstRangeAtom() {
      return rangeList;
    }
    
    /**
     * 
     * @return
     */
    public int numRangeAtoms() {
      int cnt = 0;
      for (RangeAtom ra = rangeList; ra != null; ra = ra.next) {
        cnt++;
      }
      return cnt;
    }
    
    /**
     * 
     * @param ra
     */
    public void addRangeAtom(RangeAtom ra) {
      if (rangeList == null) {
        rangeList = ra;
      } else {
        rangeTail.next = ra;
      }
      rangeTail = ra;
    }
    
    /**
     * 
     * @param d
     * @return
     */
    public boolean withinRange(double d) {
      if (rangeList == null) { return true; }
      for (RangeAtom ra = rangeList; ra != null; ra = ra.next) {
        if (ra.match(d)) { return true; }
      }
      return false;
    }
    
    /**
     * 
     * @param l
     * @return
     */
    public boolean withinRange(long l) {
      if (rangeList == null) { return true; }
      for (RangeAtom ra = rangeList; ra != null; ra = ra.next) {
        if (ra.match(l)) { return true; }
      }
      return false;
    }
    
    /**
     * 
     * @param s
     * @return
     */
    public boolean withinRange(String s) {
      if (rangeList == null) { return true; }
      for (RangeAtom ra = rangeList; ra != null; ra = ra.next) {
        if (ra.match(s)) { return true; }
      }
      return false;
    }
    
    /**
     * 
     * @param b
     * @return
     */
    public boolean withinRange(boolean b) {
      if (rangeList == null) { return true; }
      for (RangeAtom ra = rangeList; ra != null; ra = ra.next) {
        if (ra.match(b)) { return true; }
      }
      return false;
    }
    
    /**
     * 
     * @return
     */
    public String valTypeName() {
      switch (convertCode) {
        case 'i': {
          return ("integer");
        }
        case 'o': {
          return ("octal integer");
        }
        case 'd': {
          return ("decimal integer");
        }
        case 'x': {
          return ("hex integer");
        }
        case 'c': {
          return ("char");
        }
        case 'b': {
          return ("boolean");
        }
        case 'f': {
          return ("float");
        }
        case 's': {
          return ("string");
        }
      }
      return ("unknown");
    }
    
    /**
     * 
     * @param result
     * @param name
     * @param s
     * @param resultIdx
     * @throws ArgParseException
     */
    @SuppressWarnings("unchecked")
    public void scanValue(Object result, String name, String s, int resultIdx)
      throws ArgParseException {
      double dval = 0;
      String sval = null;
      long lval = 0;
      boolean bval = false;
      
      if (s.length() == 0) { throw new ArgParseException(name,
        "requires a contiguous value"); }
      StringScanner scanner = new StringScanner(s);
      try {
        switch (convertCode) {
          case 'i': {
            lval = scanner.scanInt();
            break;
          }
          case 'o': {
            lval = scanner.scanInt(8, false);
            break;
          }
          case 'd': {
            lval = scanner.scanInt(10, false);
            break;
          }
          case 'x': {
            lval = scanner.scanInt(16, false);
            break;
          }
          case 'c': {
            lval = scanner.scanChar();
            break;
          }
          case 'b': {
            bval = scanner.scanBoolean();
            break;
          }
          case 'f': {
            dval = scanner.scanDouble();
            break;
          }
          case 's': {
            sval = scanner.getString();
            break;
          }
        }
      } catch (StringScanException e) {
        throw new ArgParseException(name, "malformed " + valTypeName() + " '"
            + s + "'");
      }
      scanner.skipWhiteSpace();
      if (!scanner.atEnd()) { throw new ArgParseException(name, "malformed "
          + valTypeName() + " '" + s + "'"); }
      boolean outOfRange = false;
      switch (type) {
        case CHAR:
        case INT:
        case LONG: {
          outOfRange = !withinRange(lval);
          break;
        }
        case FLOAT:
        case DOUBLE: {
          outOfRange = !withinRange(dval);
          break;
        }
        case STRING: {
          outOfRange = !withinRange(sval);
          break;
        }
        case BOOLEAN: {
          outOfRange = !withinRange(bval);
          break;
        }
      }
      if (outOfRange) { //String errmsg = "value " + s + " not in range ";
        throw new ArgParseException(name, "value '" + s + "' not in range "
            + rangeDesc);
      }
      if (result.getClass().isArray()) {
        switch (type) {
          case BOOLEAN: {
            ((boolean[]) result)[resultIdx] = bval;
            break;
          }
          case CHAR: {
            ((char[]) result)[resultIdx] = (char) lval;
            break;
          }
          case INT: {
            ((int[]) result)[resultIdx] = (int) lval;
            break;
          }
          case LONG: {
            ((long[]) result)[resultIdx] = lval;
            break;
          }
          case FLOAT: {
            ((float[]) result)[resultIdx] = (float) dval;
            break;
          }
          case DOUBLE: {
            ((double[]) result)[resultIdx] = dval;
            break;
          }
          case STRING: {
            ((String[]) result)[resultIdx] = sval;
            break;
          }
        }
      } else {
        switch (type) {
          case BOOLEAN: {
            ((ArgHolder<Boolean>) result).setValue(Boolean.valueOf(bval));
            break;
          }
          case CHAR: {
            ((ArgHolder<Character>) result).setValue(Character
                .valueOf((char) lval));
            break;
          }
          case INT: {
            ((ArgHolder<Integer>) result).setValue(Integer.valueOf((int) lval));
            break;
          }
          case LONG: {
            ((ArgHolder<Long>) result).setValue(lval);
            break;
          }
          case FLOAT: {
            ((ArgHolder<Float>) result).setValue(Float.valueOf((float) dval));
            break;
          }
          case DOUBLE: {
            ((ArgHolder<Double>) result).setValue(Double.valueOf(dval));
            break;
          }
          case STRING: {
            ((ArgHolder<String>) result).setValue(sval);
            break;
          }
        }
      }
    }
    
    /**
     * @return
     */
    public boolean isVisible() {
      return isVisible;
    }
    
    /**
     * 
     * @param visible
     * @return
     */
    public void setVisible(boolean visible) {
      isVisible = visible;
    }
  }
  
  /**
   * 
   * @return
   */
  private String firstHelpOptionName() {
    if (firstHelpOption != null) {
      return firstHelpOption.nameList.name;
    } else {
      return null;
    }
  }
  
  /**
   * Creates an {@code ArgParser} with a synopsis string, and the default
   * help options {@code --help} and {@code -&#063;}.
   * 
   * @param synopsisString
   *        string that briefly describes program usage, for use by
   *        {@link #getHelpMessage getHelpMessage}.
   * @see ArgParser#getSynopsisString
   * @see ArgParser#getHelpMessage
   */
  public ArgParser(String synopsisString) {
    this(synopsisString, true);
  }
  
  /**
   * Creates an {@code ArgParser} with a synopsis string. The help options
   * {@code --help} and {@code -?} are added if {@code defaultHelp} is true.
   * 
   * @param synopsisString
   *        string that briefly describes program usage, for use by
   *        {@link #getHelpMessage getHelpMessage}.
   * @param defaultHelp
   *        if true, adds the default help options
   * @see ArgParser#getSynopsisString
   * @see ArgParser#getHelpMessage
   */
  public ArgParser(String synopsisString, boolean defaultHelp) {
    matchList = new Vector<Record>(128);
    this.synopsisString = synopsisString;
    if (defaultHelp) {
      addOption("--help,-? %h #displays help information", null);
      defaultHelpOption = firstHelpOption = (Record) matchList.get(0);
    }
  }
  
  /**
   * Returns the synopsis string used by the parser. The synopsis string is a
   * short description of how to invoke the program, and usually looks something
   * like
   * <p>
   * <prec> "java somepackage.SomeClass [options] files ..." </prec>
   * 
   * <p>
   * It is used in help and error messages.
   * 
   * @return synopsis string
   * @see ArgParser#setSynopsisString
   * @see ArgParser#getHelpMessage
   */
  public String getSynopsisString() {
    return synopsisString;
  }
  
  /**
   * Sets the synopsis string used by the parser.
   * 
   * @param s
   *        new synopsis string
   * @see ArgParser#getSynopsisString
   * @see ArgParser#getHelpMessage
   */
  public void setSynopsisString(String s) {
    synopsisString = s;
  }
  
  /**
   * Indicates whether or not help options are enabled.
   * 
   * @return true if help options are enabled
   * @see ArgParser#setHelpOptionsEnabled
   * @see ArgParser#addOption
   */
  public boolean getHelpOptionsEnabled() {
    return helpOptionsEnabled;
  }
  
  /**
   * Enables or disables help options. Help options are those associated with a
   * conversion code of {@code %h}. If help options are enabled, and a help
   * option is matched, then the string produced by {@link #getHelpMessage
   * getHelpMessage} is printed to the default print stream and the program
   * exits with code 0. Otherwise, arguments which match help options are
   * ignored.
   * 
   * @param enable
   *        enables help options if {@code true}.
   * @see ArgParser#getHelpOptionsEnabled
   * @see ArgParser#addOption
   * @see ArgParser#setDefaultPrintStream
   */
  public void setHelpOptionsEnabled(boolean enable) {
    helpOptionsEnabled = enable;
  }
  
  /**
   * Returns the default print stream used for outputting help and error
   * information.
   * 
   * @return default print stream
   * @see ArgParser#setDefaultPrintStream
   */
  public PrintStream getDefaultPrintStream() {
    return printStream;
  }
  
  /**
   * Sets the default print stream used for outputting help and error
   * information.
   * 
   * @param stream
   *        new default print stream
   * @see ArgParser#getDefaultPrintStream
   */
  public void setDefaultPrintStream(PrintStream stream) {
    printStream = stream;
  }
  
  /**
   * Gets the indentation used by {@link #getHelpMessage getHelpMessage}.
   * 
   * @return number of indentation columns
   * @see ArgParser#setHelpIndentation
   * @see ArgParser#getHelpMessage
   */
  public int getHelpIndentation() {
    return helpIndent;
  }
  
  /**
   * Sets the indentation used by {@link #getHelpMessage getHelpMessage}. This
   * is the number of columns that an option's help information is indented. If
   * the option's name and value information can fit within this number of
   * columns, then all information about the option is placed on one line.
   * Otherwise, the indented help information is placed on a separate line.
   * 
   * @param indent
   *        number of indentation columns
   * @see ArgParser#getHelpIndentation
   * @see ArgParser#getHelpMessage
   */
  public void setHelpIndentation(int indent) {
    helpIndent = indent;
  }
  
  /**
   * Returns the number of columns that the console is assumed to have.
   * 
   * @return
   */
  public int getConsoleColumns() {
    return consoleColumns;
  }
  
  /**
   * Sets the number of columns that the console to which output is written is
   * assumed to have. Default is 80.
   * 
   * @param columns
   */
  public void setConsoleColumns(int columns) {
    consoleColumns = columns;
  }
  
  /**
	 * 
	 */
  private void scanRangeSpec(Record rec, String s)
    throws IllegalArgumentException {
    StringScanner scanner = new StringScanner(s);
    //int i = 1, i0;
    char c, c0, c1;
    
    scanner.setStringDelimiters(")],}");
    c = scanner.getc(); // swallow the first '{'
    scanner.skipWhiteSpace();
    while ((c = scanner.peekc()) != '}') {
      RangePnt p0, p1;
      
      if (c == '[' || c == '(') {
        if (rec.convertCode == 'v' || rec.convertCode == 'b') { throw new IllegalArgumentException(
          "Sub ranges not supported for %b or %v"); }
        c0 = scanner.getc(); // record & swallow character
        scanner.skipWhiteSpace();
        p0 = new RangePnt(scanner, rec.type);
        scanner.skipWhiteSpace();
        if (scanner.getc() != ',') { throw new IllegalArgumentException(
          "Missing ',' in subrange specification"); }
        p1 = new RangePnt(scanner, rec.type);
        scanner.skipWhiteSpace();
        if ((c1 = scanner.getc()) != ']' && c1 != ')') { throw new IllegalArgumentException(
          "Unterminated subrange"); }
        if (c0 == '(') {
          p0.setClosed(false);
        }
        if (c1 == ')') {
          p1.setClosed(false);
        }
        rec.addRangeAtom(new RangeAtom(p0, p1, rec.type));
      } else {
        scanner.skipWhiteSpace();
        p0 = new RangePnt(scanner, rec.type);
        rec.addRangeAtom(new RangeAtom(p0));
      }
      scanner.skipWhiteSpace();
      if ((c = scanner.peekc()) == ',') {
        scanner.getc();
        scanner.skipWhiteSpace();
      } else if (c != '}') { throw new IllegalArgumentException(
        "Range spec: ',' or '}' expected"); }
    }
    if (rec.numRangeAtoms() == 1) {
      rec.rangeDesc = s.substring(1, s.length() - 1);
    } else {
      rec.rangeDesc = s;
    }
  }
  
  /**
   * 
   * @param convertCode
   * @return
   */
  private int defaultResultType(char convertCode) {
    switch (convertCode) {
      case 'i':
      case 'o':
      case 'd':
      case 'x': {
        return Record.LONG;
      }
      case 'c': {
        return Record.CHAR;
      }
      case 'v':
      case 'b': {
        return Record.BOOLEAN;
      }
      case 'f': {
        return Record.DOUBLE;
      }
      case 's': {
        return Record.STRING;
      }
    }
    return Record.NOTYPE;
  }
  
  /**
   * Adds a new option description to the parser. The method takes two
   * arguments: a specification string, and a result holder in which to
   * store the associated value.
   * 
   * <p>
   * The specification string has the general form
   * 
   * <p>
   * <var>optionNames</var> {@code %}<var>conversionCode</var> [{@code
   * <var>rangeSpec</var>{@code } ] [{@code X}<var>multiplier</var>] [{@code #}
   * <var>valueDescription</var>] [{@code #}<var>optionDescription</var>] }
   * 
   * <p>
   * where
   * <ul>
   * <p>
   * <li><var>optionNames</var> is a comma-separated list of names for the
   * option (such as {@code -f, --file}).
   * 
   * <p>
   * <li><var>conversionCode</var> is a single letter, following a {@code %}
   * character, specifying information about what value the option requires:
   * 
   * <table>
   * <tr>
   * <td>{@code %f}</td>
   * <td>a floating point number</td>
   * <tr>
   * <td>{@code %i}</td>
   * <td>an integer, in either decimal, hex (if preceeded by {@code 0x}), or
   * octal (if preceeded by {@code 0})</td>
   * <tr valign=top>
   * <td>{@code %d}</td>
   * <td>a decimal integer</td>
   * <tr valign=top>
   * <td>{@code %o}</td>
   * <td>an octal integer</td>
   * <tr valign=top>
   * <td>{@code %h}</td>
   * <td>a hex integer (without the preceeding {@code 0x})</td>
   * <tr valign=top>
   * <td>{@code %c}</td>
   * <td>a single character, including escape sequences (such as {@code \n} or
   * {@code \007}), and optionally enclosed in single quotes
   * <tr valign=top>
   * <td>{@code %b}</td>
   * <td>a boolean value ({@code true} or {@code false})</td>
   * <tr valign=top>
   * <td>{@code %s}</td>
   * <td>a string. This will be the argument string itself (or its remainder, in
   * the case of a single word option)</td>
   * <tr valign=top>
   * <td>{@code %v}</td>
   * <td>no explicit value is expected, but a boolean value of {@code true} (by
   * default) will be stored into the associated result holder if this option is
   * matched. If one wishes to have a value of {@code false} stored instead,
   * then the {@code %v} should be followed by a "range spec" containing
   * {@code false}, as in {@code %v false}}.
   * </table>
   * 
   * <p>
   * <li><var>rangeSpec</var> is an optional range specification, placed inside
   * curly braces, consisting of a comma-separated list of range items each
   * specifying permissible values for the option. A range item may be an
   * individual value, or it may itself be a subrange, consisting of two
   * individual values, separated by a comma, and enclosed in square or round
   * brackets. Square and round brackets denote closed and open endpoints of a
   * subrange, indicating that the associated endpoint value is included or
   * excluded from the subrange. The values specified in the range spec need to
   * be consistent with the type of value expected by the option.
   * 
   * <p>
   * <b>Examples:</b>
   * 
   * <p>
   * A range spec of {@code 2,4,8,16}} for an integer value will allow the
   * integers 2, 4, 8, or 16.
   * 
   * <p>
   * A range spec of {@code [-1.0,1.0]}} for a floating point value will allow
   * any floating point number in the range -1.0 to 1.0.
   * 
   * <p>
   * A range spec of {@code (-88,100],1000}} for an integer value will allow
   * values > -88 and <= 100, as well as 1000.
   * 
   * <p>
   * A range spec of {@code "foo", "bar", ["aaa","zzz")} } for a string value
   * will allow strings equal to {@code "foo"} or {@code "bar"}, plus any string
   * lexically greater than or equal to {@code "aaa"} but less then
   * {@code "zzz"}.
   * 
   * <p>
   * <li><var>multiplier</var> is an optional integer, following a {@code X}
   * character, indicating the number of values which the option expects. If the
   * multiplier is not specified, it is assumed to be 1. If the multiplier value
   * is greater than 1, then the result holder should be either an array (of
   * appropriate type) with a length greater than or equal to the multiplier
   * value, or a {@code java.util.Vector} <a href=#vectorHolder>as discussed
   * below</a>.
   * 
   * <p>
   * <li><var>valueDescription</var> is an optional description of the option's
   * value requirements, and consists of all characters between two {@code #}
   * characters. The final {@code #} character initiates the <i>option
   * description</i>, which may be empty. The value description is used in <a
   * href=#helpInfo>generating help messages</a>.
   * 
   * <p>
   * <li><var>optionDescription</var> is an optional description of the option
   * itself, consisting of all characters between a {@code #} character and the
   * end of the specification string. The option description is used in <a
   * href=#helpInfo>generating help messages</a>.
   * </ul>
   * 
   * <p>
   * The result holder must be an object capable of holding a value compatible
   * with the conversion code, or it must be a {@code java.util.Vector}. When
   * the option is matched, its associated value is placed in the result holder.
   * If the same option is matched repeatedly, the result holder value will be
   * overwritten, unless the result holder is a {@code java.util.Vector}, in
   * which case new holder objects for each match will be allocated and added to
   * the vector. Thus if multiple instances of an option are desired by the
   * program, the result holder should be a {@code java.util.Vector}.
   * 
   * <p>
   * If the result holder is not a {@code Vector}, then it must correspond as
   * follows to the conversion code:
   * 
   * <table>
   * <tr valign=top>
   * <td>{@code %i}, {@code %d}, {@code %x}, {@code %o}</td>
   * <td>{@link argparser.IntHolder IntHolder}, {@link argparser.LongHolder
   * LongHolder}, {@code int[]}, or {@code long[]}</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %f}</td>
   * <td>{@link argparser.FloatHolder FloatHolder},
   * {@link argparser.DoubleHolder DoubleHolder}, {@code float[]}, or
   * {@code double[]}</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %b}, {@code %v}</td>
   * <td>{@link argparser.BooleanHolder BooleanHolder} or {@code boolean[]}</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %s}</td>
   * <td>{@link argparser.StringHolder StringHolder} or {@code String[]}</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %c}</td>
   * <td>{@link argparser.CharHolder CharHolder} or {@code char[]}</td>
   * </tr>
   * </table>
   * 
   * <p>
   * In addition, if the multiplier is greater than 1, then only the array type
   * indicated above may be used, and the array must be at least as long as the
   * multiplier.
   * 
   * <p>
   * <a name=vectorHolder>If the result holder is a {@code Vector}, then the
   * system will create an appropriate result holder object and add it to the
   * vector. Multiple occurances of the option will cause multiple results to be
   * added to the vector.
   * 
   * <p>
   * The object allocated by the system to store the result will correspond to
   * the conversion code as follows:
   * 
   * <table>
   * <tr valign=top>
   * <td>{@code %i}, {@code %d}, {@code %x}, {@code %o}</td>
   * <td>{@link argparser.LongHolder LongHolder}, or {@code long[]} if the
   * multiplier value exceeds 1</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %f}</td>
   * <td>{@link argparser.DoubleHolder DoubleHolder}, or {@code double[]} if the
   * multiplier value exceeds 1</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %b}, {@code %v}</td>
   * <td>{@link argparser.BooleanHolder BooleanHolder}, or {@code boolean[]} if
   * the multiplier value exceeds 1</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %s}</td>
   * <td>{@link argparser.StringHolder StringHolder}, or {@code String[]} if the
   * multiplier value exceeds 1</td>
   * </tr>
   * 
   * <tr valign=top>
   * <td>{@code %c}</td>
   * <td>{@link argparser.CharHolder CharHolder}, or {@code char[]} if the
   * multiplier value exceeds 1</td>
   * </tr>
   * </table>
   * 
   * @param spec
   *        the specification string
   * @param resHolder
   *        object in which to store the associated
   *        value
   * @param visible
   *        decides whether or not this option should be
   *        displayed in the command-line help.
   * @throws IllegalArgumentException
   *         if there is an error in
   *         the specification or if the result holder is of an invalid
   *         type.
   */
  public void addOption(String spec, Object resHolder, boolean visible)
    throws IllegalArgumentException {
    // null terminated string is easier to parse
    StringScanner scanner = new StringScanner(spec);
    Record rec = null;
    NameDesc nameTail = null;
    NameDesc ndesc;
    int i0, i1;
    char c;
    
    do {
      ndesc = new NameDesc();
      boolean nameEndsInWhiteSpace = false;
      
      scanner.skipWhiteSpace();
      i0 = scanner.getIndex();
      while (!Character.isWhitespace(c = scanner.getc()) && c != ','
          && c != '%' && c != '\000')
        ;
      i1 = scanner.getIndex();
      if (c != '\000') {
        i1--;
      }
      if (i0 == i1) { // then c is one of ',' '%' or '\000'
        throw new IllegalArgumentException("Null option name given");
      }
      if (Character.isWhitespace(c)) {
        nameEndsInWhiteSpace = true;
        scanner.skipWhiteSpace();
        c = scanner.getc();
      }
      if (c == '\000') { throw new IllegalArgumentException(
        "No conversion character given"); }
      if (c != ',' && c != '%') { throw new IllegalArgumentException(
        "Names not separated by ','"); }
      ndesc.name = scanner.substring(i0, i1);
      if (rec == null) {
        rec = new Record();
        rec.nameList = ndesc;
      } else {
        nameTail.next = ndesc;
      }
      nameTail = ndesc;
      ndesc.oneWord = !nameEndsInWhiteSpace;
    } while (c != '%');
    
    if (!nameTail.oneWord) {
      for (ndesc = rec.nameList; ndesc != null; ndesc = ndesc.next) {
        ndesc.oneWord = false;
      }
    }
    c = scanner.getc();
    if (c == '\000') { throw new IllegalArgumentException(
      "No conversion character given"); }
    if (validConversionCodes.indexOf(c) == -1) { throw new IllegalArgumentException(
      String.format("Conversion code '%s' not one of '%s'", c,
        validConversionCodes)); }
    rec.convertCode = c;
    
    if (resHolder instanceof Vector<?>) {
      rec.type = defaultResultType(rec.convertCode);
    } else {
      switch (rec.convertCode) {
        case 'i':
        case 'o':
        case 'd':
        case 'x': {
          if (((resHolder instanceof ArgHolder<?>) && (((ArgHolder<?>) resHolder)
              .getType().equals(Long.class))) || (resHolder instanceof long[])) {
            rec.type = Record.LONG;
          } else if (((resHolder instanceof ArgHolder<?>) && (((ArgHolder<?>) resHolder)
              .getType().equals(Integer.class)))
              || (resHolder instanceof int[])) {
            rec.type = Record.INT;
          } else {
            throw new IllegalArgumentException("Invalid result holder for %"
                + c);
          }
          break;
        }
        case 'c': {
          if (!((resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) resHolder)
              .getType().equals(Character.class))
              && !(resHolder instanceof char[])) { throw new IllegalArgumentException(
            "Invalid result holder for %c"); }
          rec.type = Record.CHAR;
          break;
        }
        case 'v':
        case 'b': {
          if (!((resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) resHolder)
              .getType().equals(Boolean.class))
              && !(resHolder instanceof boolean[])) { throw new IllegalArgumentException(
            "Invalid result holder for %" + c); }
          rec.type = Record.BOOLEAN;
          break;
        }
        case 'f': {
          if (((resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) resHolder)
              .getType().equals(Double.class))
              || (resHolder instanceof double[])) {
            rec.type = Record.DOUBLE;
          } else if (((resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) resHolder)
              .getType().equals(Float.class)) || (resHolder instanceof float[])) {
            rec.type = Record.FLOAT;
          } else {
            throw new IllegalArgumentException("Invalid result holder for %f");
          }
          break;
        }
        case 's': {
          if (!((resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) resHolder)
              .getType().equals(String.class))
              && !(resHolder instanceof String[])) { throw new IllegalArgumentException(
            "Invalid result holder for %s"); }
          rec.type = Record.STRING;
          break;
        }
        case 'h': { // resHolder is ignored for this type
          break;
        }
      }
    }
    if (rec.convertCode == 'h') {
      rec.resHolder = null;
    } else {
      rec.resHolder = resHolder;
    }
    
    scanner.skipWhiteSpace();
    // get the range specification, if any
    if (scanner.peekc() == '{') {
      if (rec.convertCode == 'h') { throw new IllegalArgumentException(
        "Ranges not supported for %h"); }
      //	      int bcnt = 0;
      i0 = scanner.getIndex(); // beginning of range spec
      do {
        c = scanner.getc();
        if (c == '\000') { throw new IllegalArgumentException(
          "Unterminated range specification"); }
        //  		 else if (c=='[' || c=='(')
        //  		  { bcnt++;
        //  		  }
        //  		 else if (c==']' || c==')')
        //  		  { bcnt--;
        //  		  }
        //  		 if ((rec.convertCode=='v'||rec.convertCode=='b') && bcnt>1)
        //  		  { throw new IllegalArgumentException
        //  		      ("Sub ranges not supported for %b or %v");
        //  		  }
      } while (c != '}');
      //  	      if (c != ']')
      //  	       { throw new IllegalArgumentException
      //  		    ("Range specification must end with ']'");
      //  	       }
      i1 = scanner.getIndex(); // end of range spec
      scanRangeSpec(rec, scanner.substring(i0, i1));
      if (rec.convertCode == 'v' && rec.rangeList != null) {
        rec.vval = rec.rangeList.low.bval;
      }
    }
    // check for value multiplicity information, if any 
    if (scanner.peekc() == 'X') {
      if (rec.convertCode == 'h') { throw new IllegalArgumentException(
        "Multipliers not supported for %h"); }
      scanner.getc();
      try {
        rec.numValues = (int) scanner.scanInt();
      } catch (StringScanException e) {
        throw new IllegalArgumentException("Malformed value multiplier");
      }
      if (rec.numValues <= 0) { throw new IllegalArgumentException(
        "Value multiplier number must be > 0"); }
    } else {
      rec.numValues = 1;
    }
    if (rec.numValues > 1) {
      for (ndesc = rec.nameList; ndesc != null; ndesc = ndesc.next) {
        if (ndesc.oneWord) { throw new IllegalArgumentException(
          "Multiplier value incompatible with one word option " + ndesc.name); }
      }
    }
    if (resHolder != null && resHolder.getClass().isArray()) {
      if (Array.getLength(resHolder) < rec.numValues) { throw new IllegalArgumentException(
        "Result holder array must have a length >= " + rec.numValues); }
    } else {
      if ((rec.numValues > 1) && !(resHolder instanceof Vector<?>)) { throw new IllegalArgumentException(
        "Multiplier requires result holder to be an array of length >= "
            + rec.numValues); }
    }
    
    // skip white space following conversion information
    scanner.skipWhiteSpace();
    
    // get the help message, if any
    
    if (!scanner.atEnd()) {
      if (scanner.getc() != '#') { throw new IllegalArgumentException(
        "Illegal character(s), expecting '#'"); }
      String helpInfo = scanner.substring(scanner.getIndex());
      // look for second '#'. If there is one, then info
      // between the first and second '#' is the value descriptor.
      int k = helpInfo.indexOf("#");
      if (k != -1) {
        rec.valueDesc = helpInfo.substring(0, k);
        rec.helpMsg = helpInfo.substring(k + 1);
      } else {
        rec.helpMsg = helpInfo;
      }
    } else {
      rec.helpMsg = "";
    }
    // add option information to match list
    if (rec.convertCode == 'h' && firstHelpOption == defaultHelpOption) {
      matchList.remove(defaultHelpOption);
      firstHelpOption = rec;
    }
    rec.setVisible(visible);
    matchList.add(rec);
  }
  
  /**
   * Adds an option to the arg-parser that will be displayed in the command-line
   * help if a help string is available.
   * 
   * @param spec
   * @param resHolder
   * @throws IllegalArgumentException
   * @see {@link #addOption(String, Object, boolean)}
   */
  public void addOption(String spec, Object resHolder)
    throws IllegalArgumentException {
    addOption(spec, resHolder, true);
  }
  
  public void addDelimiter(String text) {
    
    NameDesc ndesc = new NameDesc();
    ndesc.name = text;
    // ndesc.oneWord = false;
    
    Record rec = new Record();
    rec.type = Record.DELIM;
    rec.nameList = ndesc;
    
    matchList.add(rec);
  }
  
  /**
   * 
   * @return
   */
  public Record lastMatchRecord() {
    return matchList.get(matchList.size() - 1);
  }
  
  /**
   * 
   * @param arg
   * @param ndescHolder
   * @return
   */
  private Record getRecord(String arg, ArgHolder<Object> ndescHolder) {
    NameDesc ndesc;
    for (int i = 0; i < matchList.size(); i++) {
      Record rec = (Record) matchList.get(i);
      for (ndesc = rec.nameList; ndesc != null; ndesc = ndesc.next) {
        if (rec.convertCode != 'v' && ndesc.oneWord) {
          if (arg.startsWith(ndesc.name)) {
            if (ndescHolder != null) {
              ndescHolder.setValue(ndesc);
            }
            return rec;
          }
        } else {
          if (arg.equals(ndesc.name)) {
            if (ndescHolder != null) {
              ndescHolder.setValue(ndesc);
            }
            return rec;
          }
        }
      }
    }
    return null;
  }
  
  /**
   * 
   * @param arg
   * @return
   */
  protected Object getResultHolder(String arg) {
    Record rec = getRecord(arg, null);
    return (rec != null) ? rec.resHolder : null;
  }
  
  /**
   * 
   * @param arg
   * @return
   */
  protected String getOptionName(String arg) {
    ArgHolder<Object> ndescHolder = new ArgHolder<Object>(Object.class);
    Record rec = getRecord(arg, ndescHolder);
    return (rec != null) ? ((NameDesc) ndescHolder.getValue()).name : null;
  }
  
  /**
   * 
   * @param arg
   * @return
   */
  protected String getOptionRangeDesc(String arg) {
    Record rec = getRecord(arg, null);
    return (rec != null) ? rec.rangeDesc : null;
  }
  
  /**
   * 
   * @param arg
   * @return
   */
  protected String getOptionTypeName(String arg) {
    Record rec = getRecord(arg, null);
    return (rec != null) ? rec.valTypeName() : null;
  }
  
  /**
   * 
   * @param rec
   * @return
   */
  private Object createResultHolder(Record rec) {
    if (rec.numValues == 1) {
      switch (rec.type) {
        case Record.LONG: {
          return new ArgHolder<Long>(Long.class);
        }
        case Record.CHAR: {
          return new ArgHolder<Character>(Character.class);
        }
        case Record.BOOLEAN: {
          return new ArgHolder<Boolean>(Boolean.class);
        }
        case Record.DOUBLE: {
          return new ArgHolder<Double>(Double.class);
        }
        case Record.STRING: {
          return new ArgHolder<String>(String.class);
        }
      }
    } else {
      switch (rec.type) {
        case Record.LONG: {
          return new long[rec.numValues];
        }
        case Record.CHAR: {
          return new char[rec.numValues];
        }
        case Record.BOOLEAN: {
          return new boolean[rec.numValues];
        }
        case Record.DOUBLE: {
          return new double[rec.numValues];
        }
        case Record.STRING: {
          return new String[rec.numValues];
        }
      }
    }
    return null; // can't happen
  }
  
  /**
   * 
   * @param vec
   * @param s
   * @param allowQuotedStrings
   * @throws StringScanException
   */
  public static void stringToArgs(Vector<String> vec, String s,
    boolean allowQuotedStrings) throws StringScanException {
    StringScanner scanner = new StringScanner(s);
    scanner.skipWhiteSpace();
    while (!scanner.atEnd()) {
      if (allowQuotedStrings) {
        vec.add(scanner.scanString());
      } else {
        vec.add(scanner.scanNonWhiteSpaceString());
      }
      scanner.skipWhiteSpace();
    }
  }
  
  /**
   * Reads in a set of strings from a reader and prepends them to an argument
   * list. Strings are delimited by either whitespace or double quotes {@code "}
   * .
   * The character {@code #} acts as a comment character, causing input to
   * the end of the current line to be ignored.
   * 
   * @param reader
   *        Reader from which to read the strings
   * @param args
   *        Initial set of argument values. Can be specified as {@code null}.
   * @throws IOException
   *         if an error occured while reading.
   */
  public static String[] prependArgs(Reader reader, String[] args)
    throws IOException {
    if (args == null) {
      args = new String[0];
    }
    LineNumberReader lineReader = new LineNumberReader(reader);
    Vector<String> vec = new Vector<String>(100, 100);
    String line;
    int i, k;
    
    while ((line = lineReader.readLine()) != null) {
      int commentIdx = line.indexOf("#");
      if (commentIdx != -1) {
        line = line.substring(0, commentIdx);
      }
      try {
        stringToArgs(vec, line, /* allowQuotedStings= */true);
      } catch (StringScanException e) {
        throw new IOException("malformed string, line "
            + lineReader.getLineNumber());
      }
    }
    String[] result = new String[vec.size() + args.length];
    for (i = 0; i < vec.size(); i++) {
      result[i] = (String) vec.get(i);
    }
    for (k = 0; k < args.length; k++) {
      result[i++] = args[k];
    }
    return result;
  }
  
  /**
   * Reads in a set of strings from a file and prepends them to an argument
   * list. Strings are delimited by either whitespace or double quotes {@code "}
   * .
   * The character {@code #} acts as a comment character, causing input to
   * the end of the current line to be ignored.
   * 
   * @param file
   *        File to be read
   * @param args
   *        Initial set of argument values. Can be specified as {@code null}.
   * @throws IOException
   *         if an error occured while reading the file.
   */
  public static String[] prependArgs(File file, String[] args)
    throws IOException {
    if (args == null) {
      args = new String[0];
    }
    if (!file.canRead()) { return args; }
    try {
      return prependArgs(new FileReader(file), args);
    } catch (IOException e) {
      throw new IOException("File " + file.getName() + ": " + e.getMessage());
    }
  }
  
  /**
   * Sets the parser's error message.
   * 
   * @param s
   *        Error message
   */
  protected void setError(String msg) {
    errMsg = msg;
  }
  
  /**
   * Prints an error message, along with a pointer to help options, if
   * available, and causes the program to exit with code 1.
   */
  public void printErrorAndExit(String msg) {
    if (helpOptionsEnabled && firstHelpOptionName() != null) {
      msg += "\nUse " + firstHelpOptionName() + " for help information";
    }
    if (printStream != null) {
      printStream.println(msg);
    }
    System.exit(1);
  }
  
  /**
   * Matches arguments within an argument list.
   * 
   * <p>
   * In the event of an erroneous or unmatched argument, the method prints a
   * message and exits the program with code 1.
   * 
   * <p>
   * If help options are enabled and one of the arguments matches a help option,
   * then the result of {@link #getHelpMessage getHelpMessage} is printed to the
   * default print stream and the program exits with code 0. If help options are
   * not enabled, they are ignored.
   * 
   * @param args
   *        argument list
   * @see ArgParser#getDefaultPrintStream
   */
  public void matchAllArgs(String[] args) {
    matchAllArgs(args, 0, EXIT_ON_UNMATCHED | EXIT_ON_ERROR);
  }
  
  /**
   * Matches arguments within an argument list and returns those which were not
   * matched. The matching starts at a location in {@code args} specified
   * by {@code idx}, and unmatched arguments are returned in a String
   * array.
   * 
   * <p>
   * In the event of an erroneous argument, the method either prints a message
   * and exits the program (if {@link #EXIT_ON_ERROR} is set in
   * {@code exitFlags}) or terminates the matching and creates a error message
   * that can be retrieved by {@link #getErrorMessage}.
   * 
   * <p>
   * In the event of an umatched argument, the method will print a message and
   * exit if {@link #EXIT_ON_UNMATCHED} is set in {@code errorFlags}. Otherwise,
   * the unmatched argument will be appended to the returned array of unmatched
   * values, and the matching will continue at the next location.
   * 
   * <p>
   * If help options are enabled and one of the arguments matches a help option,
   * then the result of {@link #getHelpMessage getHelpMessage} is printed to the
   * the default print stream and the program exits with code 0. If help options
   * are not enabled, then they will not be matched.
   * 
   * @param args
   *        argument list
   * @param idx
   *        starting location in list
   * @param exitFlags
   *        conditions causing the program to exit. Should be an or-ed
   *        combintion of {@link #EXIT_ON_ERROR} or {@link #EXIT_ON_UNMATCHED}.
   * @return array of arguments that were not matched, or {@code null} if
   *         all arguments were successfully matched
   * @see ArgParser#getErrorMessage
   * @see ArgParser#getDefaultPrintStream
   */
  public String[] matchAllArgs(String[] args, int idx, int exitFlags) {
    Vector<String> unmatched = new Vector<String>(10);
    
    while (args != null && idx < args.length) {
      try {
        idx = matchArg(args, idx);
        if (unmatchedArg != null) {
          if ((exitFlags & EXIT_ON_UNMATCHED) != 0) {
            printErrorAndExit("Unrecognized argument: " + unmatchedArg);
          } else {
            unmatched.add(unmatchedArg);
          }
        }
      } catch (ArgParseException e) {
        if ((exitFlags & EXIT_ON_ERROR) != 0) {
          printErrorAndExit(e.getMessage());
        }
        break;
      }
    }
    if (unmatched.size() == 0) {
      return null;
    } else {
      return unmatched.toArray(new String[0]);
    }
  }
  
  /**
   * Matches one option starting at a specified location in an argument list.
   * The method returns the location in the list where the next match should
   * begin.
   * 
   * <p>
   * In the event of an erroneous argument, the method throws an
   * {@link ArgParseException} with an appropriate error message. This error
   * message can also be retrieved using {@link #getErrorMessage
   * getErrorMessage}.
   * 
   * <p>
   * In the event of an umatched argument, the method will return idx + 1, and
   * {@link #getUnmatchedArgument getUnmatchedArgument} will return a copy of
   * the unmatched argument. If an argument is matched,
   * {@link #getUnmatchedArgument getUnmatchedArgument} will return {@code null}.
   * 
   * <p>
   * If help options are enabled and the argument matches a help option, then
   * the result of {@link #getHelpMessage getHelpMessage} is printed to the the
   * default print stream and the program exits with code 0. If help options are
   * not enabled, then they are ignored.
   * 
   * @param args
   *        argument list
   * @param idx
   *        location in list where match should start
   * @return location in list where next match should start
   * @throws ArgParseException
   *         if there was an error performing the match (such as improper or
   *         insufficient values).
   * @see ArgParser#setDefaultPrintStream
   * @see ArgParser#getHelpOptionsEnabled
   * @see ArgParser#getErrorMessage
   * @see ArgParser#getUnmatchedArgument
   */
  @SuppressWarnings("unchecked")
  public int matchArg(String[] args, int idx) throws ArgParseException {
    unmatchedArg = null;
    setError(null);
    try {
      ArgHolder<Object> ndescHolder = new ArgHolder<Object>(Object.class);
      Record rec = getRecord(args[idx], ndescHolder);
      if ((rec == null) || ((rec.convertCode == 'h') && !helpOptionsEnabled)) {
        // didn't match
        unmatchedArg = new String(args[idx]);
        return idx + 1;
      }
      NameDesc ndesc = (NameDesc) ndescHolder.getValue();
      Object result;
      if (rec.resHolder instanceof Vector<?>) {
        result = createResultHolder(rec);
      } else {
        result = rec.resHolder;
      }
      if (rec.convertCode == 'h') {
        if (helpOptionsEnabled) {
          printStream.println(getHelpMessage());
          System.exit(0);
        } else {
          return idx + 1;
        }
      } else if (rec.convertCode != 'v') {
        if (ndesc.oneWord) {
          rec.scanValue(result, ndesc.name,
            args[idx].substring(ndesc.name.length()), 0);
        } else {
          if (rec.convertCode != 'b') {
            if (idx + rec.numValues >= args.length) { throw new ArgParseException(
              ndesc.name, String.format("requires %d value%s", rec.numValues,
                (rec.numValues > 1 ? "s" : ""))); }
            for (int k = 0; k < rec.numValues; k++) {
              rec.scanValue(result, ndesc.name, args[++idx], k);
            }
          } else {
            // special handling of %b to allow for omitting 'true'
            if (idx + rec.numValues >= args.length) {
              // last option followed by nothing, so its 'true'
              ((ArgHolder<Boolean>) result).setValue(true);
            } else if (rec.numValues > 1) {
              // more than one value, must be a boolean array, proceed as usual
              for (int k = 0; k < rec.numValues; k++) {
                rec.scanValue(result, ndesc.name, args[++idx], k);
              }
            } else {
              // only one expected value
              try {
                // try to parse it
                rec.scanValue(result, ndesc.name, args[++idx], 0);
              } catch (ArgParseException e) {
                // if it fails with a "malformed boolean" exception
                if (e.getMessage().contains("malformed boolean")) {
                  // assume that it was omitted and treat it as 'true'
                  ((ArgHolder<Boolean>) result).setValue(true);
                  // and decrement the idx again for correct parsing again
                  idx--;
                } else {
                  throw e;
                }
              }
            }
          }
        }
      } else {
        if (((rec.resHolder instanceof ArgHolder<?>) && ((ArgHolder<?>) rec.resHolder)
            .getType().equals(Boolean.class))) {
          ((ArgHolder<Boolean>) result).setValue(Boolean.valueOf(rec.vval));
        } else {
          for (int k = 0; k < rec.numValues; k++) {
            ((boolean[]) result)[k] = rec.vval;
          }
        }
      }
      if (rec.resHolder instanceof Vector<?>) {
        ((Vector<Object>) rec.resHolder).add(result);
      }
    } catch (ArgParseException e) {
      setError(e.getMessage());
      throw e;
    }
    return idx + 1;
  }
  
  private String spaceString(int n) {
    StringBuffer sbuf = new StringBuffer(n);
    for (int i = 0; i < n; i++) {
      sbuf.append(' ');
    }
    return sbuf.toString();
  }
  
  /**
   * Returns the longest common prefix of both strings. This method is
   * case-sensitive.
   * 
   * @param a
   *        string a
   * @param b
   *        string b
   * @return the longest common prefix
   */
  public static String getLongestCommonPrefix(String a, String b) {
    int i;
    for (i = 0; i < Math.min(a.length(), b.length()); i++) {
      if (a.charAt(i) != b.charAt(i)) break;
    }
    return a.substring(0, i);
  }
  
  /**
   * Inserts linebreaks into {@code message} and return the newly formatted
   * string.
   * 
   * @param message
   *        the string to be formatted
   * @param lineBreak
   *        the column at which the linebreak should occur at latest
   * @param lineBreakSymbol
   *        the string to use as linebreak
   * @param padString
   *        a padding string that will be placed at the start of each
   *        new line
   * @param breakBeforeLineBreak
   *        if {@code false}, breaks after a line is longer
   *        than {@code lineBreak} characters. If true, ensures that no line is
   *        longer than {@code lineBreak} characters, i.e., breaks before that
   *        number of chars.
   * @return
   */
  public static String insertLineBreaks(String message, int lineBreak,
    String lineBreakSymbol, String padString, boolean breakBeforeLineBreak) {
    
    StringBuilder sb = new StringBuilder();
    StringTokenizer st = new StringTokenizer(message != null ? message : "",
      " ");
    if (st.hasMoreElements()) {
      sb.append(st.nextElement().toString());
    }
    int length = sb.length();
    int pos;
    int count = 0;
    while (st.hasMoreElements()) {
      String tmp = st.nextElement().toString();
      
      if ((lineBreak < Integer.MAX_VALUE)
          && ((length >= lineBreak) || (breakBeforeLineBreak && (length + tmp
              .length()) >= lineBreak))) {
        sb.append(lineBreakSymbol);
        if (padString != null) {
          sb.append(padString);
        }
        count++;
        length = 0;
      } else {
        sb.append(' ');
      }
      
      // Append current element
      sb.append(tmp);
      
      // Change length
      if ((pos = tmp.indexOf(lineBreakSymbol)) >= 0) {
        length = tmp.length() - pos - lineBreakSymbol.length();
      } else {
        length += tmp.length() + 1;
      }
    }
    return sb.toString();
    
  }
  
  /**
   * Returns a string describing the allowed options in detail.
   * 
   * @return help information string.
   */
  public String getHelpMessage() {
    Record rec;
    NameDesc ndesc;
    boolean hasOneWordAlias = false;
    StringBuilder s = new StringBuilder();
    
    s.append(String.format("Usage: %s\n", synopsisString));
    s.append("Options include:\n\n");
    
    // iterate over all options in forms of records
    for (int i = 0; i < matchList.size(); i++) {
      StringBuilder optionInfo = new StringBuilder();
      rec = matchList.get(i);
      
      // if the record is not visible, skip it
      if (!rec.isVisible()) {
        continue;
      }
      
      // if the record represents the 'help' option, but help options are
      // disabled, skip it
      if ((rec.convertCode == 'h') && !helpOptionsEnabled) {
        continue;
      }
      
      // if the record is a delimiter, print it and continue to the next record
      if (rec.type == Record.DELIM) {
        s.append('\n');
        s.append(rec.nameList.name);
        s.append('\n');
        continue;
      }
      
      for (ndesc = rec.nameList; ndesc != null; ndesc = ndesc.next) {
        if (ndesc.oneWord) {
          hasOneWordAlias = true;
          break;
        }
      }
      String next = null;
      for (ndesc = rec.nameList; ndesc != null; ndesc = ndesc.next) {
        optionInfo.append(ndesc.name);
        if (hasOneWordAlias && !ndesc.oneWord) {
          optionInfo.append(' ');
        }
        if (ndesc.next != null) {
          next = ndesc.next.name;
          String prefix = getLongestCommonPrefix(next, ndesc.name);
          int lenDiffCurr = Math.abs(ndesc.name.length() - prefix.length());
          int lenDiffNext = Math.abs(next.length() - prefix.length());
          if (((lenDiffCurr == 1) || (lenDiffNext == 1))
              && (prefix.length() > 2)) {
            break;
          }
          optionInfo.append(", ");
        }
      }
      if (!hasOneWordAlias) {
        optionInfo.append(' ');
      }
      if (rec.convertCode != 'v' && rec.convertCode != 'h') {
        if (rec.valueDesc != null) {
          optionInfo.append(rec.valueDesc);
        } else {
          if (rec.rangeDesc != null) {
            optionInfo.append(String.format("<%s %s>", rec.valTypeName(),
              rec.rangeDesc));
          } else {
            optionInfo.append(String.format("<%s>", rec.valTypeName()));
          }
        }
      }
      if (rec.numValues > 1) {
        optionInfo.append('X');
        optionInfo.append(rec.numValues);
      }
      s.append(optionInfo.toString());
      if (rec.helpMsg.length() > 0) {
        int pad = helpIndent - optionInfo.length();
        if (pad < 2) {
          s.append('\n');
          pad = helpIndent;
        }
        s.append(spaceString(pad));
        s.append(insertLineBreaks(rec.helpMsg,
          getConsoleColumns() - helpIndent, "\n",
          String.format("%1$" + helpIndent + "s", ""), true));
      }
      s.append('\n');
    }
    return s.toString();
  }
  
  /**
   * Returns the parser's error message. This is automatically set whenever an
   * error is encountered in {@code matchArg} or {@code matchAllArgs},
   * and is automatically set to {@code null} at the beginning of these
   * methods.
   * 
   * @return error message
   */
  public String getErrorMessage() {
    return errMsg;
  }
  
  /**
   * Returns the value of an unmatched argument discovered {@link #matchArg
   * matchArg} or {@link #matchAllArgs(String[],int,int) matchAllArgs}. If there
   * was no unmatched argument, {@code null} is returned.
   * 
   * @return unmatched argument
   */
  public String getUnmatchedArgument() {
    return unmatchedArg;
  }
}
