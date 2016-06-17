/****************************************************************************
 **
 ** This demo file is part of yFiles for Java 3.0.0.1.
 **
 ** Copyright (c) 2000-2016 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package bpmn.view.config;

import bpmn.view.ChoreographyNodeStyle;
import bpmn.view.ChoreographyType;
import bpmn.view.LoopCharacteristic;
import bpmn.view.SubState;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;

/**
 * Configuration class for ChoreographyNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Choreography Node")
public class ChoreographyNodeStyleConfiguration extends NodeStyleConfiguration<ChoreographyNodeStyle> {
  //region Properties

  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final ChoreographyType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final void setType( ChoreographyType value ) {
    getStyleTemplate().setType(value);
  }

  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final LoopCharacteristic getLoopCharacteristic() {
    return getStyleTemplate().getLoopCharacteristic();
  }

  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    getStyleTemplate().setLoopCharacteristic(value);
  }

  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final SubState getSubState() {
    return getStyleTemplate().getSubState();
  }

  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final void setSubState( SubState value ) {
    getStyleTemplate().setSubState(value);
  }

  @Label("Initiating Message")
  public final boolean isInitiatingMessage() {
    return getStyleTemplate().isInitiatingMessage();
  }

  @Label("Initiating Message")
  public final void setInitiatingMessage( boolean value ) {
    getStyleTemplate().setInitiatingMessage(value);
  }

  @Label("Response Message")
  public final boolean isResponseMessage() {
    return getStyleTemplate().isResponseMessage();
  }

  @Label("Response Message")
  public final void setResponseMessage( boolean value ) {
    getStyleTemplate().setResponseMessage(value);
  }

  @Label("Initiating At Top")
  public final boolean isInitiatingAtTop() {
    return getStyleTemplate().isInitiatingAtTop();
  }

  @Label("Initiating At Top")
  public final void setInitiatingAtTop( boolean value ) {
    getStyleTemplate().setInitiatingAtTop(value);
  }

  @Override
  protected ChoreographyNodeStyle createDefault() {
    return new ChoreographyNodeStyle();
  }
}
