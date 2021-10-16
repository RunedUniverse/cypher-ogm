package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IdContainer {
	private Serializable id;
}
