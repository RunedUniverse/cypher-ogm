package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.runeduniverse.libs.rogm.error.ChainLayerCallException;

public class BaseChainLayer implements ILayer {

	private final Method method;
	private final Class<?>[] paramTypes;
	private final Class<?> returnType;

	public BaseChainLayer(Method method) {
		this.method = method;
		this.paramTypes = this.method.getParameterTypes();
		this.returnType = this.method.getReturnType();
	}

	public void call(ChainRuntime<?> runtime) throws ChainLayerCallException {
		Object[] params = null;
		try {
			params = runtime.getParameters(this.paramTypes);
			runtime.storeData(this.returnType, this.method.invoke(null, params));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw this.packageException(e, params);
		}
	}

	public ChainLayer asChainLayer(Chain chain) {
		return new ChainLayer(this, chain);
	}

	private ChainLayerCallException packageException(Exception e, Object[] passedParams) {
		StringBuilder msg = new StringBuilder(this.method.getDeclaringClass()
				.getName());
		msg.append(": " + this.returnType.getSimpleName() + ' ' + this.method.getName());
		List<String> params = new ArrayList<>();
		for (int i = 0; i < paramTypes.length; i++)
			msg.append(paramTypes[i].getName() + " » "
					+ (passedParams[i] == null ? null : passedParams[i].getClass()).getName());
		msg.append('(' + String.join(", ", params) + ')');
		return new ChainLayerCallException(msg.toString(), e);
	}
}
