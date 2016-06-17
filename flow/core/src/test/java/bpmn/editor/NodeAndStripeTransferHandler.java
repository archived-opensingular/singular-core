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
package bpmn.editor;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.INodeStyle;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transfers {@link INode} and
 * {@link IStripe} instances from a
 * {@link JList} to another Swing component in the same JVM.
 */
class NodeAndStripeTransferHandler extends TransferHandler {
  /**
   * Data flavor the represents {@link INode}
   * instances.
   */
  private DataFlavor nodeFlavor;
  /**
   * Data flavor the represents {@link IStripe}
   * instances.
   */
  private DataFlavor stripeFlavor;

  /**
   * Initializes a new <code>MyTransferHandler</code> instance for
   * {@link INode} and
   * {@link IStripe} instances.
   */
  NodeAndStripeTransferHandler() {
    super("selectedValue");
    nodeFlavor = newFlavor(INode.class);
    stripeFlavor = newFlavor(IStripe.class);
  }

  /**
   * Creates a {@link Transferable} instance for the
   * selected value from the given {@link JList}.
   * This method assumes that all values in the {@link JList}
   * are of type {@link INode}.
   */
  @Override
  protected Transferable createTransferable( JComponent c ) {
    INode value = (INode) ((JList) c).getSelectedValue();
    Object tag = value.getTag();
    if (tag instanceof IStripe) {
      // stripeFlavor will trigger com.yworks.yfiles.view.input.StripeDropInputMode
      return new MyTransferable(stripeFlavor, tag);
    } else {
      // we use a copy of the node since the style should not be shared
      SimpleNode node = new SimpleNode();
      node.setLayout(value.getLayout());
      node.setStyle((INodeStyle) value.getStyle().clone());
      node.setTag(value.getTag());
      // nodeFlavor will trigger com.yworks.yfiles.view.input.NodeDropInputMode
      return new MyTransferable(nodeFlavor, node);
    }
  }

  /**
   * Creates a new {@link DataFlavor} instance
   * for a
   * {@link DataFlavor#javaJVMLocalObjectMimeType JVM local object}
   * of the given type.
   */
  private static DataFlavor newFlavor( Class<?> type )  {
    try {
      return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + type.getName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Transfers data from one Swing component to another one in the same JVM.
   */
  private static class MyTransferable implements Transferable {
    private DataFlavor flavor;
    /** The transferred data. */
    private Object data;

    /**
     * Initializes a new <code>MyTransferable</code> instance for transferring
     * the given data from one Swing component to another one in the same JVM.
     * @param flavor  the type of data to be transferred. The flavor should
     * represent a
     * {@link DataFlavor#javaJVMLocalObjectMimeType JVM local object}.
     * The flavor's representation class should match the type of the given
     * data.
     * @param data the data to be transferred. The type of the data should match
     * the given flavor's representation class.
     */
    MyTransferable( DataFlavor flavor, Object data ) {
      this.flavor = flavor;
      this.data = data;
    }

    /**
     * Returns the transferable's single supported data flavor.
     * @return the transferable's single supported data flavor.
     * @see #MyTransferable(DataFlavor, Object)
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {flavor};
    }

    /**
     * Determines whether or not the transferred data is represented
     * by the specified flavor.
     * @param flavor the flavor to check against the transferable's single
     * supported data flavor.
     * @return <code>true</code> if the specified flavor matches the
     * transferable's single supported data flavor; <code>false</code>
     * otherwise.
     */
    @Override
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
      return flavor.equals(this.flavor);
    }

    /**
     * Returns the transferred data if the specified flavor matches the
     * transferable's single supported data flavor.
     * @param flavor the flavor to check against the transferable's single
     * supported data flavor.
     * @return the transferred data.
     * @throws UnsupportedFlavorException if the specified flavor does not
     * match the transferable's single supported data flavor.
     * @see #MyTransferable(DataFlavor, Object)
     */
    @Override
    public Object getTransferData(
        DataFlavor flavor
    ) throws UnsupportedFlavorException, IOException {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return data;
    }
  }
}
