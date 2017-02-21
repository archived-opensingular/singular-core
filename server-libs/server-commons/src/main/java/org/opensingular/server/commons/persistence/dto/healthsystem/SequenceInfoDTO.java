package org.opensingular.server.commons.persistence.dto.healthsystem;

import java.io.Serializable;
import java.math.BigDecimal;

public class SequenceInfoDTO implements Serializable {

	private String sequenceName;
	private BigDecimal maxValue;
	private BigDecimal minValue;
	private BigDecimal currentValue;
	private BigDecimal increment;
	private boolean found = false;
	
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	public BigDecimal getMinValue() {
		return minValue;
	}
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	public BigDecimal getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}
	public BigDecimal getIncrement() {
		return increment;
	}
	public void setIncrement(BigDecimal increment) {
		this.increment = increment;
	}
	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}
	
}
