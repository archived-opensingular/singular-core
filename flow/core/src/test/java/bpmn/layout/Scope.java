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
package bpmn.layout;


/**
 * @see BpmnLayout#getScope()
 */
public enum Scope {
  /**
   * Scope specifier. Consider all elements during the layout.
   * @see BpmnLayout#getScope()
   */
  ALL_ELEMENTS(0),

  /**
   * Scope specifier to consider only selected elements.
   * <p>
   * The selection state of an edge is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link BpmnLayout#AFFECTED_EDGES_DP_KEY} .
   * <br />
   * The selection state of a node is determined by a boolean value returned by the data provider associated with the data
   * provider key {@link BpmnLayout#AFFECTED_NODES_DP_KEY} .
   * </p>
   * <p>
   * Note, if the layout mode is set to {@link bpmn.layout.LayoutMode#FULL_LAYOUT} non-selected elements may also be moved to
   * produce valid drawings. However the layout algorithm uses the initial position of such elements as sketch.
   * </p>
   * @see BpmnLayout#getScope()
   */
  SELECTED_ELEMENTS(2);

  private final int value;

  Scope(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final Scope fromOrdinal( int ordinal ) {
    for (Scope current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }
}
