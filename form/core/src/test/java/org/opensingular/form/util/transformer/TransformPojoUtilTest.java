package org.opensingular.form.util.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;

@SuppressWarnings("unchecked")
public class TransformPojoUtilTest {
	@Test
	public void testPrimiteVal(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		
		object.setIdade(25);
		object.setDoubleVal(2974.5);
		
		Map<String, Object> map = getMapObject(object);
		
		Assert.assertEquals(25, (int)map.get("idade"));
		Assert.assertEquals(2974.5, (double)map.get("doubleVal"), 0.0);
	}
	
	@Test
	public void testNonPrimitiveVal(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		
		object.setIdadeBig(25);
		object.setDoubleValBig(2974.5);
		
		Map<String, Object> map = getMapObject(object);
		
		Integer integerExpected = 25;
		Double doubleExpected = 2974.5;
		
		Assert.assertEquals(integerExpected, (Integer)map.get("idadeBig"));
		Assert.assertEquals(doubleExpected, (Double)map.get("doubleValBig"));
	}
	
	@Test
	public void testAppendedObjectVal(){
		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass filhoObject = new PojoTransformTestSubClass();
		
		filhoObject.setNome("filho");
		filhoObject.setCpf("123456789");
		
		paiObject.setSubClass(filhoObject);
		
		Map<String, Object> map = getMapObject(paiObject);
		
		Map<String, Object> dadosRecuperados = (Map<String, Object>) map.get("subClass");
		
		Assert.assertEquals(filhoObject.getNome(), dadosRecuperados.get("nome"));
		Assert.assertEquals(filhoObject.getCpf(), dadosRecuperados.get("cpf"));
	}
	
	@Test
	public void testTwoSidedReference(){
		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass filhoObject = new PojoTransformTestSubClass();
		
		filhoObject.setNome("filho");
		filhoObject.setCpf("123456789");
		filhoObject.setPai(paiObject);
		
		paiObject.setSubClass(filhoObject);
		paiObject.setIdadeBig(40);
		
		Map<Integer, Map<String, Object>> mappedAll = TransformPojoUtil.pojoToMap(paiObject);
		
		Map<String, Object> dadosRecuperados = (Map<String, Object>) mappedAll.get(System.identityHashCode(filhoObject));
		
		Assert.assertEquals("codRef="+System.identityHashCode(paiObject), dadosRecuperados.get("pai"));
		Assert.assertEquals((Integer) 40, mappedAll.get(System.identityHashCode(paiObject)).get("idadeBig"));
		Assert.assertEquals("filho", ((Map<String, Object>)mappedAll.get(System.identityHashCode(paiObject)).get("subClass")).get("nome") );
	}
	
	@Test
	public void testSetCollection(){
		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
		Set<String> set = new TreeSet<>();
		
		set.add("string 1");
		set.add("string 2");
		set.add("string 3");
		
		paiObject.setSetCollectionTest(set);
		
		Map<String, Object> map = getMapObject(paiObject);
		
		List<String> object = (List<String>) map.get("setCollectionTest");
		
		Assert.assertTrue(set.contains(object.get(0)));
		Assert.assertTrue(set.contains(object.get(1)));
		Assert.assertTrue(set.contains(object.get(2)));
	}
	
//	@Test
//	public void testMapCollectionSimple(){
//		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
//		
//		// TODO
//		Map<Object, Object> mapTest = new HashMap<>();
//		 
//		mapTest.put("um", "valor1");
//		mapTest.put("dois", "valor2");
//		mapTest.put("tres", "valor3");
//
//		paiObject.setMapTest(mapTest);
//		
//		Map<String, Object> map = getMapObject(paiObject);
//		
//		map.get("mapTest");
//		
//		System.out.println();
//	}
	
//	@Test
//	public void testMapCollectionComplex(){
//		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
//		
//		// TODO
//		Map<Object, Object> mapTest = new HashMap<>();
//		 
//		mapTest.put("um", "valor1");
//		mapTest.put("dois", "valor2");
//		mapTest.put("tres", "valor3");
//
//		paiObject.setMapTest(mapTest);
//		
//		Map<String, Object> map = getMapObject(paiObject);
//		
//		map.get("mapTest");
//		
//		System.out.println();
//	}
	
	@Test
	public void testCollectionsObjects(){
		PojoTransformTestSuperClass paiObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass filhoObject = new PojoTransformTestSubClass();
		
		filhoObject.setNome("filho");
		filhoObject.setCpf("123456789");
		filhoObject.setPai(paiObject);
		
		PojoTransformTestSubClass filho2Object = new PojoTransformTestSubClass();
		filho2Object.setNome("filho2");
		
		List<PojoTransformTestSubClass> filhos = new ArrayList<>();
		filhos.add(filhoObject);
		filhos.add(filho2Object);
		filhos.add(new PojoTransformTestSubClass());
		
		paiObject.setComplexCollection(filhos);
		
		Map<String, Object> map = getMapObject(paiObject);
		
		List<Map<String, Object>> object = (List<Map<String, Object>>) map.get("complexCollection");
		
		Assert.assertEquals("filho", object.get(0).get("nome"));
		Assert.assertEquals("filho2", object.get(1).get("nome"));
		Assert.assertNull(object.get(2).get("nome"));
		
	}

	private Map<String, Object> getMapObject(PojoTransformTestSuperClass object) {
		Map<Integer, Map<String, Object>> mappedAll = TransformPojoUtil.pojoToMap(object);
		
		return mappedAll.get(System.identityHashCode(object));
	}
	
	
//	@Inject
//	@Named("formConfigWithDatabase")
	private SFormConfig<String> formConfig;
	
	@Test
	public void testeMapPrimitiveTypes(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		object.setDoubleVal(2974.5);
		
		// TODO comparacao
		SInstance instanceTest = generateInstance();
		pojoToInstanceTest(object, instanceTest, false);
	}


	
	@Test
	public void testeMapComplexType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setDoubleValBig(294.5);
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass();
		subObject.setNome("nome");
		
		object.setSubClass(subObject);
		
//		TODO comparacao
		SInstance instanceTest = generateInstance();
		pojoToInstanceTest(object, instanceTest, false);
	}
	
	@Test
	public void testeMapCollectionPrimitiveType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		Set<String> treeSetTest = new TreeSet<>();
		treeSetTest.add("string 1");
		treeSetTest.add("string 2");
		treeSetTest.add("string 3");
		object.setSetCollectionTest(treeSetTest);
		
		// TODO comparacao
		SInstance instanceTest = generateInstance();
		pojoToInstanceTest(object, instanceTest, false);
	}
	
	@Test
	public void testeMapCollectionComplexType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass("nome");
		PojoTransformTestSubClass subObject2 = new PojoTransformTestSubClass("outroNome");
		PojoTransformTestSubClass subObject3 = new PojoTransformTestSubClass("maisOutroNome");
		
		object.setComplexCollection(Arrays.asList(subObject, subObject2, subObject3));
		
		// TODO comparacao
		SInstance instanceTest = generateInstance();
		pojoToInstanceTest(object, instanceTest, false);
	}
	
	@Test
	public void testeMapStrictModeTrue(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass("nome");
		PojoTransformTestSubClass subObject2 = new PojoTransformTestSubClass("outroNome");
		PojoTransformTestSubClass subObject3 = new PojoTransformTestSubClass("maisOutroNome");
		
		object.setComplexCollection(Arrays.asList(subObject, subObject2, subObject3));
		
		// TODO comparacao
		SInstance instanceTest = generateInstance();
		pojoToInstanceTest(object, instanceTest, false);
	}
	
	private SInstance generateInstance() {
		return SDocumentFactory.empty().createInstance(new RefType() {
			@Override
			protected SType<?> retrieve() {
				return formConfig.getTypeLoader().loadTypeOrException("");
//				return formConfig.getTypeLoader().loadTypeOrException(SDbHealth.TYPE_FULL_NAME);
			}
		});
	}

	private void pojoToInstanceTest(PojoTransformTestSuperClass object, SInstance instanceTest, boolean strictMode) {
		try {
			TransformPojoUtil.pojoToSInstance(object, instanceTest, strictMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class PojoTransformTestSuperClass {
		private int idade;
		private Integer idadeBig;
		private double doubleVal;
		private Double doubleValBig;
		
		private PojoTransformTestSubClass subClass;
		
		private List<PojoTransformTestSubClass> complexCollection = new ArrayList<>();
		private Set<String> setCollectionTest = new TreeSet<>();
		
//		private Map<Object, Object> mapTest = new HashMap<>();

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

//		public Map<Object, Object> getMapTest() {
//			return mapTest;
//		}
	//
//		public void setMapTest(Map<Object, Object> mapTest) {
//			this.mapTest = mapTest;
//		}
	}
	
	private class PojoTransformTestSubClass {
		private String nome;
		private String cpf;
		
		private Integer valorQualquer;
		
		private PojoTransformTestSuperClass pai;
		
		public PojoTransformTestSubClass() {
		}
		
		public PojoTransformTestSubClass(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getCpf() {
			return cpf;
		}

		public void setCpf(String cpf) {
			this.cpf = cpf;
		}

		public Integer getValorQualquer() {
			return valorQualquer;
		}

		public void setValorQualquer(Integer valorQualquer) {
			this.valorQualquer = valorQualquer;
		}

		public PojoTransformTestSuperClass getPai() {
			return pai;
		}

		public void setPai(PojoTransformTestSuperClass pai) {
			this.pai = pai;
		}
		
	}
}
