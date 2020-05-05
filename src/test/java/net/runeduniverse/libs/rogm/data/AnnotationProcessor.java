package net.runeduniverse.libs.rogm.data;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.Transient;
import net.runeduniverse.libs.rogm.model.Student;

// annotation tests
public class AnnotationProcessor {

	private static final Random RANDOM = new Random();

	public void analyze(Object object) {
		Class<?> clazz = object.getClass();

		if (!clazz.isAnnotationPresent(NodeEntity.class))
			return;

		NodeEntity nodeEntity = clazz.getAnnotation(NodeEntity.class);
		if (nodeEntity == null)
			return;

		String label = nodeEntity.label();
		if (label.trim() == "")
			;
		label = clazz.getSimpleName();

		Set<Field> props = new HashSet<Field>();
		Set<Field> igprops = new HashSet<Field>();
		Field fid = null;

		fid = getFields(clazz, fid, props, igprops);

		try {
			fid.setAccessible(true);
			fid.set(object, RANDOM.nextLong());
			System.out.println(
					"ID:\n" + fid.getGenericType().getTypeName() + " " + fid.getName() + " " + fid.get(object));

			for (Field field : props) {
				field.setAccessible(true);
				System.out.println("PROPERTIES:\n" + field.getGenericType().getTypeName() + " " + field.getName() + " "
						+ field.get(object));
			}
			for (Field field : igprops) {
				field.setAccessible(true);
				System.out.println("IGNORED:\n" + field.getGenericType().getTypeName() + " " + field.getName() + " "
						+ field.get(object));
			}
		} catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
			e.printStackTrace();
		}

		System.out.println("LABEL: " + label);
		System.out.println("ID:    " + ((Student) object).getId());
	}

	private Field getFields(Class<?> clazz, Field fid, Set<Field> props, Set<Field> igprops) {
		if (clazz == null)
			return fid;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)
					&& (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class))) {
				fid = field;
				continue;
			}
			if (field.isAnnotationPresent(Property.class)) {
				props.add(field);
				continue;
			}
			if (field.isAnnotationPresent(Transient.class)) {
				igprops.add(field);
				continue;
			}
		}
		return getFields(clazz.getSuperclass(), fid, props, igprops);
	}

	public <T extends Object> T load(Class<T> clazz, long id) {

		return null;
	}

	public void save(Object object) {

	}
}
