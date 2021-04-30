package net.runeduniverse.libs.rogm.pipeline.chains;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pipeline.Chain;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class LoadChain {
	public static final String LABEL = Chain.Load.LABEL;

	@Getter
	public static class Data<T, ID extends Serializable> extends AChainData {
		public Data(Pipeline pipeline) {
			super(pipeline);
		}

		Class<T> type = null;
		ID id = null;
		int depth = 1;
		IFilter filter = null;
		Set<T> returnValues = new HashSet<>();
	}

	// BUFFER_REQUEST
	@SuppressWarnings("null")
	@Chain(label = LABEL, layer = Chain.Load.BUFFER_REQUEST)
	public <T, ID extends Serializable> void checkBuffer(Data<T, ID> data) {
		IBuffer buffer = null;
		if (data.getType() == null || data.getId() == null)
			return;
		data.getReturnValues()
				.add(data.getDepth() == 0 ? buffer.getByEntityId(data.getId(), data.getType())
						: buffer.getCompleteByEntityId(data.getId(), data.getType()));

		if (!data.returnValues.isEmpty())
			data.commit();
	}

	// BUILD_FILTER
	@Chain(label = LABEL, layer = Chain.Load.BUILD_FILTER)
	public <T, ID extends Serializable> void buildFilter(Data<T, ID> data) {
		if (data.getId() == null) {
			// without id
		} else {
			// with id
		}
	}
}
