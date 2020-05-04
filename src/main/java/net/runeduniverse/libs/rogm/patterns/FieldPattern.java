package net.runeduniverse.libs.rogm.patterns;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FieldPattern {

	private Field field;
	private DataType type;
	
	
	public FieldPattern(Field field) {
		this.field = field;
		this.type = DataType.fromType(field.getType());
	}
	
	public enum DataType{
		BOOLEAN(long.class),
		SHORT(short.class),
		INTEGER(int.class),
		LONG(long.class),
		UUID(UUID.class),
		STRING(String.class),
		OBJECT(Object.class),
		// specifics
		MAP(HashMap.class),
		SET(HashSet.class),
		LIST(ArrayList.class);
		
		
		@Getter @Setter(value = AccessLevel.PRIVATE)
		private Class<?> objectType = null;
		// specifics
		@Getter @Setter(value = AccessLevel.PRIVATE)
		private DataType keyType = null;
		@Getter @Setter(value = AccessLevel.PRIVATE)
		private DataType valueType = null;
		
		private DataType(Class<?> c) {
			this.objectType = c;
		}
		
		private DataType setObjectType(Class<?> c) {
			this.objectType = c;
			return this;
		}

		// TODO: rework for specific types needed
		// ParameterizedType type = (ParameterizedType) field.getGenericType();
		static DataType fromType(Class<?> type) {
			if(type==boolean.class||type==Boolean.class)
			return BOOLEAN;
			if(type==short.class||type==Short.class)
				return SHORT;
			if(type==int.class||type==Integer.class)
				return INTEGER;
			if(type==long.class||type==Long.class)
				return LONG;
			if(type==UUID.class)
				return UUID;
			if(type==String.class)
				return STRING;
			if(type.isAssignableFrom(Map.class)) {
				DataType d = MAP;
				return d;
			}
			if(type.isAssignableFrom(Set.class))
				return SET;
			if(type.isAssignableFrom(List.class))
				return LIST;
			
			return OBJECT.setObjectType(type);
		}
	}
}
