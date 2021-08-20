package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Result<R> {
	private final Class<R> type;
	private R result;

	public boolean hasResult() {
		return result != null;
	}

	@SuppressWarnings("unchecked")
	public Result<R> setResult(Object result) {
		this.result = (R) result;
		return this;
	}
}