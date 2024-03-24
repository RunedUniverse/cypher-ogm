/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import lombok.Getter;
import net.runeduniverse.lib.rogm.annotations.IConverter;
import net.runeduniverse.lib.rogm.annotations.Id;
import net.runeduniverse.lib.rogm.errors.ScannerException;
import net.runeduniverse.lib.rogm.info.PackageInfo;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.lib.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.lib.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.IQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder;
import net.runeduniverse.lib.utils.logging.Level;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;
import net.runeduniverse.lib.utils.scanner.PackageScanner;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public final class Archive {
	public static boolean PACKAGE_SCANNER_DEBUG_MODE = false;

	private final Map<Class<?>, Set<IPattern>> patterns = new HashMap<>();
	private final Set<String> pkgs = new HashSet<>();
	private final Set<ClassLoader> loader = new HashSet<>();
	@Getter
	private final PackageInfo info;
	@Getter
	private final IdTypeResolver idTypeResolver;
	@Getter
	private final QueryBuilder queryBuilder;
	private final PackageScanner.Validator validator = new PackageScanner.Validator() {

		@Override
		public void validate() throws Exception {
			IValidatable.validate(patterns.values());
		}
	};

	public Archive(final PackageInfo info, IdTypeResolver idTypeResolver) {
		this.info = info;
		this.pkgs.addAll(this.info.getPkgs());
		this.loader.addAll(this.info.getLoader());
		this.idTypeResolver = idTypeResolver;
		this.queryBuilder = new QueryBuilder(this);
	}

	public void scan(TypeScanner... scanner) throws ScannerException {
		try {
			new PackageScanner().includeOptions(this.loader, this.pkgs, Arrays.asList(scanner), this.validator)
					.enableDebugMode(
							PACKAGE_SCANNER_DEBUG_MODE || info.getLoggingLevel() != null && info.getLoggingLevel()
									.intValue() < Level.INFO.intValue())
					.scan()
					.throwSurpressions();
		} catch (Exception e) {
			throw new ScannerException("Pattern parsing failed! See surpressed Exceptions!", e);
		}
	}

	public void addEntry(Class<?> type, IPattern pattern) {
		if (!this.patterns.containsKey(type))
			this.patterns.put(type, new HashSet<>());
		this.patterns.get(type)
				.add(pattern);
	}

	public void logPatterns(Logger logger) {
		CompoundTree tree = new CompoundTree("Archive Pattern Dump");

		this.loader.forEach(l -> tree.append("LOADER", l.getClass()
				.getCanonicalName()));

		this.patterns.forEach((c, patterns) -> {
			CompoundTree clazz = new CompoundTree("CLASS", c.getCanonicalName());
			patterns.forEach(p -> {
				if (p == null)
					return;
				CompoundTree pattern = new CompoundTree(p.getPatternType()
						.toString(),
						p.getClass()
								.getCanonicalName());

				if (p instanceof TypePattern<?, ?>) {
					TypePattern<?, ?> tp = (TypePattern<?, ?>) p;
					pattern.append("PKG", tp.getPkg())
							.append("TYPE", tp.getType()
									.getCanonicalName())
							.append("SUPER TYPE", tp.getSuperType()
									.getCanonicalName());
					if (!p.getLabels()
							.isEmpty())
						pattern.append("LABELS", String.join(", ", p.getLabels()));

					tp.mapFields()
							.forEach((f, s) -> {
								CompoundTree annos = new CompoundTree("FIELD", f.getClass()
										.getCanonicalName()).append("NAME", f.getField()
												.getName())
												.append("TYPE", f.getType()
														.getCanonicalName());
								if (f instanceof RelatedFieldPattern) {
									String label = ((RelatedFieldPattern) f).getLabel();
									if (label != null)
										annos.append("LABEL", label);
								}
								s.forEach(a -> {
									annos.append("ANNO", '@' + a.getSimpleName());
								});
								pattern.append(annos);
							});
					tp.mapMethods()
							.forEach((m, s) -> {
								CompoundTree annos = new CompoundTree("METHOD", m.getMethod()
										.getName());
								s.forEach(a -> {
									annos.append("ANNO", '@' + a.getSimpleName());
								});
								pattern.append(annos);
							});
				}
				clazz.append(pattern);
			});
			tree.append(clazz);
		});

		logger.config(tree.toString());
	}

	// QUERRYING
	public Set<IPattern> getPatterns(Class<?> type) {
		return this.patterns.get(type);
	}

	@SuppressWarnings("unchecked")
	public <P extends IPattern> Set<P> getPatterns(Class<?> entityType, Class<P> patternType) {
		Set<P> rPatterns = new HashSet<>();
		for (IPattern pattern : this.patterns.get(entityType))
			if (patternType.isInstance(pattern))
				rPatterns.add((P) pattern);

		return rPatterns;
	}

	public <P extends IPattern> P getPattern(Class<?> entityType, Class<P> patternType) {
		if (this.patterns.containsKey(entityType))
			return this._getPattern(entityType, patternType);

		for (Class<?> key : this.patterns.keySet())
			if (entityType.isInstance(key))
				return this._getPattern(key, patternType);
		return null;
	}

	public IPattern getPattern(Class<?> entityType, Class<?>... patternTypes) {
		for (IPattern pattern : this.patterns.get(entityType))
			for (int i = 0; i < patternTypes.length; i++)
				if (patternTypes[i].isInstance(pattern))
					return pattern;
		return null;
	}

	@SuppressWarnings("unchecked")
	private <P extends IPattern> P _getPattern(Class<?> entityType, Class<P> patternType) {
		for (IPattern pattern : this.patterns.get(entityType))
			if (patternType.isInstance(pattern))
				return (P) pattern;
		return null;
	}

	// TODO move to needed pos
	public boolean isIdSet(Object entity) {
		try {
			return this.getPattern(entity.getClass(), IBaseQueryPattern.class)
					.isIdSet(entity);
		} catch (Exception e) {
			// this.logger.burying("isIdSet(Object)", e);
			return false;
		}
	}

	// TODO move to needed pos
	public Object setId(Object entity, Serializable id) {
		try {
			return this.getPattern(entity.getClass(), IBaseQueryPattern.class)
					.setId(entity, id);
		} catch (Exception e) {
			// this.logger.burying("setId(Object, Serializable)", e);
		}
		return entity;
	}

	public IConverter<?> getIdFieldConverter(Class<?> type) {
		IConverter<?> converter = null;
		for (IPattern p : this.patterns.get(type))
			if (p.getField(Id.class) != null) {
				converter = p.getField(Id.class)
						.getConverter();
				break;
			}
		return IConverter.validate(converter);
	}

	/**
	 * Used to call all parsed Methods matching the Class
	 *
	 * @param entityType {@link Class} of the entity
	 * @param anno       {@link Annotation} by which the method can be identified
	 * @param obj        {@link Object} which has the method
	 * @param args       {@link Object} array which gets passed to the method
	 * @return {@code true} if all calls returned successfully
	 */
	public boolean callMethod(Class<?> entityType, Class<? extends Annotation> anno, Object obj, Object... args) {
		boolean success = true;
		for (IPattern pattern : this.patterns.get(entityType))
			if (!pattern.callMethod(anno, obj, args))
				success = false;
		return success;
	}

	public IQueryBuilder<?, ?, ? extends IFilter> search(final Class<?> entityType, boolean lazy) throws Exception {
		IQueryBuilder<?, ?, ?> builder = this.getPattern(entityType, IBaseQueryPattern.class)
				.search(lazy);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.search(builder);
		return builder;
	}

	// search exactly 1 node / querry deeper layers for node
	public IQueryBuilder<?, ?, ? extends IFilter> search(final Class<?> entityType, Serializable id, boolean lazy)
			throws Exception {
		IQueryBuilder<?, ?, ?> builder = this.getPattern(entityType, IBaseQueryPattern.class)
				.search(id, lazy);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.search(builder);
		return builder;
	}

	public SaveContainer save(final Class<?> entityType, Object entity, Integer depth) throws Exception {
		SaveContainer container = this.getPattern(entityType, IBaseQueryPattern.class)
				.save(entity, depth);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.save(container);
		return container;
	}

	// TODO reduce to filter
	public IDeleteContainer delete(final Class<?> entityType, final Serializable id, Object entity) throws Exception {
		IDeleteContainer container = this.getPattern(entityType, IBaseQueryPattern.class)
				.delete(id, entity);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.delete(container);
		return container;
	}

}
