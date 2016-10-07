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
import com.yworks.yfiles.annotations.SerializationVisibility;
import com.yworks.yfiles.annotations.SerializationVisibilityType;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyleRenderer;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IEditLabelHelper;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.LabelEditingEventArgs;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Choreography according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyNodeStyle extends BpmnNodeStyle {
  //region Initialize static fields

  private static final ShapeNodeStyle SNS;

  private static final IIcon TOP_INITIATING_MESSAGE_ICON;

  private static final IIcon BOTTOM_RESPONSE_MESSAGE_ICON;

  private static final IIcon BOTTOM_INITIATING_MESSAGE_ICON;

  private static final IIcon TOP_RESPONSE_MESSAGE_ICON;

  private static final IIcon TASK_BAND_BACKGROUND_ICON;

  private static final IIcon MULTI_INSTANCE_ICON;

  private static final int MESSAGE_DISTANCE = 15;

  //endregion

  //region Properties

  private ChoreographyType type;

  /**
   * Gets the choreography type of this style.
   * @return The Type.
   * @see #setType(ChoreographyType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChoreographyType.class, stringValue = "TASK")
  public final ChoreographyType getType() {
    return type;
  }

  /**
   * Sets the choreography type of this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChoreographyType.class, stringValue = "TASK")
  public final void setType( ChoreographyType value ) {
    if (type != value || outlineIcon == null) {
      incrementModCount();
      type = value;
      outlineIcon = IconFactory.createChoreography(type);
    }
  }

  private LoopCharacteristic loopCharacteristic;

  /**
   * Gets the loop characteristic of this style.
   * @return The LoopCharacteristic.
   * @see #setLoopCharacteristic(LoopCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final LoopCharacteristic getLoopCharacteristic() {
    return loopCharacteristic;
  }

  /**
   * Sets the loop characteristic of this style.
   * @param value The LoopCharacteristic to set.
   * @see #getLoopCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    if (loopCharacteristic != value) {
      incrementModCount();
      loopCharacteristic = value;
      loopIcon = IconFactory.createLoopCharacteristic(value);
    }
  }

  private SubState subState;

  /**
   * Gets the sub state of this style.
   * @return The SubState.
   * @see #setSubState(SubState)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final SubState getSubState() {
    return subState;
  }

  /**
   * Sets the sub state of this style.
   * @param value The SubState to set.
   * @see #getSubState()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final void setSubState( SubState value ) {
    if (subState != value) {
      incrementModCount();
      subState = value;
    }
  }

  private boolean initiatingMessage;

  /**
   * Gets whether the initiating message icon is displayed.
   * @return The InitiatingMessage.
   * @see #setInitiatingMessage(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiatingMessage() {
    return initiatingMessage;
  }

  /**
   * Sets whether the initiating message icon is displayed.
   * @param value The InitiatingMessage to set.
   * @see #isInitiatingMessage()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiatingMessage( boolean value ) {
    if (initiatingMessage != value) {
      incrementModCount();
      initiatingMessage = value;
    }
  }

  private boolean responseMessage;

  /**
   * Gets whether the response message icon is displayed.
   * @return The ResponseMessage.
   * @see #setResponseMessage(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isResponseMessage() {
    return responseMessage;
  }

  /**
   * Sets whether the response message icon is displayed.
   * @param value The ResponseMessage to set.
   * @see #isResponseMessage()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setResponseMessage( boolean value ) {
    if (responseMessage != value) {
      incrementModCount();
      responseMessage = value;
    }
  }

  private boolean initiatingAtTop = true;

  /**
   * Gets whether the initiating message icon or the response message icon is displayed on top of the node while the other
   * one is at the bottom side.
   * <p>
   * Whether the initiating and response message icons are displayed at all depends on {@link #isInitiatingMessage() InitiatingMessage}
   * and
   * {@link #isResponseMessage() ResponseMessage}. This property only determines which one is displayed on which side of
   * the node.
   * </p>
   * @return The InitiatingAtTop.
   * @see #setInitiatingAtTop(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiatingAtTop() {
    return initiatingAtTop;
  }

  /**
   * Sets whether the initiating message icon or the response message icon is displayed on top of the node while the other
   * one is at the bottom side.
   * <p>
   * Whether the initiating and response message icons are displayed at all depends on {@link #isInitiatingMessage() InitiatingMessage}
   * and
   * {@link #isResponseMessage() ResponseMessage}. This property only determines which one is displayed on which side of
   * the node.
   * </p>
   * @param value The InitiatingAtTop to set.
   * @see #isInitiatingAtTop()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiatingAtTop( boolean value ) {
    if (initiatingAtTop != value) {
      initiatingAtTop = value;
      if (isInitiatingMessage() || isResponseMessage()) {
        incrementModCount();
      }
    }
  }

  private ParticipantList topParticipants = new ParticipantList();

  /**
   * Gets the list of {@link Participant}s at the top of the node, ordered from top to bottom.
   * @return The TopParticipants.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @SerializationVisibility(SerializationVisibilityType.CONTENT)
  public final Collection<Participant> getTopParticipants() {
    return topParticipants;
  }

  private ParticipantList bottomParticipants = new ParticipantList();

  /**
   * Gets the list of {@link Participant}s at the bottom of the node, ordered from bottom to top.
   * @return The BottomParticipants.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @SerializationVisibility(SerializationVisibilityType.CONTENT)
  public final Collection<Participant> getBottomParticipants() {
    return bottomParticipants;
  }

  /**
   * Gets the insets for the task name band of the given item.
   * <p>
   * These insets are extended by the sizes of the participant bands on top and bottom side and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @return The Insets.
   * @see INodeInsetsProvider
   * @see #setInsets(InsetsD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "5", classValue = InsetsD.class)
  public final InsetsD getInsets() {
    return insets;
  }

  /**
   * Sets the insets for the task name band of the given item.
   * <p>
   * These insets are extended by the sizes of the participant bands on top and bottom side and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @param value The Insets to set.
   * @see INodeInsetsProvider
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "5", classValue = InsetsD.class)
  public final void setInsets( InsetsD value ) {
    insets = value;
  }

  private boolean isShowTopMessage() {
    return (isInitiatingMessage() && isInitiatingAtTop()) || (isResponseMessage() && !isInitiatingAtTop());
  }

  private boolean isShowBottomMessage() {
    return (isInitiatingMessage() && !isInitiatingAtTop()) || (isResponseMessage() && isInitiatingAtTop());
  }

  //endregion

  private IIcon outlineIcon;

  private IIcon loopIcon;

  private InsetsD insets = new InsetsD(5);

  /**
   * Creates a new instance.
   */
  public ChoreographyNodeStyle() {
    setType(ChoreographyType.TASK);
    setMinimumSize(new SizeD(30, 30));
    setLoopCharacteristic(LoopCharacteristic.NONE);
    setSubState(SubState.NONE);
  }

  //region IVisualCreator methods

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual createVisual( IRenderContext context, INode node ) {
    RectD bounds = node.getLayout().toRectD();
    ChoreographyContainer container = new ChoreographyContainer();

    // task band
    TaskBandContainer taskBandContainer = new TaskBandContainer();
    IIcon bandIcon = createTaskBandIcon(node);
    bandIcon.setBounds(getRelativeTaskNameBandBounds(node));
    taskBandContainer.add(bandIcon.createVisual(context));
    taskBandContainer.setIcon(bandIcon);
    container.getChildren().add(taskBandContainer);

    ArrayList<IIcon> tpi = new ArrayList<>();
    // top participants
    double topOffset = 0;
    boolean first = true;
    for (Participant participant : topParticipants) {
      IIcon participantIcon = createParticipantIcon(participant, true, first);
      tpi.add(participantIcon);
      double height = participant.getSize();
      participantIcon.setBounds(new RectD(0, topOffset, bounds.width, height));
      container.add(participantIcon.createVisual(context));
      topOffset += height;
      first = false;
    }

    ArrayList<IIcon> bpi = new ArrayList<>();
    // bottom participants
    double bottomOffset = bounds.height;
    first = true;
    for (Participant participant : bottomParticipants) {
      IIcon participantIcon = createParticipantIcon(participant, false, first);
      bpi.add(participantIcon);
      double height = participant.getSize();
      bottomOffset -= height;
      participantIcon.setBounds(new RectD(0, bottomOffset, bounds.width, height));
      container.add(participantIcon.createVisual(context));
      first = false;
    }

    // outline
    outlineIcon.setBounds(new RectD(PointD.ORIGIN, bounds.getSize()));
    container.add(outlineIcon.createVisual(context));

    // messages
    if (isInitiatingMessage()) {
      IIcon initiatingMessageIcon = isInitiatingAtTop() ? TOP_INITIATING_MESSAGE_ICON : BOTTOM_INITIATING_MESSAGE_ICON;
      initiatingMessageIcon.setBounds(new RectD(0, 0, bounds.width, bounds.height));
      container.add(initiatingMessageIcon.createVisual(context));
    }
    if (isResponseMessage()) {
      IIcon responseMessageIcon = isInitiatingAtTop() ? BOTTOM_RESPONSE_MESSAGE_ICON : TOP_RESPONSE_MESSAGE_ICON;
      responseMessageIcon.setBounds(new RectD(0, 0, bounds.width, bounds.height));
      container.add(responseMessageIcon.createVisual(context));
    }

    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));

    container.setModCount(getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount());
    container.setBounds(bounds);
    container.setBottomParticipantIcons(bpi);
    container.setTopParticipantIcons(tpi);
    return container;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    ChoreographyContainer container = (oldVisual instanceof ChoreographyContainer) ? (ChoreographyContainer)oldVisual : null;

    int currentModCount = getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount();
    if (container == null || container.getModCount() != currentModCount) {
      return createVisual(context, node);
    }

    RectD newBounds = node.getLayout().toRectD();

    if (RectD.equals(container.getBounds(), newBounds)) {
      return container;
    }

    if (container.getBounds().width != newBounds.width || container.getBounds().height != newBounds.height) {
      // update icon bounds
      int childIndex = 0;
      IVisual v = container.getChildren().get(childIndex++);
      // task band
      TaskBandContainer taskBandContainer = (v instanceof TaskBandContainer) ? (TaskBandContainer)v : null;

      IIcon taskBandIcon = taskBandContainer != null?taskBandContainer.getIcon():null;
      RectD taskBandBounds = getRelativeTaskNameBandBounds(node);

      if (taskBandIcon != null && taskBandContainer.getChildren().size() == 1) {
        taskBandIcon.setBounds(taskBandBounds);
        updateChildVisual(context, taskBandContainer, 0, taskBandIcon);
      }

      // top participants
      double topOffset = 0;
      for (int i = 0; i < topParticipants.size(); i++) {
        Participant participant = topParticipants.get(i);
        IIcon participantIcon = container.getTopParticipantIcons().get(i);
        double height = participant.getSize();
        participantIcon.setBounds(new RectD(0, topOffset, newBounds.width, height));
        updateChildVisual(context, container, childIndex++, participantIcon);
        topOffset += height;
      }

      // bottom participants
      double bottomOffset = newBounds.height;
      for (int i = 0; i < bottomParticipants.size(); i++) {
        Participant participant = bottomParticipants.get(i);
        IIcon participantIcon = container.getBottomParticipantIcons().get(i);
        double height = participant.getSize();
        bottomOffset -= height;
        participantIcon.setBounds(new RectD(0, bottomOffset, newBounds.width, height));
        updateChildVisual(context, container, childIndex++, participantIcon);
      }

      // outline
      outlineIcon.setBounds(new RectD(PointD.ORIGIN, newBounds.getSize()));
      updateChildVisual(context, container, childIndex++, outlineIcon);

      // messages
      if (isInitiatingMessage()) {
        IIcon initiatingMessageIcon = isInitiatingAtTop() ? TOP_INITIATING_MESSAGE_ICON : BOTTOM_INITIATING_MESSAGE_ICON;
        initiatingMessageIcon.setBounds(new RectD(0, 0, newBounds.width, newBounds.height));
        updateChildVisual(context, container, childIndex++, initiatingMessageIcon);
      }
      if (isResponseMessage()) {
        IIcon responseMessageIcon = isInitiatingAtTop() ? BOTTOM_RESPONSE_MESSAGE_ICON : TOP_RESPONSE_MESSAGE_ICON;
        responseMessageIcon.setBounds(new RectD(0, 0, newBounds.width, newBounds.height));
        updateChildVisual(context, container, childIndex++, responseMessageIcon);
      }
    }

    container.setModCount(getModCount() + topParticipants.getModCount() + bottomParticipants.getModCount());
    container.setBounds(newBounds);
    container.getTransform().setToTranslation(newBounds.getX(), newBounds.getY());
    return container;
  }

  private IIcon createTaskBandIcon( INode node ) {
    IIcon collapseIcon = null;
    if (getSubState() != SubState.NONE) {
      collapseIcon = getSubState() == SubState.DYNAMIC ? IconFactory.createDynamicSubState(node) : IconFactory.createStaticSubState(getSubState());
    }

    IIcon markerIcon = null;
    if (loopIcon != null && collapseIcon != null) {
      markerIcon = IconFactory.createLineUpIcon(Arrays.asList(loopIcon, collapseIcon), BpmnConstants.Sizes.MARKER, 5);
    } else if (loopIcon != null) {
      markerIcon = loopIcon;
    } else if (collapseIcon != null) {
      markerIcon = collapseIcon;
    }
    if (markerIcon != null) {
      IIcon placedMarkers = IconFactory.createPlacedIcon(markerIcon, BpmnConstants.Placements.CHOROGRAPHY_MARKER, BpmnConstants.Sizes.MARKER);
      return IconFactory.createCombinedIcon(Arrays.asList(TASK_BAND_BACKGROUND_ICON, placedMarkers));
    } else {
      return TASK_BAND_BACKGROUND_ICON;
    }
  }

  private IIcon createParticipantIcon( Participant participant, boolean top, boolean isFirst ) {
    boolean isInitializing = isFirst && (top ^ !isInitiatingAtTop());

    IIcon icon = IconFactory.createChoreographyParticipant(isInitializing, top && isFirst ? BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS : 0, !top && isFirst ? BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS : 0);
    if (participant.isMultiInstance()) {
      icon = IconFactory.createCombinedIcon(Arrays.asList(icon, MULTI_INSTANCE_ICON));
    }
    return icon;
  }

  private static void updateChildVisual( IRenderContext context, VisualGroup container, int index, IVisualCreator icon ) {
    IVisual oldPathVisual = container.getChildren().get(index);
    IVisual newPathVisual = icon.updateVisual(context, oldPathVisual);
    if (!oldPathVisual.equals(newPathVisual)) {
      newPathVisual = newPathVisual != null ? newPathVisual : new VisualGroup();
      container.getChildren().remove(oldPathVisual);
      container.getChildren().add(index, newPathVisual);
    }
  }

  //endregion

  /**
   * Returns the participant at the specified location.
   * @param node The node whose bounds shall be used.
   * @param location The location of the participant.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final Participant getParticipant( INode node, PointD location ) {
    RectD layout = node.getLayout().toRectD();
    if (!layout.contains(location)) {
      return null;
    }

    double relativeY = (PointD.subtract(location, layout.getTopLeft())).y;
    if (relativeY < topParticipants.getHeight()) {
      for (Participant participant : getTopParticipants()) {
        double size = participant.getSize();
        if (relativeY < size) {
          return participant;
        }
        relativeY -= size;
      }
    } else if (layout.getHeight() - bottomParticipants.getHeight() < relativeY) {
      double yFromBottom = layout.getHeight() - relativeY;
      for (Participant participant : getBottomParticipants()) {
        double size = participant.getSize();
        if (yFromBottom < size) {
          return participant;
        }
        yFromBottom -= size;
      }
    }

    return null;
  }

  /**
   * Returns the bounds of the specified participant band.
   * @param owner The node whose bounds shall be used.
   * @param index The index of the participant in its list.
   * @param top Whether the top of bottom list of participants shall be used.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final RectD getParticipantBandBounds( INode owner, int index, boolean top ) {
    RectD layout = owner.getLayout().toRectD();
    double width = layout.getWidth();
    if (top && index <= topParticipants.size()) {
      int i = 0;
      double yOffset = 0;
      for (Participant topParticipant : topParticipants) {
        if (index == i++) {
          return new RectD(layout.getX(), layout.getY() + yOffset, width, topParticipant.getSize());
        } else {
          yOffset += topParticipant.getSize();
        }
      }
    } else if (!top && index < bottomParticipants.size()) {
      int i = 0;
      double yOffset = layout.getHeight();
      for (Participant bottomParticipant : bottomParticipants) {
        yOffset -= bottomParticipant.getSize();
        if (index == i++) {
          return new RectD(layout.getX(), layout.getY() + yOffset, width, bottomParticipant.getSize());
        }
      }
    }
    return getTaskNameBandBounds(owner);
  }

  /**
   * Returns the bounds of the task name band.
   * @param owner The node whose bounds shall be used.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final RectD getTaskNameBandBounds( INode owner ) {
    return getRelativeTaskNameBandBounds(owner).getTranslated(owner.getLayout().getTopLeft());
  }

  private RectD getRelativeTaskNameBandBounds( INode owner ) {
    double topHeight = topParticipants.getHeight();
    return new RectD(0, topHeight, owner.getLayout().getWidth(), Math.max(0, owner.getLayout().getHeight() - topHeight - bottomParticipants.getHeight()));
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    SNS.getRenderer().getShapeGeometry(node, SNS);
    GeneralPath tmp = SNS.getRenderer().getOutline();
    GeneralPath path = tmp != null ? tmp : new GeneralPath();

    RectD layout = node.getLayout().toRectD();

    if (isShowTopMessage()) {
      SizeD topBoxSize = BpmnConstants.Sizes.MESSAGE;
      double cx = layout.getCenter().x;
      double topBoxMaxY = layout.getY() - MESSAGE_DISTANCE;
      path.moveTo(cx - topBoxSize.width / 2, layout.getY());
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY);
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY - topBoxSize.height);
      path.lineTo(cx + topBoxSize.width / 2, topBoxMaxY - topBoxSize.height);
      path.lineTo(cx + topBoxSize.width / 2, topBoxMaxY);
      path.lineTo(cx - topBoxSize.width / 2, topBoxMaxY);
      path.close();
    }

    if (isShowBottomMessage()) {
      SizeD bottomBoxSize = BpmnConstants.Sizes.MESSAGE;
      double cx = layout.getCenter().x;
      double bottomBoxY = layout.getMaxY() + MESSAGE_DISTANCE;
      path.moveTo(cx - bottomBoxSize.width / 2, layout.getMaxY());
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY);
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY + bottomBoxSize.height);
      path.lineTo(cx + bottomBoxSize.width / 2, bottomBoxY + bottomBoxSize.height);
      path.lineTo(cx + bottomBoxSize.width / 2, bottomBoxY);
      path.lineTo(cx - bottomBoxSize.width / 2, bottomBoxY);
      path.close();
    }

    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD p, INode node ) {
    if (SNS.getRenderer().getHitTestable(node, SNS).isHit(context, p)) {
      return true;
    }
    RectD layout = node.getLayout().toRectD();

    if (isShowTopMessage()) {
      double cx = layout.getCenter().x;
      SizeD topBoxSize = BpmnConstants.Sizes.MESSAGE;
      RectD messageRect = new RectD(new PointD(cx - topBoxSize.width / 2, layout.getY() - MESSAGE_DISTANCE - topBoxSize.height), topBoxSize);
      if (messageRect.contains(p, context.getHitTestRadius())) {
        return true;
      }
      if (Math.abs(p.x - cx) < context.getHitTestRadius() && layout.getY() - MESSAGE_DISTANCE - context.getHitTestRadius() < p.y && p.y < layout.getY() + context.getHitTestRadius()) {
        return true;
      }
    }

    if (isShowBottomMessage()) {
      SizeD bottomBoxSize = BpmnConstants.Sizes.MESSAGE;
      double cx = layout.getCenter().x;
      RectD messageRect = new RectD(new PointD(cx - bottomBoxSize.width / 2, layout.getMaxY() + MESSAGE_DISTANCE), bottomBoxSize);
      if (messageRect.contains(p, context.getHitTestRadius())) {
        return true;
      }
      if (Math.abs(p.x - cx) < context.getHitTestRadius() && layout.getMaxY() - context.getHitTestRadius() < p.y && p.y < layout.getMaxY() + MESSAGE_DISTANCE + context.getHitTestRadius()) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected RectD getBounds( ICanvasContext context, INode node ) {
    RectD bounds = node.getLayout().toRectD();
    if (isShowTopMessage()) {
      bounds = bounds.getEnlarged(InsetsD.fromLTRB(0, MESSAGE_DISTANCE + BpmnConstants.Sizes.MESSAGE.height, 0, 0));
    }
    if (isShowBottomMessage()) {
      bounds = bounds.getEnlarged(InsetsD.fromLTRB(0, 0, 0, MESSAGE_DISTANCE + BpmnConstants.Sizes.MESSAGE.height));
    }

    return bounds;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Object lookup( INode node, Class type ) {
    if (type == INodeSizeConstraintProvider.class) {
      double minWidth = Math.max(0, getMinimumSize().width);
      double minHeight = Math.max(0, getMinimumSize().height) + topParticipants.getHeight() + bottomParticipants.getHeight();
      return new NodeSizeConstraintProvider(new SizeD(minWidth, minHeight), SizeD.INFINITE);
    } else if (type == INodeInsetsProvider.class) {
      return new ChoreographyInsetsProvider();
    } else if (type == IEditLabelHelper.class) {
      return new ChoreographyEditLabelHelper(node);
    } else if (type == IClickListener.class) {
      IIcon bandIcon = createTaskBandIcon(node);
      if (bandIcon != null) {
        bandIcon.setBounds(getTaskNameBandBounds(node));
        return getDelegatingClickListener(bandIcon);
      }
    }
    return super.lookup(node, type);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public ChoreographyNodeStyle clone() {
    ChoreographyNodeStyle newInstance = (ChoreographyNodeStyle)super.clone();

    newInstance.topParticipants = new ParticipantList();
    for (Participant participant : getTopParticipants()) {
      newInstance.topParticipants.add(participant.clone());
    }
    newInstance.bottomParticipants = new ParticipantList();
    for (Participant participant : getBottomParticipants()) {
      newInstance.bottomParticipants.add(participant.clone());
    }
    return newInstance;
  }

  static class ParticipantList extends ArrayList<Participant> {
    public final int getModCount() {
      return modCount + getParticipantModCount();
    }

    public final double getHeight() {
      double height = 0;
      for (Participant participant : this) {
        height += participant.getSize();
      }
      return height;
    }

    private int getParticipantModCount() {
      int participantCount = 0;
      for (Participant participant : this) {
        participantCount += participant.getModCount();
      }
      return participantCount;
    }
  }

  /**
   * Uses the style insets extended by the size of the participant bands.
   */
  private class ChoreographyInsetsProvider implements INodeInsetsProvider {

    public final InsetsD getInsets( INode node ) {
      InsetsD outerInsets = ChoreographyNodeStyle.this.getInsets();
      double topInsets = ((ParticipantList)getTopParticipants()).getHeight();
      double bottomInsets = ((ParticipantList)getBottomParticipants()).getHeight();

      bottomInsets += (getLoopCharacteristic() != LoopCharacteristic.NONE || getSubState() != SubState.NONE)
          ? BpmnConstants.Sizes.MARKER.height + ((InteriorLabelModel)BpmnConstants.Placements.CHOROGRAPHY_MARKER.getModel()).getInsets().bottom : 0;

      return InsetsD.fromLTRB(outerInsets.left, outerInsets.top + topInsets, outerInsets.right, outerInsets.bottom + bottomInsets);
    }
  }

  private static class ChoreographyEditLabelHelper implements IEditLabelHelper {
    private final INode node;

    public ChoreographyEditLabelHelper( INode node ) {
      this.node = node;
    }

    public final void onLabelEditing( LabelEditingEventArgs args ) {
      if (node.getLabels().size() == 0) {
        onLabelAdding(args);
        return;
      }
      args.setLabel(node.getLabels().getItem(0));
      args.setHandled(true);
    }

    public final void onLabelAdding( LabelEditingEventArgs args ) {
      ILabelModelParameter parameter = ChoreographyLabelModel.INSTANCE.findNextParameter(node);
      ILabelStyle labelStyle;
      if (parameter == ChoreographyLabelModel.NORTH_MESSAGE || parameter == ChoreographyLabelModel.SOUTH_MESSAGE) {
        labelStyle = new ChoreographyMessageLabelStyle();
      } else {
        labelStyle = ((GraphComponent)args.getContext().getCanvasComponent()).getGraph().getNodeDefaults().getLabelDefaults().getStyle();
      }
      if (parameter == null) {
        parameter = ExteriorLabelModel.WEST;
      }

      args.setLayoutParameter(parameter);
      args.setOwner(node);
      args.setStyle(labelStyle);
      args.setHandled(true);
    }

  }

  private static class ChoreographyContainer extends VisualGroup {
    public void setBounds(RectD bounds) {
      this.bounds = bounds;
    }

    private RectD bounds;

    public final RectD getBounds() {
      return bounds;
    }

    private int modCount;

    public final int getModCount() {
      return modCount;
    }

    private List<IIcon> topParticipantIcons;

    public final List<IIcon> getTopParticipantIcons() {
      return this.topParticipantIcons;
    }

    public final void setTopParticipantIcons( List<IIcon> value ) {
      this.topParticipantIcons = value;
    }

    private List<IIcon> bottomParticipantIcons;

    public final List<IIcon> getBottomParticipantIcons() {
      return this.bottomParticipantIcons;
    }

    public final void setBottomParticipantIcons( List<IIcon> value ) {
      this.bottomParticipantIcons = value;
    }

    public void setModCount(int modCount) {
      this.modCount = modCount;
    }
  }

  private static class TaskBandContainer extends VisualGroup {
    private IIcon icon;

    public IIcon getIcon() {
      return icon;
    }

    public void setIcon(IIcon icon) {
      this.icon = icon;
    }
  }


  static {
    ShapeNodeStyleRenderer renderer = new ShapeNodeStyleRenderer();
    renderer.setRoundRectArcRadius(BpmnConstants.CHOREOGRAPHY_CORNER_RADIUS);
    ShapeNodeStyle style = new ShapeNodeStyle(renderer);
    style.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    style.setPen(Pen.getBlack());
    style.setPaint(null);
    SNS = style;

    IIcon lineIcon = IconFactory.createLine(BpmnConstants.Pens.CHOREOGRAPHY_MESSAGE_LINK, 0.5, 0, 0.5, 1);
    IIcon initiatingMessageIcon = IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.CHOREOGRAPHY_INITIALIZING_PARTICIPANT);
    IIcon responseMessageIcon = IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.CHOREOGRAPHY_RECEIVING_PARTICIPANT);

    ArrayList<IIcon> icons = new ArrayList<>();
    icons.add(IconFactory.createPlacedIcon(lineIcon, ExteriorLabelModel.NORTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)));
    icons.add(IconFactory.createPlacedIcon(initiatingMessageIcon, BpmnConstants.Placements.CHOREOGRAPHY_TOP_MESSAGE, BpmnConstants.Sizes.MESSAGE));
    TOP_INITIATING_MESSAGE_ICON = IconFactory.createCombinedIcon(icons);

    icons = new ArrayList<>();
    icons.add(IconFactory.createPlacedIcon(lineIcon, ExteriorLabelModel.SOUTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)));
    icons.add(IconFactory.createPlacedIcon(responseMessageIcon, BpmnConstants.Placements.CHOREOGRAPHY_BOTTOM_MESSAGE, BpmnConstants.Sizes.MESSAGE));
    BOTTOM_RESPONSE_MESSAGE_ICON = IconFactory.createCombinedIcon(icons);

    icons = new ArrayList<>();
    icons.add(IconFactory.createPlacedIcon(lineIcon, ExteriorLabelModel.SOUTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)));
    icons.add(IconFactory.createPlacedIcon(initiatingMessageIcon, BpmnConstants.Placements.CHOREOGRAPHY_BOTTOM_MESSAGE, BpmnConstants.Sizes.MESSAGE));
    BOTTOM_INITIATING_MESSAGE_ICON = IconFactory.createCombinedIcon(icons);

    icons = new ArrayList<>();
    icons.add(IconFactory.createPlacedIcon(lineIcon, ExteriorLabelModel.NORTH, new SizeD(MESSAGE_DISTANCE, MESSAGE_DISTANCE)));
    icons.add(IconFactory.createPlacedIcon(responseMessageIcon, BpmnConstants.Placements.CHOREOGRAPHY_TOP_MESSAGE, BpmnConstants.Sizes.MESSAGE));
    TOP_RESPONSE_MESSAGE_ICON = IconFactory.createCombinedIcon(icons);

    TASK_BAND_BACKGROUND_ICON = IconFactory.createChoreographyTaskBand();
    MULTI_INSTANCE_ICON = IconFactory.createPlacedIcon(IconFactory.createLoopCharacteristic(LoopCharacteristic.PARALLEL), BpmnConstants.Placements.CHOROGRAPHY_MARKER, BpmnConstants.Sizes.MARKER);
  }

}
