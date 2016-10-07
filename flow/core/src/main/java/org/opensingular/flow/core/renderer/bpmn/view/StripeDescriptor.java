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
package org.opensingular.flow.core.renderer.bpmn.view;

import com.yworks.yfiles.annotations.DefaultValue;
import com.yworks.yfiles.annotations.Obfuscation;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.view.Colors;

import java.awt.Paint;

/**
 * Helper class that can be used as StyleTag to bundle common visualization parameters for stripes.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class StripeDescriptor {
  private Paint backgroundPaint = Colors.TRANSPARENT;

  /**
   * The background brush for a stripe.
   * @return The BackgroundBrush.
   * @see #setBackgroundPaint(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Transparent", classValue = Paint.class)
  @GraphML(name = "BackgroundBrush")
  public final Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  /**
   * The background brush for a stripe.
   * @param value The BackgroundBrush to set.
   * @see #getBackgroundPaint()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Transparent", classValue = Paint.class)
  @GraphML(name = "BackgroundBrush")
  public final void setBackgroundPaint(Paint value) {
    backgroundPaint = value;
  }

  private Paint insetPaint = Colors.TRANSPARENT;

  /**
   * The inset brush for a stripe.
   * @return The InsetBrush.
   * @see #setInsetPaint(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Transparent", classValue = Paint.class)
  @GraphML(name = "InsetBrush")
  public final Paint getInsetPaint() {
    return insetPaint;
  }

  /**
   * The inset brush for a stripe.
   * @param value The InsetBrush to set.
   * @see #getInsetPaint()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Transparent", classValue = Paint.class)
  @GraphML(name = "InsetBrush")
  public final void setInsetPaint(Paint value) {
    insetPaint = value;
  }

  private Paint borderPaint = Colors.BLACK;

  /**
   * The border brush for a stripe.
   * @return The BorderBrush.
   * @see #setBorderPaint(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Black", classValue = Paint.class)
  @GraphML(name = "BorderBrush")
  public final Paint getBorderPaint() {
    return borderPaint;
  }

  /**
   * The border brush for a stripe.
   * @param value The BorderBrush to set.
   * @see #getBorderPaint()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Black", classValue = Paint.class)
  @GraphML(name = "BorderBrush")
  public final void setBorderPaint(Paint value) {
    borderPaint = value;
  }

  private double borderThickness = 1;

  /**
   * The border thickness for a stripe.
   * @return The BorderThickness.
   * @see #setBorderThickness(double)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "1", classValue = Double.class)
  public final double getBorderThickness() {
    return borderThickness;
  }

  /**
   * The border thickness for a stripe.
   * @param value The BorderThickness to set.
   * @see #getBorderThickness()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "1", classValue = Double.class)
  public final void setBorderThickness( double value ) {
    borderThickness = value;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public boolean equals( Object obj ) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    StripeDescriptor that = (StripeDescriptor) obj;

    if (Double.compare(that.borderThickness, borderThickness) != 0) {
      return false;
    }
    if (backgroundPaint != null ? !backgroundPaint.equals(that.backgroundPaint) : that.backgroundPaint != null) {
      return false;
    }
    if (borderPaint != null ? !borderPaint.equals(that.borderPaint) : that.borderPaint != null) {
      return false;
    }
    if (insetPaint != null ? !insetPaint.equals(that.insetPaint) : that.insetPaint != null) {
      return false;
    }

    return true;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public int hashCode() {
    {
      int result;
      long temp;
      result = backgroundPaint != null ? backgroundPaint.hashCode() : 0;
      result = 31 * result + (insetPaint != null ? insetPaint.hashCode() : 0);
      result = 31 * result + (borderPaint != null ? borderPaint.hashCode() : 0);
      temp = Double.doubleToLongBits(borderThickness);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
    }
  }
}

