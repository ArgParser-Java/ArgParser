/*
 * Copyright 2011-2013 by Andreas Draeger and Florian Mittag
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

import java.io.Serializable;

/**
 * A carrier for a certain value of the given generic type.
 * 
 * @see ArgParser
 */
public class ArgHolder<V> implements Cloneable, Serializable {
  
  /**
   * Generated serial version identifier.
   */
  private static final long serialVersionUID = 5362889114277649711L;
  
  /**
   * Default: {@code false}.
   * 
   * @return
   */
  public static final ArgHolder<Boolean> createBooleanHolder() {
    return new ArgHolder<Boolean>(Boolean.FALSE);
  }
  
  /**
   * Default: {@code NaN}
   * 
   * @return
   */
  public static final ArgHolder<Double> createDoubleHolder() {
    return new ArgHolder<Double>(Double.NaN);
  }
  
  /**
   * Default: {@code NaN}
   * 
   * @return
   */
  public static final ArgHolder<Float> createFloatHolder() {
    return new ArgHolder<Float>(Float.NaN);
  }
  
  /**
   * Default: {@code null}
   * 
   * @return
   */
  public static final ArgHolder<Integer> createIntHolder() {
    return new ArgHolder<Integer>(Integer.class);
  }
  
  /**
   * Default: {@code null}
   * 
   * @return
   */
  public static final ArgHolder<Long> createLongHolder() {
    return new ArgHolder<Long>(Long.class);
  }
  
  /**
   * Default: {@code null}
   * 
   * @return
   */
  public static final ArgHolder<Short> createShortHolder() {
    return new ArgHolder<Short>(Short.class);
  }
  
  /**
   * Default: {@code null}
   * 
   * @return
   */
  public static final ArgHolder<String> createStringHolder() {
    return new ArgHolder<String>(String.class);
  }
  
  /**
   * @param def
   *        default parameter, must not be null
   * @return
   */
  public static final ArgHolder<String> createStringHolder(String def) {
    return new ArgHolder<String>(def);
  }
  
  /**
   * The type of argument.
   */
  private Class<V> clazz;
  
  /**
	 * 
	 */
  private V defaultValue;
  
  /**
   * Value of the {@link Object} reference, set and examined by the application
   * as needed.
   */
  private V value;
  
  /**
   * Clone constructor.
   * 
   * @param valueHolder
   */
  public ArgHolder(ArgHolder<V> valueHolder) {
    this(valueHolder.getValue());
  }
  
  /**
   * Creates a new {@link ArgHolder} of the given type ({@link Class}) whose
   * value is set to null.
   */
  public ArgHolder(Class<V> clazz) {
    this.clazz = clazz;
    this.defaultValue = this.value = null;
  }
  
  /**
   * Creates a new {@link ArgHolder} object with the given value, whose type is
   * set to the {@link Class} of the given value.
   * 
   * @param defaultValue
   *        must not be null. If this value is null, please use the constructor
   *        that accepts an instance of {@link Class}.
   * @see #ArgHolder(Class)
   */
  @SuppressWarnings("unchecked")
  public ArgHolder(V defaultValue) {
    this((Class<V>) defaultValue.getClass());
    this.defaultValue = defaultValue;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public ArgHolder<V> clone() {
    return new ArgHolder<V>(this);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) { return true; }
    if (o.getClass().equals(ArgHolder.class)) {
      ArgHolder<?> v = (ArgHolder<?>) o;
      boolean equal = getType().equals(v.getType());
      equal &= v.isSetValue() == isSetValue();
      if (equal && isSetValue()) {
        equal &= v.getValue().equals(getValue());
      }
      equal &= v.isSetDefaultValue() == isSetDefaultValue();
      if (equal && isSetDefaultValue()) {
        equal &= v.getDefaultValue().equals(getDefaultValue());
      }
      return equal;
    }
    return false;
  }
  
  /**
   * @return
   */
  public V getDefaultValue() {
    return defaultValue;
  }
  
  /**
   * 
   * @return
   */
  public Class<V> getType() {
    return clazz;
  }
  
  /**
   * 
   * @return the {@link #value}.
   */
  public V getValue() {
    return value;
  }
  
  /**
   * @return the {@link #value} if it is set, the default otherwise
   */
  public V getFinalValue() {
    if (isSetValue()) { return value; }
    return defaultValue;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int hash = super.hashCode() + 7;
    hash += clazz.hashCode() + (isSetValue() ? value.hashCode() : 0);
    hash += (defaultValue != null ? defaultValue.hashCode() : 0);
    return hash;
  }
  
  /**
   * @return
   */
  public boolean isSetDefaultValue() {
    return defaultValue != null;
  }
  
  /**
   * Check if a value has been set.
   * 
   * @return {@code true} if the {@link #value} in this {@link ArgHolder} is not
   *         null, {@code false} otherwise.
   */
  public boolean isSetValue() {
    return value != null;
  }
  
  /**
   * 
   * @param value
   */
  public void setValue(V value) {
    this.value = value;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(),
      (getValue() == null) ? "null" : getValue().toString());
  }
  
  /**
   * @see #setValue(Object)
   */
  public void unsetValue() {
    setValue(null);
  }
  
}
