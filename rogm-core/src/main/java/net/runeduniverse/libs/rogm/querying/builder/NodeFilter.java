package net.runeduniverse.libs.rogm.querying.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IIdentified;
import net.runeduniverse.libs.rogm.querying.ILabeled;
import net.runeduniverse.libs.rogm.querying.IParameterized;

@RequiredArgsConstructor
public class NodeFilter implements IFilter, InvocationHandler {

	@Getter
	private final FilterType filterType;

	private final LabeledHandler labeledHandler;
	private final IdentifiedHandler identifiedHandler;
	private final ParamHandler paramHandler;

	public Class<?>[] gatherInterfaces() {
		List<Class<?>> l = new ArrayList<>();
		l.add(IFilter.class);
		if (this.labeledHandler != null)
			l.add(ILabeled.class);
		if (this.identifiedHandler != null)
			l.add(IIdentified.class);
		if (this.paramHandler != null)
			l.add(IParameterized.class);
		return l.toArray(new Class<?>[l.size()]);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (has(IFilter.class, method))
			return method.invoke(this, args);
		if (has(ILabeled.class, method))
			return method.invoke(this.labeledHandler, args);
		if (has(IIdentified.class, method))
			return method.invoke(this.identifiedHandler, args);
		if (has(IParameterized.class, method))
			return method.invoke(this.paramHandler, args);
		return new Exception("Interface not found!");
	}

	private static boolean has(Class<?> c, Method method) {
		return Arrays.asList(c.getMethods())
				.contains(method);
	}

}
