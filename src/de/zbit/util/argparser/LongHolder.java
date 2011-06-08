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
 * Wrapper class which ``holds'' a long value, enabling methods to return long
 * values through arguments.
 * 
 * @author John E. Lloyd
 * @author Andreas Dr&auml;ger
 * @since Fall 2004
 * @version $Rev$
 * @see ArgParser
 */
public class LongHolder extends ArgHolder<Long> {
	
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -5427370814272954509L;
	
	/**
	 * Constructs a new <code>LongHolder</code> with an initial value of null.
	 */
	public LongHolder() {
		this((Long) null);
	}
	
	/**
	 * Constructs a new <code>LongHolder</code> with a specific initial value.
	 * 
	 * @param l
	 *        Initial long value.
	 */
	public LongHolder(Long l) {
		super(l);
	}

}
