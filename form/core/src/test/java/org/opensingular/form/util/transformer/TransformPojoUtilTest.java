package org.opensingular.form.util.transformer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
		PojoTransformTestSuperClass parentObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass childObject = new PojoTransformTestSubClass();
		
		childObject.setNome("filho");
		childObject.setCpf("123456789");
		
		parentObject.setSubClass(childObject);
		
		Map<String, Object> map = getMapObject(parentObject);
		
		Map<String, Object> dadosRecuperados = (Map<String, Object>) map.get("subClass");
		
		Assert.assertEquals(childObject.getNome(), dadosRecuperados.get("nome"));
		Assert.assertEquals(childObject.getCpf(), dadosRecuperados.get("cpf"));
	}
	
	@Test
	public void testTwoSidedReference(){
		PojoTransformTestSuperClass parentObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass childObject = new PojoTransformTestSubClass();
		
		childObject.setNome("filho");
		childObject.setCpf("123456789");
		childObject.setPai(parentObject);
		
		parentObject.setSubClass(childObject);
		parentObject.setIdadeBig(40);
		
		Map<Integer, Map<String, Object>> mappedAll = TransformPojoUtil.pojoToMap(parentObject);
		
		Map<String, Object> dadosRecuperados = (Map<String, Object>) mappedAll.get(System.identityHashCode(childObject));
		
		Assert.assertEquals("codRef="+System.identityHashCode(parentObject), dadosRecuperados.get("pai"));
		Assert.assertEquals((Integer) 40, mappedAll.get(System.identityHashCode(parentObject)).get("idadeBig"));
		Assert.assertEquals("filho", ((Map<String, Object>)mappedAll.get(System.identityHashCode(parentObject)).get("subClass")).get("nome") );
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
		
		List<Object> object = (List<Object>) map.get("setCollectionTest");
		
		Assert.assertTrue(set.contains(((Map<String, Object>)object.get(0)).get("String")));
		Assert.assertTrue(set.contains(((Map<String, Object>)object.get(1)).get("String")));
		Assert.assertTrue(set.contains(((Map<String, Object>)object.get(2)).get("String")));
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
		PojoTransformTestSuperClass parentObject = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass childObject = new PojoTransformTestSubClass();
		
		childObject.setNome("filho");
		childObject.setCpf("123456789");
		childObject.setPai(parentObject);
		
		PojoTransformTestSubClass childObject2 = new PojoTransformTestSubClass();
		childObject2.setNome("filho2");
		
		List<PojoTransformTestSubClass> children = new ArrayList<>();
		children.add(childObject);
		children.add(childObject2);
		children.add(new PojoTransformTestSubClass());
		
		parentObject.setComplexCollection(children);
		
		Map<String, Object> map = getMapObject(parentObject);
		
		List<Map<String, Object>> object = (List<Map<String, Object>>) map.get("complexCollection");
		
		Assert.assertEquals("filho", object.get(0).get("nome"));
		Assert.assertEquals("filho2", object.get(1).get("nome"));
		Assert.assertNull(object.get(2).get("nome"));
		
	}

	private Map<String, Object> getMapObject(PojoTransformTestSuperClass object) {
		Map<Integer, Map<String, Object>> mappedAll = TransformPojoUtil.pojoToMap(object);
		
		return mappedAll.get(System.identityHashCode(object));
	}
	
	private SDictionary dictionary;
	private PackageBuilder pb;
	
	@Before
	public void setUp(){
		dictionary = SDictionary.create();
		pb = dictionary.createNewPackage("teste");
	}
	
	@Test
	public void testeMapPrimitiveTypes(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		object.setDoubleVal(2974.5);
		
		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		classe.addFieldInteger("idade");
		classe.addFieldDecimal("doubleVal");
		
		SIComposite siObject = classe.newInstance();
		
		pojoToInstanceTest(object, siObject, false);
		
		SInstance integerField = siObject.getField("idade");
		SInstance doubleField = siObject.getField("doubleVal");
		
		Assert.assertNotNull(integerField);
		Assert.assertNotNull(doubleField);
		
		Assert.assertEquals(25, integerField.getValue());
		Assert.assertEquals(2974.5, ((BigDecimal)doubleField.getValue()).doubleValue(), 0);
	}
	
	@Test
	public void testeMapComplexType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setDoubleValBig(294.5);
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass();
		subObject.setNome("nome");
		object.setSubClass(subObject);

		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		classe.addFieldDecimal("doubleValBig");
		STypeComposite<SIComposite> subClasse =  classe.addFieldComposite("subClass");
		subClasse.addFieldString("nome");
		
		SIComposite siObject = classe.newInstance();
		
		pojoToInstanceTest(object, siObject, false);
		
		SIComposite subClassField = (SIComposite) siObject.getField("subClass");
		
		Assert.assertNotNull(subClassField);
		Assert.assertEquals("nome", subClassField.getField("nome").getValue());
	}
	
	@Test
	public void testeMapCollectionPrimitiveType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		Set<String> treeSetTest = new TreeSet<>();
		treeSetTest.add("string 1");
		treeSetTest.add("string 2");
		treeSetTest.add("string 3");
		object.setSetCollectionTest(treeSetTest);
		
		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		classe.addFieldListOf("setCollectionTest", STypeString.class);
		
		SIComposite siObject = classe.newInstance();
		
		pojoToInstanceTest(object, siObject, false);
		
		SIList<SInstance> field = (SIList<SInstance>) siObject.getField("setCollectionTest");
		
		List<Object> value = field.getValue();
		
		Assert.assertEquals("string 1", value.get(0));
		Assert.assertEquals("string 2", value.get(1));
		Assert.assertEquals("string 3", value.get(2));
		
	}
	
	@Test
	public void testeMapCollectionComplexType(){
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass("nome");
		PojoTransformTestSubClass subObject2 = new PojoTransformTestSubClass("outroNome");
		PojoTransformTestSubClass subObject3 = new PojoTransformTestSubClass("maisOutroNome");
		
		object.setComplexCollection(Arrays.asList(subObject, subObject2, subObject3));
		
		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		STypeList<STypeComposite<SIComposite>, SIComposite> listItems = classe.addFieldListOfComposite("complexCollection", "subTipo");
		
		STypeComposite<SIComposite> item = listItems.getElementsType();
		item.addFieldString("nome");
		
		SIComposite siObject = classe.newInstance();
		
		pojoToInstanceTest(object, siObject, false);
		
		SIList<SIComposite> compositeList = (SIList<SIComposite>) siObject.getFieldList("complexCollection");
		
		List<SIComposite> children = compositeList.getChildren();
		
		Assert.assertEquals("nome", children.get(0).getField("nome").getValue());
		Assert.assertEquals("outroNome", children.get(1).getField("nome").getValue());
		Assert.assertEquals("maisOutroNome", children.get(2).getField("nome").getValue());
	}
	
	@Test
	public void testeMapCircularReference(){
		
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass("nome");
		subObject.setPai(object);
		
		object.setSubClass(subObject);
		
		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		classe.addFieldInteger("idade");
		STypeComposite<SIComposite> subClass = classe.addFieldComposite("subClass");
		STypeComposite<SIComposite> copyOfObject = subClass.addFieldComposite("pai");
		copyOfObject.addFieldInteger("idade");
		
		SIComposite siObject = classe.newInstance();
		
		pojoToInstanceTest(object, siObject, false);
		
		SIComposite field = (SIComposite) siObject.getField("subClass");
		SIComposite fieldCopyOfSiObject = (SIComposite) field.getField("pai");
		
		Assert.assertEquals(object.getIdade(), fieldCopyOfSiObject.getField("idade").getValue());
	}
	
	@Test(expected=Exception.class)
	public void testeMapStrictModeTrue() throws Exception{
		
		PojoTransformTestSuperClass object = new PojoTransformTestSuperClass();
		object.setIdade(25);
		
		PojoTransformTestSubClass subObject = new PojoTransformTestSubClass("nome");
		object.setSubClass(subObject);
		
		STypeComposite<SIComposite> classe = pb.createCompositeType("classe");
		classe.addFieldInteger("idade");
		classe.addFieldInteger("idadeBig");
		classe.addFieldDecimal("doubleVal");
		classe.addFieldDecimal("doubleValBig");
		classe.addFieldListOf("setCollectionTest", STypeString.class);
		classe.addFieldListOfComposite("complexCollection", "subTipo");
		classe.addFieldComposite("subClass");
		
		SIComposite siObject = classe.newInstance();
		TransformPojoUtil.pojoToSInstance(object, siObject, true);
	}
	
	private void pojoToInstanceTest(PojoTransformTestSuperClass object, SInstance instanceTest, boolean strictMode) {
		try {
			TransformPojoUtil.pojoToSInstance(object, instanceTest, strictMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
