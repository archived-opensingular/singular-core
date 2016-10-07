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

