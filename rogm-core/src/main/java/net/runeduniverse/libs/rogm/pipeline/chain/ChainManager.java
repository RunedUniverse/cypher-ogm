package net.runeduniverse.libs.rogm.pipeline.chain;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.runeduniverse.libs.rogm.pipeline.chain.sys.BaseChainLayer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainContainer;
import static net.runeduniverse.libs.utils.StringUtils.*;

public final class ChainManager {

	private static final Map<String, ChainContainer> chains = new HashMap<>();

	public static void addChainLayers(Class<?> carrierClass) {
		for (Method method : carrierClass.getMethods()) {
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

	public static <R> R callChain(String label, Class<R> resultType, Object... args) throws Exception {
		ChainContainer container = ChainManager.chains.get(label);
		if (container == null)
			return null;
		return container.call(resultType, args);
	}

	private static ChainContainer _getChain(String label) {
		ChainContainer c = ChainManager.chains.get(label);
		if (c != null)
			return c;
		c = new ChainContainer(label);
		ChainManager.chains.put(label, c);
		return c;
	}
}
