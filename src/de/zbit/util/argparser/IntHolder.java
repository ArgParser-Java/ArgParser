/*
 * $Id$ $URL$ Copyright John E. Lloyd, 2004. All rights reserved. Permission to
 * use, copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 * 
 * This software is distributed "as is", without any warranty, including any
 * implied warranty of merchantability or fitness for a particular use. The
 * author assumes no responsibility for, and shall not be liable for, any
 * special, indirect, or consequential damages, or any damages whatsoever,
 * arising out of or in connection with the use of this software.
 */
package de.zbit.util.argparser;

/**
 * Wrapper class which ``holds'' an integer value, enabling methods to return
 * integer values through arguments.
 * 
 * @author John E. Lloyd
 * @author Andreas Dr&auml;ger
 * @since Fall 2004
 * @version $Rev$
 * @see ArgParser
 */
public class IntHolder extends ArgHolder<Integer> {
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -2941387101772143503L;
		
	/**
	 * Constructs a new <code>IntHolder</code> with an initial value of 0.
	 */
	public IntHolder() {
		this((Integer) null);
	}
	
	/**
	 * Constructs a new <code>IntHolder</code> with a specific initial value.
	 * 
	 * @param i
	 *        Initial {@link Integer} value.
	 */
	public IntHolder(Integer i) {
		super(i);
	}
	
}
