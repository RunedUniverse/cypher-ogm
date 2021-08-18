package net.runeduniverse.libs.rogm.pipeline.chains;

import java.lang.reflect.Method;
import net.runeduniverse.libs.rogm.pipeline.chains.ChainContainer.Store;

public final class ChainLayer implements ILayer {

	private final Method method;
	private final Class<?>[] paramTypes;
	private final Class<?> returnType;

	protected ChainLayer(Method method) {
		this.method = method;
		this.paramTypes = this.method.getParameterTypes();
		this.returnType = this.method.getReturnType();
	}

	public void call(Store store) throws Exception {
		store.putData(this.returnType, this.method.invoke(null, store.getData(this.paramTypes)));
	}
}
