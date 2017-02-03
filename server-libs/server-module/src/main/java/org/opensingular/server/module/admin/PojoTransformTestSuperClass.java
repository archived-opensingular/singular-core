package org.opensingular.server.module.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PojoTransformTestSuperClass {
	private int idade;
	private Integer idadeBig;
	private double doubleVal;
	private Double doubleValBig;
	
	private PojoTransformTestSubClass subClass;
	
	private List<PojoTransformTestSubClass> complexCollection = new ArrayList<>();
	private Set<String> setCollectionTest = new TreeSet<>();
	
//	private Map<Object, Object> mapTest = new HashMap<>();

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public Integer getIdadeBig() {
		return idadeBig;
	}

	public void setIdadeBig(Integer idadeBig) {
		this.idadeBig = idadeBig;
	}

	public double getDoubleVal() {
		return doubleVal;
	}

	public void setDoubleVal(double doubleVal) {
		this.doubleVal = doubleVal;
	}

	public Double getDoubleValBig() {
		return doubleValBig;
	}

	public void setDoubleValBig(Double doubleValBig) {
		this.doubleValBig = doubleValBig;
	}

	public PojoTransformTestSubClass getSubClass() {
		return subClass;
	}

	public void setSubClass(PojoTransformTestSubClass subClass) {
		this.subClass = subClass;
	}

	public List<PojoTransformTestSubClass> getComplexCollection() {
		return complexCollection;
	}

	public void setComplexCollection(List<PojoTransformTestSubClass> complexCollection) {
		this.complexCollection = complexCollection;
	}

	public Set<String> getSetCollectionTest() {
		return setCollectionTest;
	}

	public void setSetCollectionTest(Set<String> setCollectionTest) {
		this.setCollectionTest = setCollectionTest;
	}

//	public Map<Object, Object> getMapTest() {
//		return mapTest;
//	}
//
//	public void setMapTest(Map<Object, Object> mapTest) {
//		this.mapTest = mapTest;
//	}
	
}
