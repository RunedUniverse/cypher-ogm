package net.runeduniverse.libs.rogm.lang.cypher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FilterStatus {
	public static final FilterStatus INITIALIZED = new FilterStatus(1);
	public static final FilterStatus PRE_PRINTED = new FilterStatus(2);
	public static final FilterStatus PRINTED = new FilterStatus(3);
	public static final FilterStatus EXTENSION_PRINTED = new FilterStatus(4);

	@Getter
	private int status = 0;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterStatus)
			return this.status >= ((FilterStatus) obj).getStatus();
		return super.equals(obj);
	}
}