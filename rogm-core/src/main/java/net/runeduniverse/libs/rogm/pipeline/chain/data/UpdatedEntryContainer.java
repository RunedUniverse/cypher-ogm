package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.LoadState;

@AllArgsConstructor
@Getter
@Setter
public class UpdatedEntryContainer {
	protected Serializable id;
	protected Serializable entityId;
	protected Object entity;
	protected LoadState loadState;
}
