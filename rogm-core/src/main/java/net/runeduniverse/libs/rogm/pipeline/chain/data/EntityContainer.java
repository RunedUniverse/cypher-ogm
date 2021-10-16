package net.runeduniverse.libs.rogm.pipeline.chain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EntityContainer {

	private Object entity = null;

	public Class<?> getType() {
		return this.entity.getClass();
	}
}
