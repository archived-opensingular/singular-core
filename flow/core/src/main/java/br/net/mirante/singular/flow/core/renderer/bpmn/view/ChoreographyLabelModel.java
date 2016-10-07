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
package br.net.mirante.singular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.ConstantLabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.DefaultLabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelCandidateDescriptorProvider;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterProvider;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.SandwichLabelModel;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.IMarkupExtensionConverter;
import com.yworks.yfiles.graphml.IWriteContext;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.graphml.MarkupExtensionConverter;
import com.yworks.yfiles.graphml.SingletonSerialization;
import com.yworks.yfiles.utils.IEnumerable;

import java.util.ArrayList;

/**
 * A label model for nodes using a {@link ChoreographyNodeStyle} that position labels on the participant or task name
 * bands.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyLabelModel implements ILabelModel, ILabelModelParameterProvider {
  //region Initialize static fields

  /**
   * The {@link ChoreographyLabelModel} singleton.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public static final ChoreographyLabelModel INSTANCE;

  /**
   * A singleton for labels placed centered on the task name band.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "TaskNameBand")
  public static final ILabelModelParameter TASK_NAME_BAND;

  /**
   * A singleton for message labels placed north of the node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "NorthMessage")
  public static final ILabelModelParameter NORTH_MESSAGE;

  /**
   * A singleton for message labels placed south of the node.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @GraphML(name = "SouthMessage")
  public static final ILabelModelParameter SOUTH_MESSAGE;

  private static final InteriorLabelModel INTERIOR_MODEL = new InteriorLabelModel();

  private static final SimpleNode DUMMY_NODE = new SimpleNode();

  private static final SimpleLabel DUMMY_LABEL = new SimpleLabel(DUMMY_NODE, "", InteriorLabelModel.CENTER);

  //endregion

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter parameter ) {
    if (parameter instanceof ChoreographyParameter && label.getOwner() instanceof INode && ((INode)label.getOwner()).getStyle() instanceof ChoreographyNodeStyle) {
      return ((ChoreographyParameter)parameter).getGeometry(label);
    } else if (label.getOwner() instanceof INode) {
      IRectangle layout = ((INode)label.getOwner()).getLayout();
      return new OrientedRectangle(layout.getX(), layout.getY() + layout.getHeight(), layout.getWidth(), layout.getHeight());
    }
    return OrientedRectangle.EMPTY;
  }

  /**
   * Returns {@link #TASK_NAME_BAND} as default parameter.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createDefaultParameter() {
    return TASK_NAME_BAND;
  }

  /**
   * Creates the parameter for the participant at the given position.
   * @param top Whether the index refers to {@link ChoreographyNodeStyle#getTopParticipants() TopParticipants} or
   * {@link ChoreographyNodeStyle#getBottomParticipants() BottomParticipants}.
   * @param index The index of the participant band the label shall be placed in.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter createParticipantParameter( boolean top, int index ) {
    return new ParticipantParameter(top, index);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILookup getContext( ILabel label, ILabelModelParameter parameter ) {
    return InteriorLabelModel.CENTER.getModel().getContext(label, parameter);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final <TLookup> TLookup lookup( Class<TLookup> type ) {
    if (type == ILabelModelParameterProvider.class) {
      return (TLookup)this;
    }
    if (type == ILabelModelParameterFinder.class) {
      return (TLookup)DefaultLabelModelParameterFinder.INSTANCE;
    }
    if (type == ILabelCandidateDescriptorProvider.class) {
      return (TLookup)ConstantLabelCandidateDescriptorProvider.INTERNAL_DESCRIPTOR_PROVIDER;
    }
    return null;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IEnumerable<ILabelModelParameter> getParameters( ILabel label, ILabelModel model ) {
    ILabelOwner owner = label.getOwner();
    if(!(owner instanceof INode)) {
      return IEnumerable.EMPTY;
    }
    ArrayList<ILabelModelParameter> parameters = new ArrayList<>();
    INode node = (INode)owner;
    if (node.getStyle() instanceof ChoreographyNodeStyle) {
      ChoreographyNodeStyle nodeStyle = (ChoreographyNodeStyle)node.getStyle();
      int i = 0;
      for (Participant p : nodeStyle.getTopParticipants()) {
        parameters.add(createParticipantParameter(true, i));
        i++;
      }
      parameters.add(TASK_NAME_BAND);
      i = 0;
      for (Participant p : nodeStyle.getBottomParticipants()) {
        parameters.add(createParticipantParameter(false, i));
        i++;
      }
      parameters.add(NORTH_MESSAGE);
      parameters.add(SOUTH_MESSAGE);
    }

    return IEnumerable.create(parameters);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelModelParameter findNextParameter( INode node ) {
    INodeStyle style = node.getStyle();
    ChoreographyNodeStyle nodeStyle = style instanceof ChoreographyNodeStyle ? (ChoreographyNodeStyle) style : null;
    if (nodeStyle != null) {
      int taskNameBandCount = 1;
      int topParticipantCount = nodeStyle.getTopParticipants().size();
      int bottomParticipantCount = nodeStyle.getBottomParticipants().size();
      int messageCount = 2;

      boolean[] parameterTaken = new boolean[taskNameBandCount + topParticipantCount + bottomParticipantCount + messageCount];

      // check which label positions are already taken
      for (ILabel label : node.getLabels()) {
        ILabelModelParameter param = label.getLayoutParameter();
        ChoreographyParameter parameter = param instanceof ChoreographyParameter ? (ChoreographyParameter)param : null;
        if (parameter != null) {
          int index = 0;
          if (!(parameter instanceof TaskNameBandParameter)) {
            index++;

            if (parameter instanceof ParticipantParameter) {
              ParticipantParameter pp = (ParticipantParameter)parameter;
              if (!pp.top) {
                index += topParticipantCount;
              }
              index += pp.index;
            } else {
              index += topParticipantCount + bottomParticipantCount;
              if (!((MessageParameter)parameter).isNorth()) {
                index++;
              }
            }
          }
          parameterTaken[index] = true;
        }
      }

      // get first label position that isn't taken already
      for (int i = 0; i < parameterTaken.length; i++) {
        if (!parameterTaken[i]) {
          if (i < taskNameBandCount) {
            return TASK_NAME_BAND;
          }
          i -= taskNameBandCount;
          if (i < topParticipantCount) {
            return createParticipantParameter(true, i);
          }
          i -= topParticipantCount;
          if (i < bottomParticipantCount) {
            return createParticipantParameter(false, i);
          }
          i -= bottomParticipantCount;
          return i == 0 ? NORTH_MESSAGE : SOUTH_MESSAGE;
        }
      }
    }
    return null;
  }

  //region Parameters

  private abstract static class ChoreographyParameter implements ILabelModelParameter {
    public final ILabelModel getModel() {
      return INSTANCE;
    }

    public abstract IOrientedRectangle getGeometry( ILabel label );

    public final boolean supports( ILabel label ) {
      return label.getOwner() instanceof INode;
    }

    public abstract ChoreographyParameter clone();
  }

  @MarkupExtensionConverter(ParticipantParameterConverter.class)
  private static class ParticipantParameter extends ChoreographyParameter {
    private static final InteriorLabelModel ILM;
    private static final ILabelModelParameter PLACEMENT;

    static {
      ILM = new InteriorLabelModel();
      ILM.setInsets(new InsetsD(0));
      PLACEMENT = ILM.createParameter(InteriorLabelModel.Position.NORTH);
    }
    final int index;

    final boolean top;

    public ParticipantParameter( boolean top, int index ) {
      this.top = top;
      this.index = index;
    }

    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      ILabelOwner owner = label.getOwner();
      if(!(owner instanceof INode)) {
        return OrientedRectangle.EMPTY;
      }
      INode node = (INode)owner;

      INodeStyle s = node.getStyle();
      if(!(s instanceof ChoreographyNodeStyle)) {
        return OrientedRectangle.EMPTY;
      }
      ChoreographyNodeStyle style = (ChoreographyNodeStyle)s;
      DUMMY_NODE.setLayout(style.getParticipantBandBounds(node, index, top));
      DUMMY_LABEL.setPreferredSize(label.getPreferredSize());
      return ILM.getGeometry(DUMMY_LABEL, PLACEMENT);
    }

    @Override
    public ParticipantParameter clone() {
      return new ParticipantParameter(top, index);
    }
  }

  public static final class ParticipantParameterConverter implements IMarkupExtensionConverter {
    public final boolean canConvert( IWriteContext context, Object value ) {
      return value instanceof ParticipantParameter;
    }

    public final MarkupExtension convert( IWriteContext context, Object value ) {
      ParticipantParameter participantParameter = (ParticipantParameter)value;
      ParticipantLabelModelParameterExtension newInstance = new ParticipantLabelModelParameterExtension();
      newInstance.setIndex(participantParameter.index);
      newInstance.setTop(participantParameter.top);
      return newInstance;
    }

    //endregion
  }

  @SingletonSerialization({ChoreographyLabelModel.class})
  private static class TaskNameBandParameter extends ChoreographyParameter {
    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      ILabelOwner owner = label.getOwner();
      if(!(owner instanceof INode)) {
        return OrientedRectangle.EMPTY;
      }
      INode node = (INode)owner;

      INodeStyle s = node.getStyle();
      if(!(s instanceof ChoreographyNodeStyle)) {
        return OrientedRectangle.EMPTY;
      }
      ChoreographyNodeStyle style = (ChoreographyNodeStyle)s;

      RectD bandBounds = style.getTaskNameBandBounds(node);
      DUMMY_NODE.setLayout(bandBounds);
      DUMMY_LABEL.setPreferredSize(label.getPreferredSize());
      return INTERIOR_MODEL.getGeometry(DUMMY_LABEL, InteriorLabelModel.CENTER);
    }

    @Override
    public TaskNameBandParameter clone() {
      return new TaskNameBandParameter();
    }
  }

  @SingletonSerialization({ChoreographyLabelModel.class})
  private static class MessageParameter extends ChoreographyParameter {
    private static final ILabelModelParameter NORTH_PARAMETER;

    private static final ILabelModelParameter SOUTH_PARAMETER;

    private boolean north;

    public final boolean isNorth() {
      return this.north;
    }

    public final void setNorth( boolean value ) {
      this.north = value;
    }

    @Override
    public IOrientedRectangle getGeometry( ILabel label ) {
      ILabelModelParameter parameter = isNorth() ? NORTH_PARAMETER : SOUTH_PARAMETER;
      return parameter.getModel().getGeometry(label, parameter);
    }

    @Override
    public MessageParameter clone() {
      MessageParameter newInstance = new MessageParameter();
        newInstance.setNorth(isNorth());
      return newInstance;
    }

    static {
      SandwichLabelModel slm = new SandwichLabelModel();
      slm.setYOffset(15);
      NORTH_PARAMETER = slm.createNorthParameter();
      SOUTH_PARAMETER = slm.createSouthParameter();
    }

  }

  //endregion
  static {
    INSTANCE = new ChoreographyLabelModel();
    TASK_NAME_BAND = new TaskNameBandParameter();
    MessageParameter newInstance = new MessageParameter();
    newInstance.setNorth(true);
    NORTH_MESSAGE = newInstance;
    MessageParameter newInstance2 = new MessageParameter();
    newInstance2.setNorth(false);
    SOUTH_MESSAGE = newInstance2;
  }

}
