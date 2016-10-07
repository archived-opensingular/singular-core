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
package org.opensingular.flow.core.renderer.bpmn.view.config;

import org.opensingular.flow.core.renderer.bpmn.view.BpmnEdgeStyle;
import org.opensingular.flow.core.renderer.bpmn.view.EdgeType;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.EnumValueAnnotation;
import org.opensingular.flow.core.renderer.toolkit.optionhandler.Label;

/**
 * Configuration class for BpmnEdgeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Bpmn Edge")
public class BpmnEdgeStyleConfiguration extends EdgeStyleConfiguration<BpmnEdgeStyle> {
  //region Properties

  @Label("Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final EdgeType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final void setType( EdgeType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected BpmnEdgeStyle createDefault() {
    return new BpmnEdgeStyle();
  }
}
