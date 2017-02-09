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

package org.opensingular.form.util.transformer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

public class TransformPojoUtil {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(TransformPojoUtil.class);
	private static Set<Class<?>> STOP_CRITERY = defineStopCritery();

	private static Set<Class<?>> defineStopCritery() {
		Set<Class<?>> stop = new HashSet<>();
		
		stop.add(Byte.class);
		stop.add(Integer.class);
		stop.add(Short.class);
		stop.add(Long.class);
		stop.add(Float.class);
		stop.add(Double.class);
		stop.add(String.class);
		stop.add(Boolean.class);
		
		stop.add(BigDecimal.class);
		stop.add(BigInteger.class);

		return stop;
	}
	
	/**
	 * metodo para transformar um objeto em um map com os valores dele
	 * 
	 * @param objectToConvert o objeto que deseja obter o map
	 * @return retorna o Map com o objeto que se queria converter e todos os objetos pendurados a ele,
	 * 		para recuperá-lo deve-se usar System.identityHashCode() passando o objectToConvert como argumento.
	 * 
	 * @exception quando o Objeto a ser mapeado possui um map dentro dele. Não foi implementado ainda.
	 */
	public static Map<Integer, Map<String, Object>> pojoToMap(Object objectToConvert){
		
		Map<Integer, Map<String, Object>> fieldsTST = new HashMap<>();
		
		fieldsTST.put(System.identityHashCode(objectToConvert), new HashMap<>());
		
		Map<String, Object> mapObjRaiz = new HashMap<>();
		fieldsTST.put(System.identityHashCode(objectToConvert), mapObjRaiz);
		
		Arrays.asList(objectToConvert.getClass().getDeclaredFields())
			.forEach(f->convertObjectToMap(fieldsTST, f, objectToConvert));
		
		return fieldsTST;
	}
	
	private static void convertObjectToMap(Map<Integer, Map<String, Object>> mapMain, Field field, Object objectToConvert) {
		field.setAccessible(true);
		Map<String, Object> map = mapMain.get(System.identityHashCode(objectToConvert));
		if(map == null){
			map = new HashMap<>();
			mapMain.put(System.identityHashCode(objectToConvert), map);
		}
		
		try {
			verifyTypeOfAField(mapMain, field, objectToConvert, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void verifyTypeOfAField(Map<Integer, Map<String, Object>> mapMain, Field field,
			Object objectToConvert, Map<String, Object> map) throws Exception {
		Class<?> type = field.getType();
		// Verifica qual o tipo de campo
		if(Collection.class.isAssignableFrom(type)){
			fieldIsACollection(mapMain, field, objectToConvert, map);
		} else if(Map.class.isAssignableFrom(type)){
			fieldIsAMap(mapMain, field, objectToConvert, map);
		} else{
			fieldIsAObjectClass(mapMain, field, objectToConvert, map, type);
		}
	}

	// TODO Implementar futuramente
	private static void fieldIsAMap(Map<Integer, Map<String, Object>> mapMain, Field field, Object objectToConvert, Map<String, Object> map)
			throws Exception {

		throw new Exception("Não é suportado atualmente o mapeamento de map.");
	}

	private static void fieldIsAObjectClass(Map<Integer, Map<String, Object>> mapMain, Field field,
			Object objectToConvert, Map<String, Object> map, Class<?> type) throws IllegalAccessException {
		// caso contrario é um obj ou um tipo normal

		Object obj = field.get(objectToConvert);
		
		if(type.isPrimitive() || STOP_CRITERY.contains(type)){
			map.put(field.getName(), obj);
		}else{
			if(obj == null){
				map.put(field.getName(), null);
			}else{
				// se ja existe, coloca-se apenas a referencia do obj
				if(mapMain.containsKey(System.identityHashCode(obj))){
					map.put(field.getName(), "codRef="+System.identityHashCode(obj));
				}else{
					// senao, gera-se o dados necessarios
					Map<String, Object> mapItemFilho = new HashMap<>(); // cria o mapa do obj filho
					mapMain.put(System.identityHashCode(obj), mapItemFilho); // add no mapa de referencia
					map.put(field.getName(), mapItemFilho); // add no obj pai
					
					Arrays.asList(type.getDeclaredFields()).forEach(f->{
						try {
							convertObjectToMap(mapMain, f, obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
		}
	}

	private static void fieldIsACollection(Map<Integer, Map<String, Object>> mapMain, Field field,
			Object objectToConvert, Map<String, Object> map) throws IllegalAccessException {
		Collection<?> objCollection = (Collection<?>) field.get(objectToConvert);
		
		if(objCollection == null){
			map.put(field.getName(), null);
		} else{
			Iterator<?> iterator = objCollection.iterator();
			if(iterator.hasNext()){
				List<Object> colecao = new ArrayList<>();
				
				while(iterator.hasNext()){
					Object item = iterator.next();
					if(item.getClass().isPrimitive() || STOP_CRITERY.contains(item.getClass())){
						// maneira feita para garantir o tipo primitivo de ser colocado, e de se ter uma lista dele no SInstance
						Map<String, Object> itemMap = new HashMap<>();
						
						String[] valor = item.getClass().getName().split("\\.");
						itemMap.put(valor[valor.length-1], item);
						
						colecao.add(itemMap);
					} else{
						Map<String, Object> itemMap = mapMain.get(System.identityHashCode(item));
						if(itemMap == null){
							itemMap = new HashMap<>();
							mapMain.put(System.identityHashCode(item), itemMap);
						}
						Arrays.asList(item.getClass().getDeclaredFields()).forEach(f->convertObjectToMap(mapMain, f, item));
						colecao.add(itemMap);
					}
				}
				map.put(field.getName(), colecao);
			} else{
				map.put(field.getName(), null);
			}
		}
	}
	/**
	 * 
	 * @param pojoReferenceDataMap O map com os valores do Pojo e das subEntidades dele
	 * @param pojo O objeto a ser colocado no SInstance
	 * @param rootInstance O SInstance a ser preenchido com o Pojo dado
	 * @param strictMode boolean 
	 * 				- se TRUE então o preenchimento é feito se o valor existe no map e nao existe no SInstance dá erro
	 * 				- se FALSE então o preenchimento é permissivo, ou seja, só coloca os valores existentes da SInstance e nao se importa com os que nao existem no mapa 
	 * @return a propria instancia com os valores preenchidos
	 * @throws Exception quando o strictMode é true e temos um valor existente no mapa que não é encontrado na SInstance.
	 */
	public static SInstance mapToSInstace(Map<Integer, Map<String, Object>> pojoReferenceDataMap, Object pojo, SInstance rootInstance, boolean strictMode) throws Exception{
		
		Map<String, Object> mapOfObject = pojoReferenceDataMap.get(System.identityHashCode(pojo));
		
		realMapToSInstance(pojoReferenceDataMap, mapOfObject, rootInstance, strictMode);
		
		return rootInstance;
	}

	@SuppressWarnings("unchecked")
	private static void realMapToSInstance(Map<Integer, Map<String, Object>> pojoReferenceDataMap, Object pojoDataMap, SInstance rootInstance, boolean strictMode) throws Exception{
		SType<?> type = rootInstance.getType();
		if (type.isComposite()){
			SIComposite composite = (SIComposite) rootInstance;
			
			if(strictMode){
				// verifica se os valores existem em ambos os lugares
				List<String> nomesAtributos = new ArrayList<>();
				composite.getAllChildren().forEach(inst->nomesAtributos.add(inst.getType().getNameSimple()));
				Set<String> keySet = ((Map<String, Object>) pojoDataMap).keySet();
				for (String string : keySet) {
					if(!nomesAtributos.contains(string)){
						throw new Exception("Valor existente no mapa não encontrado no SInstance.");
					}
				}
			}
			for (SInstance child :  composite.getAllChildren()){
				Object object = ((Map<String, Object>) pojoDataMap).get(child.getType().getNameSimple());
				// pega o objeto ou mapa que é referenciado 
				
				Map<String, Object> mapNovo = new HashMap<>();
				if(child.getType().isComposite()){
					mapNovo = (Map<String, Object>) object;
				}else{
					// mapa criado pra garantir que teremos a referencia do objeto salva(key) 
					mapNovo.put(child.getType().getNameSimple(), object);
				}
				
				
				 /*Caso ele tenha uma referencia já colocada, ela estará no pojoReferenceDataMap
				 * essa referencia terá atributos repetidos, mas por causa do strict mode, só colocará os que forem
				 * especificados no STYPE chamador, ou seja, nao vai entrar em recursão enchendo a heap se quem chamou nao repetir os atributos*/ 
				// TODO verificar quando tiver referencia circular
				if(object instanceof String && ((String) object).contains("codRef=")){
					String[] split = ((String) object).split("=");
					mapNovo.put(child.getType().getNameSimple(), pojoReferenceDataMap.get(Integer.parseInt(split[split.length-1])));
				}
				realMapToSInstance(pojoReferenceDataMap, mapNovo, child, strictMode);
			}
		} else if (type.isList()){
			
			SIList<SInstance> sIList = (SIList<SInstance>) rootInstance;
			List<Object> list = (List<Object>) ((Map<String, Object>) pojoDataMap).get(type.getNameSimple());
			
			if(list != null){
				while(sIList.size() < list.size()){
					sIList.addNew();
				}
				
				Iterator<SInstance> iterator = sIList.iterator();
				int contador = 0;
				while(iterator.hasNext()){
					realMapToSInstance(pojoReferenceDataMap, list.get(contador), iterator.next(), strictMode);
					contador++;
				}
			}
		} else {
			rootInstance.setValue(((Map<String, Object>) pojoDataMap).get(type.getNameSimple()));
		}
	}
	
	public static SInstance pojoToSInstance(Object objectToConvert, SInstance instance, boolean strictMode) throws Exception{
		Map<Integer, Map<String, Object>> pojoToMap = pojoToMap(objectToConvert);
		return mapToSInstace(pojoToMap, objectToConvert, instance, strictMode);
	}
}
