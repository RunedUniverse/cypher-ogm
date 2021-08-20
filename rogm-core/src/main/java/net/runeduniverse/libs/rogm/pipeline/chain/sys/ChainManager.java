package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.runeduniverse.libs.utils.StringUtils.*;

public final class ChainManager {

	private final Set<Method> existingMethods = new HashSet<>();
	private final Map<String, ChainContainer> chains = new HashMap<>();

	public void addChainLayers(Class<?> carrierClass) {

		for (Method method : carrierClass.getMethods()) {
			if (this.existingMethods.contains(method))
				continue;
			this.existingMethods.add(method);
			int mods = method.getModifiers();
			if (Modifier.isAbstract(mods) || !Modifier.isStatic(mods) || !Modifier.isPublic(mods))
				continue;
			BaseChainLayer layer = new BaseChainLayer(method);
			for (Chain anno : method.getAnnotationsByType(Chain.class)) {
				if (isBlank(anno.label()) || anno.layers() == null)
					continue;
				_getChain(anno.label()).putAtLayers(anno.layers(), layer);
			}
		}
	}

	public <R> R callChain(String label, Class<R> resultType, Object... args) throws Exception {
		ChainContainer container = this.chains.get(label);
		if (container == null)
			return null;
		return container.callChain(resultType, args);
	}

	protected <R> R callChain(String label, Class<R> resultType, ChainRuntime<?> rootRuntime,
			Map<Class<?>, Object> sourceDataMap, Object... args) throws Exception {
		ChainContainer container = this.chains.get(label);
		if (container == null)
			return null;
		return container.callChain(resultType, rootRuntime, sourceDataMap, args);
	}

	private ChainContainer _getChain(String label) {
		ChainContainer c = this.chains.get(label);
		if (c != null)
			return c;
		c = new ChainContainer(this, label);
		this.chains.put(label, c);
		return c;
	}
}
