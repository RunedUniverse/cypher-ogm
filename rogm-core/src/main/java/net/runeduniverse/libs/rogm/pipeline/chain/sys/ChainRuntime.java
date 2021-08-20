package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("deprecation")
public class ChainRuntime<R> {

	@Getter
	protected final ChainRuntime<?> root;
	protected final ChainContainer container;
	protected final Store store;
	protected final Result<R> result;

	@Getter
	@Setter
	protected boolean canceled = false;

	protected ChainRuntime(ChainContainer container, Class<R> resultType, Object[] args) {
		this(null, container, resultType, null, args);
	}

	protected ChainRuntime(ChainRuntime<?> root, ChainContainer container, Class<R> resultType,
			Map<Class<?>, Object> sourceDataMap, Object[] args) {
		this.root = root;
		this.container = container;
		this.store = new Store(sourceDataMap, args);
		this.result = new Result<R>(resultType);

		this.store.putData(Result.class, this.result);
	}

	public <S> S callSubChain(String label, Class<S> resultType, Object... args) throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, null, args);
	}

	public <S> S callSubChainWithSourceData(String label, Class<S> resultType, Object... args) throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, this.store.getSourceDataMap(), args);
	}

	public <S> S callSubChainWithRuntimeData(String label, Class<S> resultType, Object... args) throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, this.store.getRuntimeDataMap(), args);
	}

	public void storeData(Class<?> type, Object entity) {
		if (entity instanceof ChainRuntime<?> || entity instanceof Result<?>)
			return;
		if (entity instanceof Store)
			return;

		this.store.putData(type, entity);
	}

	public boolean setPossibleResult(Object entity) {
		if (this.result.getType()
				.isAssignableFrom(entity.getClass())) {
			this.result.setResult(entity);
			return true;
		}
		return false;
	}

	public Object[] getParameters(Class<?>[] paramTypes) {
		return this.store.getData(paramTypes);
	}

	public boolean active() {
		if (this.canceled || this.hasResult())
			return false;
		return true;
	}

	public boolean isRoot() {
		return this.root == null;
	}

	public boolean hasResult() {
		return this.result.hasResult();
	}

	@SuppressWarnings("unchecked")
	protected R getFinalResult() {
		if (this.canceled)
			return null;

		Class<R> resultType = this.result.getType();
		if (resultType == null)
			return (R) store.getLastAdded();
		else if (result.hasResult())
			return result.getResult();
		return store.getData(resultType);
	}

}
