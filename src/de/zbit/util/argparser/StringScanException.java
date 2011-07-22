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
  * Exception class used by {@link StringScanner} when
  * command line arguments do not parse correctly.
  * 
  * @author John E. Lloyd
  * @author Andreas Dr&auml;ger
  * @since Winter 2001
  * @version $Rev$
  * @see StringScanner
  */
class StringScanException extends IOException {
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = 1976498726950191808L;
	
	/**
	 * 
	 */
	int failIdx;
	
	/**
	 * Creates a new StringScanException with the given message.
	 * 
	 * @param msg
	 *        Error message
	 * @see StringScanner
	 */
	public StringScanException(String msg) {
		super(msg);
	}
	
	/**
	 * 
	 * @param idx
	 * @param msg
	 */
	public StringScanException(int idx, String msg) {
		super(msg);
		failIdx = idx;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFailIndex() {
		return failIdx;
	}
	
}
