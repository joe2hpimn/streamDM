/*
 * Copyright (C) 2015 Holmes Team at HUAWEI Noah's Ark Lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.spark.streamdm.core

/**
 * An Example is a wrapper on top of the Instance class hierarchy. It
 * contains a reference to an input Instance and an output Instance, and provides every method that the
 * instance provides. This is done so that the DStream accepts any type on
 * Instance in the parameters, and that the same DStream contains multiple types
 * of Instance. Every operation and class -- except Reader classes -- operate on
 * Example instead on Instance.
 */

class Example(inInstance: Instance, outInstance: Instance) 
  extends Serializable {
  
  val in = inInstance
  val out = outInstance

  /** Get the feature value present at position index
   *
   * @param index the index of the features
   * @return a Double representing the feature value
   */
  def featureAt(index: Int): Double = in(index)
  
  /** Get the class value present at position index
   *
   * @param index the index of the class
   * @return a Double representing the value for the class
   */
  def labelAt(index: Int): Double = out(index)

    /** Add a feature to the instance in the example
   *
   * @param index the index at which the value is added
   * @param input the feature value which is added up
   * @return an Example containing an Instance with the new features
   */
  def setFeature(index: Int, input: Double): Example =
    new Example(in.set(index, input),out)
  
  /** Perform a dot product between two instances
   *
   * @param input an Example with which the dot product is performed
   * @return a Double representing the dot product 
   */
  def dot(input: Instance): Double = in.dot(input)

  /** Perform an element by element addition between two Examples
   *
   * @param input an Instance which is added up
   * @return an Instance representing the added Instances
   */
  def add(input: Instance): Instance = in.add(input)

  /** Apply an operation to every feature of the input Instance contained in the
   * Example (essentially a map)
   *
   * @param func the function for the transformation
   * @return a new Example with the transformed input features
   */
  def mapFeatures(func: Double=>Double): Example = 
    new Example(in.map(func),out)

  override def toString = "%s %s".format(out.toString, in.toString)
}

object Example extends Serializable {
  
  /** Parse the input string as an SparseInstance class
   *
   * @param input the String line to be read, in LibSVM format
   * @return a DenseInstance which is parsed from input
   */
  def parse(input: String, inType: String, outType: String): Example = {
    val tokens = input.split("\\s+")
    new Example(getInstance(tokens.tail.head, inType),
                getInstance(tokens.head, outType))
  }

  private def getInstance(input: String, instType: String): Instance = 
    instType match {
      case "dense" => DenseInstance.parse(input)
      case "sparse" => SparseInstance.parse(input)
      case _ => null
    }
}
