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

import java.io.IOException;

/** 
  * Exception class used by <code>ArgParser</code> when
  * command line arguments contain an error.
  * 
  * @author John E. Lloyd
  * @author Andreas Dr&auml;ger
  * @since Fall 2004
  * @version $Rev$
  */
public class ArgParseException extends IOException
{
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -8868205997224787923L;

	/** 
	  * Creates a new ArgParseException with the given message. 
	  * 
	  * @param msg Exception message
	  */
	public ArgParseException (String msg)
	 { super (msg);
	 }

	/** 
	  * Creates a new ArgParseException from the given
	  * argument and message. 
	  * 
	  * @param arg Offending argument
	  * @param msg Error message
	  */
	public ArgParseException (String arg, String msg)
	 { super (arg + ": " + msg);
	 }
}
