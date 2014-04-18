/*
 * Copyright 2004 by John E. Lloyd
 * Copyright 2011-2013 by Andreas Draeger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.argparser;

import java.io.IOException;

/**
 * Exception class used by {@code ArgParser} when command line arguments contain
 * an error.
 */
public class ArgParseException extends IOException {
  /**
   * Generated serial version identifier.
   */
  private static final long serialVersionUID = -8868205997224787923L;
  
  /**
   * Creates a new ArgParseException with the given message.
   * 
   * @param msg
   *        Exception message
   */
  public ArgParseException(String msg) {
    super(msg);
  }
  
  /**
   * Creates a new ArgParseException from the given argument and message.
   * 
   * @param arg
   *        Offending argument
   * @param msg
   *        Error message
   */
  public ArgParseException(String arg, String msg) {
    super(arg + ": " + msg);
  }
}
