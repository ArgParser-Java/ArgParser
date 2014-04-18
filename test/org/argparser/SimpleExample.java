/*
 * Copyright 2004 by John E. Lloyd
 * Copyright 2011-2013 by Andreas Draeger
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

/**
 * Gives a very simple example of the use of {@link ArgParser}.
 * 
 * @see ArgParser
 */
public class SimpleExample {
  /**
   * Run this to invoke command line parsing.
   */
  public static void main(String[] args) {
    // create holder objects for storing results ...
    
    ArgHolder<Double> theta = new ArgHolder<Double>(Double.class);
    ArgHolder<String> fileName = new ArgHolder<String>(String.class);
    ArgHolder<Boolean> debug = new ArgHolder<Boolean>(Boolean.valueOf(false));
    
    // create the parser and specify the allowed options ...
    
    ArgParser parser = new ArgParser("java argparser.SimpleExample");
    parser.addOption("-theta %f #theta value (in degrees)", theta);
    parser.addOption("-file %s #name of the operating file", fileName);
    parser.addOption("-debug %v #enables display of debugging info", debug);
    
    // and then match the arguments
    
    parser.matchAllArgs(args);
    
    // now print out the values
    
    System.out.println("theta=" + theta.getValue());
    System.out.println("fileName=" + fileName.getValue());
    System.out.println("debug=" + debug.getValue());
  }
}
