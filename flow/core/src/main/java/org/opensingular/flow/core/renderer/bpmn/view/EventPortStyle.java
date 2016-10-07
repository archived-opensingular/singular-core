/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
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
 */
package org.opensingular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.DefaultValue;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.DefaultEdgePathCropper;
import com.yworks.yfiles.graph.styles.IEdgePathCropper;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.IPortStyleRenderer;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link IPortStyle} implementation representing an Event attached to an Activity boundary according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class EventPortStyle implements IPortStyle, Cloneable {
  private final IPortStyleRenderer renderer;

  //region Properties

  /**
   * Gets the event type for this style.
   * @return The Type.
   * @see #setType(EventType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "COMPENSATION")
  public final EventType getType() {
    return getEventNodeStyle().getType();
  }

  /**
   * Sets the event type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "COMPENSATION")
  public final void setType( EventType value ) {
    getEventNodeStyle().setType(value);
  }

  /**
   * Gets the event characteristic for this style.
   * @return The Characteristic.
   * @see #setCharacteristic(EventCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "BOUNDARY_INTERRUPTING")
  public final EventCharacteristic getCharacteristic() {
    return getEventNodeStyle().getCharacteristic();
  }

  /**
   * Sets the event characteristic for this style.
   * @param value The Characteristic to set.
   * @see #getCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "BOUNDARY_INTERRUPTING")
  public final void setCharacteristic( EventCharacteristic value ) {
    getEventNodeStyle().setCharacteristic(value);
  }

  /**
   * Gets the size the port style is rendered with.
   * @return The RenderSize.
   * @see #setRenderSize(SizeD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "20,20", classValue = SizeD.class)
  public final SizeD getRenderSize() {
    return getAdapter().getRenderSize();
  }

  /**
   * Sets the size the port style is rendered with.
   * @param value The RenderSize to set.
   * @see #getRenderSize()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "20,20", classValue = SizeD.class)
  public final void setRenderSize( SizeD value ) {
    getAdapter().setRenderSize(value);
  }

  //endregion

  private NodeStylePortStyleAdapter adapter;

  final NodeStylePortStyleAdapter getAdapter() {
    return this.adapter;
  }

  final void setAdapter( NodeStylePortStyleAdapter value ) {
    this.adapter = value;
  }

  /**
   * Creates a new instance.
   */
  public EventPortStyle() {
    EventNodeStyle newInstance = new EventNodeStyle();
      newInstance.setCharacteristic(EventCharacteristic.BOUNDARY_INTERRUPTING);
      newInstance.setType(EventType.COMPENSATION);

    NodeStylePortStyleAdapter newInstance2 = new NodeStylePortStyleAdapter(newInstance);
      newInstance2.setRenderSize(BpmnConstants.Sizes.EVENT_PORT);
    setAdapter(newInstance2);
    renderer = EventPortStyleRenderer.INSTANCE;
  }

  final EventNodeStyle getEventNodeStyle() {
    INodeStyle nodeStyle = getAdapter().getNodeStyle();
    return (nodeStyle instanceof EventNodeStyle) ? (EventNodeStyle)nodeStyle : null;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final EventPortStyle clone() {
    try {
      return (EventPortStyle)super.clone();
    }catch (CloneNotSupportedException exception) {
      throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
    }
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IPortStyleRenderer getRenderer() {
    return renderer;
  }

  /**
   * Renderer used by {@link EventPortStyle}.
   */
  private static class EventPortStyleRenderer implements IPortStyleRenderer, ILookup {
    public static final EventPortStyleRenderer INSTANCE = new EventPortStyleRenderer();

    private ILookup fallbackLookup;

    public final IVisualCreator getVisualCreator( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getVisualCreator(item, adapter);
    }

    public final IBoundsProvider getBoundsProvider( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getBoundsProvider(item, adapter);
    }

    public final IVisibilityTestable getVisibilityTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getVisibilityTestable(item, adapter);
    }

    public final IHitTestable getHitTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getHitTestable(item, adapter);
    }

    public final IMarqueeTestable getMarqueeTestable( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      return adapter.getRenderer().getMarqueeTestable(item, adapter);
    }

    public final ILookup getContext( IPort item, IPortStyle style ) {
      NodeStylePortStyleAdapter adapter = ((EventPortStyle)style).getAdapter();
      fallbackLookup = adapter.getRenderer().getContext(item, adapter);
      return this;
    }

    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == IEdgePathCropper.class) {
        return (TLookup) EventPortEdgePathCropper.CALCULATOR_INSTANCE;
      }
      return fallbackLookup.lookup(type);
    }
  }

  /**
   * IEdgePathCropper instance that crops the edge at the circular port bounds.
   */
  private static class EventPortEdgePathCropper extends DefaultEdgePathCropper {
    public static final EventPortEdgePathCropper CALCULATOR_INSTANCE = new EventPortEdgePathCropper();

    private EventPortEdgePathCropper() {
      setCroppingAtPortEnabled(true);
    }

    @Override
    protected IShapeGeometry getPortGeometry( IPort port ) {
      IPortStyle style = port.getStyle();
      if (style instanceof EventPortStyle) {
        EventPortStyle eventPortStyle = (EventPortStyle) style;
        IPortStyleRenderer renderer = eventPortStyle.getAdapter().getRenderer();
        return (renderer instanceof IShapeGeometry) ? (IShapeGeometry)renderer : null;
      }
      return null;
    }
  }

}
