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

import bpmn.view.GatewayNodeStyle;
import bpmn.view.GatewayType;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;

/**
 * Configuration class for GatewayNodeStyle option pane.
 *
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer applications
 * will likely provide their own property configuration framework and won't need this part of the library
 */
@Label("Gateway Node")
public class GatewayNodeStyleConfiguration extends NodeStyleConfiguration<GatewayNodeStyle> {
  //region Properties

  @Label("Gateway Type")
  @EnumValueAnnotation(label = "Exclusive Without Marker", value = "EXCLUSIVE_WITHOUT_MARKER")
  @EnumValueAnnotation(label = "Exclusive With Marker", value = "EXCLUSIVE_WITH_MARKER")
  @EnumValueAnnotation(label = "Inclusive", value = "INCLUSIVE")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Complex", value = "COMPLEX")
  @EnumValueAnnotation(label = "Event Based", value = "EVENT_BASED")
  @EnumValueAnnotation(label = "Exclusive Event Based", value = "EXCLUSIVE_EVENT_BASED")
  @EnumValueAnnotation(label = "Parallel Event Based", value = "PARALLEL_EVENT_BASED")
  public final GatewayType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Gateway Type")
  @EnumValueAnnotation(label = "Exclusive Without Marker", value = "EXCLUSIVE_WITHOUT_MARKER")
  @EnumValueAnnotation(label = "Exclusive With Marker", value = "EXCLUSIVE_WITH_MARKER")
  @EnumValueAnnotation(label = "Inclusive", value = "INCLUSIVE")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Complex", value = "COMPLEX")
  @EnumValueAnnotation(label = "Event Based", value = "EVENT_BASED")
  @EnumValueAnnotation(label = "Exclusive Event Based", value = "EXCLUSIVE_EVENT_BASED")
  @EnumValueAnnotation(label = "Parallel Event Based", value = "PARALLEL_EVENT_BASED")
  public final void setType( GatewayType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected GatewayNodeStyle createDefault() {
    return new GatewayNodeStyle();
  }
}
