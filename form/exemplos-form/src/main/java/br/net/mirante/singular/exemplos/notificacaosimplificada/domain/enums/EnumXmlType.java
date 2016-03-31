package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum EnumXmlType {
      CHAR(Character.class),
		STRING(String.class),
		FLOAT(Float.class),
		DOUBLE(Double.class),
		INTEGER(Integer.class),
		SHORT(Short.class),
		BOOLEAN(Boolean.class),
		BYTE(Byte.class),
		LONG(Long.class);
		
		@SuppressWarnings("rawtypes")
		private EnumXmlType(Class clazz) {
			this.type = clazz;			
		}		
		@SuppressWarnings("rawtypes")
		public Class type;
}
