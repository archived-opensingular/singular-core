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
package org.opensingular.singular.flow.core.renderer.bpmn.view.config;

import org.opensingular.singular.flow.core.renderer.bpmn.view.EventCharacteristic;
import org.opensingular.singular.flow.core.renderer.bpmn.view.EventNodeStyle;
import org.opensingular.singular.flow.core.renderer.bpmn.view.EventType;
import org.opensingular.singular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.singular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for EventNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Event Node")
public class EventNodeStyleConfiguration extends NodeStyleConfiguration<EventNodeStyle> {
  //region Properties

  @Label("Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final EventType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final void setType( EventType value ) {
    getStyleTemplate().setType(value);
  }

  @Label("Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final EventCharacteristic getCharacteristic() {
    return getStyleTemplate().getCharacteristic();
  }

  @Label("Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final void setCharacteristic( EventCharacteristic value ) {
    getStyleTemplate().setCharacteristic(value);
  }

  @Override
  protected EventNodeStyle createDefault() {
    return new EventNodeStyle();
  }
}
