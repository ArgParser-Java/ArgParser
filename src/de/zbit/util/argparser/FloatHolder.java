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
 * Wrapper class which ``holds'' a float value, enabling methods to return float
 * values through arguments.
 * 
 * @author John E. Lloyd
 * @author Andreas Dr&auml;ger
 * @since Fall 2004
 * @version $Rev$
 * @see ArgParser
 */
public class FloatHolder extends ArgHolder<Float> {
	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -3915960841296052550L;
		
	/**
	 * Constructs a new <code>FloatHolder</code> with an initial value of 0.
	 */
	public FloatHolder() {
		super((Float) null);
	}
	
	/**
	 * Constructs a new <code>FloatHolder</code> with a specific initial value.
	 * 
	 * @param f
	 *        Initial {@link Float} value.
	 */
	public FloatHolder(Float f) {
		super(f);
	}
}
