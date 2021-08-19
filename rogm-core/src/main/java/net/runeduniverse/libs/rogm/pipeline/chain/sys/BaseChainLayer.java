package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.lang.reflect.Method;

import net.runeduniverse.libs.rogm.pipeline.chain.Chain;

public class BaseChainLayer implements ILayer {

	private final Method method;
	private final Class<?>[] paramTypes;
	private final Class<?> returnType;

	public BaseChainLayer(Method method) {
		this.method = method;
		this.paramTypes = this.method.getParameterTypes();
		this.returnType = this.method.getReturnType();
	}

	public void call(Store store) throws Exception {
		store.putData(this.returnType, this.method.invoke(null, store.getData(this.paramTypes)));
	}

	public ChainLayer asChainLayer(Chain chain) {
		return new ChainLayer(this, chain);
	}
}
