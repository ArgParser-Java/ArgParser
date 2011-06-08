/* $Id$
 * $URL$
 * Copyright John E. Lloyd, 2004. All rights reserved. Permission to use,
 * copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 *
 * This  software is distributed "as is", without any warranty, including 
 * any implied warranty of merchantability or fitness for a particular
 * use. The author assumes no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software.
 */
package de.zbit.util.argparser;

/**
 * Gives a very simple example of the use of {@link ArgParser}.
 * 
 * @author John E. Lloyd
 * @author Andreas Dr&auml;ger
 * @since Fall 2004
 * @version $Rev$
 * @see ArgParser
 */
public class SimpleExample
{
	/**
	 * Run this to invoke command line parsing.
	 */
	public static void main (String[] args) 
	 {
	   // create holder objects for storing results ...
 
	   DoubleHolder theta = new DoubleHolder();
	   StringHolder fileName = new StringHolder();
	   BooleanHolder debug = new BooleanHolder();
 
	   // create the parser and specify the allowed options ...
 
	   ArgParser parser = new ArgParser("java argparser.SimpleExample");
	   parser.addOption ("-theta %f #theta value (in degrees)", theta); 
	   parser.addOption ("-file %s #name of the operating file", fileName);
	   parser.addOption ("-debug %v #enables display of debugging info",
			     debug);

	   // and then match the arguments

	   parser.matchAllArgs (args);

	   // now print out the values

	   System.out.println ("theta=" + theta.getValue());
	   System.out.println ("fileName=" + fileName.getValue());
	   System.out.println ("debug=" + debug.getValue());
	 }
}

