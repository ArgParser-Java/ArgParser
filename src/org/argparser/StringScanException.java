/*
 * Copyright 2004 by John E. Lloyd
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

import java.io.IOException;

/**
 * Exception class used by {@link StringScanner} when
 * command line arguments do not parse correctly.
 * 
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
