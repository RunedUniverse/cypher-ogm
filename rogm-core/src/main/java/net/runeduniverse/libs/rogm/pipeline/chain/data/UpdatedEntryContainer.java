package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.buffer.BufferTypes.LoadState;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdatedEntryContainer {
	protected Serializable id;
	protected Serializable entityId;
	protected Object entity;
	protected LoadState loadState;
}
