package net.runeduniverse.libs.rogm.querying.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFilter;

@RequiredArgsConstructor
public class NodeFilter implements IFilter, InvocationHandler {

	@Getter
	private final FilterType filterType;
	private final Map<Class<?>, Object> handler;

	public Class<?>[] gatherInterfaces() {
		Set<Class<?>> l = new HashSet<>();
		l.add(IFilter.class);
		this.handler.forEach((c, i) -> {
			if (i != null)
				l.add(c);
		});
		return l.toArray(new Class<?>[l.size()]);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (has(IFilter.class, method))
			return method.invoke(this, args);
		
		for (Class<?> clazz : this.handler.keySet()) {
			if (has(clazz, method))
				return method.invoke(this.handler.get(clazz), args);
		}
		return new Exception("Interface not found!");
	}

	private static boolean has(Class<?> c, Method method) {
		return Arrays.asList(c.getMethods())
				.contains(method);
	}

}
