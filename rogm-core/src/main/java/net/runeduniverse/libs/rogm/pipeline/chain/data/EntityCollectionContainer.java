package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.ToString;

@ToString
public class EntityCollectionContainer {

	@Getter
	private final Collection<Object> entityCollection;

	public EntityCollectionContainer(Object... entities) {
		this.entityCollection = new ArrayList<>();
		for (Object entity : entities)
			this.entityCollection.add(entity);
	}

	public EntityCollectionContainer(EntityContainer entity) {
		this.entityCollection = new ArrayList<>();
	}

	public EntityCollectionContainer(Collection<Object> entityCollection) {
		this.entityCollection = entityCollection;
	}

	public boolean add(Object entity) {
		return this.entityCollection.add(entity);
	}

	public boolean add(EntityContainer entityContainer) {
		return this.entityCollection.add(entityContainer.getEntity());
	}

	public boolean addAll(Collection<Object> entities) {
		return this.entityCollection.addAll(entities);
	}

	public boolean addAll(EntityCollectionContainer entitiesContainer) {
		return this.entityCollection.addAll(entitiesContainer.getEntityCollection());
	}

	public boolean contains(Object entity) {
		return this.entityCollection.contains(entity);
	}

	public boolean contains(EntityContainer entityContainer) {
		return this.entityCollection.contains(entityContainer.getEntity());
	}
}
