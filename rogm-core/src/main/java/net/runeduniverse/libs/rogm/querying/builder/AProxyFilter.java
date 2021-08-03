package net.runeduniverse.libs.rogm.querying.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.error.ExceptionSurpression;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.ILabeled;

public abstract class AProxyFilter<FILTER> implements IFilter, ILabeled, InvocationHandler {
	protected FILTER instance;

	@Getter
	protected final Map<Class<?>, Object> handler = new HashMap<>();
	protected final Map<Method, Object> methodHandlerMapper = new HashMap<>();
	@Getter
	@Setter
	protected FilterType filterType = FilterType.MATCH;
	@Getter
	protected Set<String> labels = new HashSet<>();

	public FILTER addLabel(String label) {
		this.labels.add(label);
		return this.instance;
	}

	public FILTER addLabels(Collection<String> labels) {
		this.labels.addAll(labels);
		return this.instance;
	}

	public Class<?>[] buildInvocationHandler() {
		Set<Class<?>> l = new HashSet<>();
		l.add(IFilter.class);
		for (Class<?> i : this.instance.getClass()
				.getInterfaces())
			l.add(i);

		this.handler.forEach((c, i) -> {
			if (i != null)
				l.add(c);
		});

		this.methodHandlerMapper.clear();
		// PUT ORDER > LAST ADDED => PERSISTS
		// 1. exten
		for (Class<?> clazz : this.handler.keySet()) {
			Object methodHandler = this.handler.get(clazz);
			for (Method m : clazz.getMethods())
				this.methodHandlerMapper.put(m, methodHandler);
		}
		// 2. this.instance Object
		for (Method m : this.instance.getClass()
				.getMethods())
			this.methodHandlerMapper.put(m, this.instance);
		// 3. this.instance Interfaces [IFilter, ILabeled, InvocationHandler, ...]
		for (Class<?> clazz : this.instance.getClass()
				.getInterfaces()) {
			for (Method m : clazz.getMethods())
				this.methodHandlerMapper.put(m, this.instance);
		}
		return l.toArray(new Class<?>[l.size()]);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {

			if (this.methodHandlerMapper.containsKey(method))
				return method.invoke(this.methodHandlerMapper.get(method), args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw AProxyFilter.surpressErr(this, proxy, method, args, e);
		}
		throw new Exception("Interface for Method<" + method + "> not found!");
	}

	private static Throwable surpressErr(AProxyFilter<?> instance, Object proxy, Method method, Object[] args,
			Throwable throwable) {
		String msg = "QueryBuilder > AProxyFilter > invoke()\n  Proxy: " + proxy + "\n  Method: " + method
				+ (args == null ? "" : "\n  args[" + args.length + ']') + ")\n  registered Interfaces:";
		for (Class<?> c : instance.handler.keySet())
			msg += "\n  - " + c.getCanonicalName() + " > " + instance.handler.get(c);
		msg += "\n  Mapper:";
		for (Method m : instance.methodHandlerMapper.keySet())
			msg += "\n  - " + m + "\n    '-> " + instance.methodHandlerMapper.get(m);

		ExceptionSurpression surpression = new ExceptionSurpression(msg);
		surpression.addSuppressed(throwable);
		return surpression;
	}
}
