package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.PackageInfo;
import net.runeduniverse.libs.rogm.logging.Level;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.scanner.PackageScanner;

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
		new PackageScanner().includeOptions(this.loader, this.pkgs, Arrays.asList(scanner), this.validator)
				.enableDebugMode(PACKAGE_SCANNER_DEBUG_MODE || info.getLoggingLevel() != null && info.getLoggingLevel()
						.intValue() < Level.INFO.intValue())
				.scan()
				.throwSurpressions(new ScannerException("Pattern parsing failed! See surpressed Exceptions!"));
	}

	public void addEntry(Class<?> type, IPattern pattern) {
		if (!this.patterns.containsKey(type))
			this.patterns.put(type, new HashSet<>());
		this.patterns.get(type)
				.add(pattern);
	}

	public void logPatterns(Logger logger) {
		StringBuilder msg = new StringBuilder("Archive Pattern Dump");
		this.patterns.forEach((c, patterns) -> {
			msg.append("\n [" + c.getSimpleName() + "] " + c.getCanonicalName());
			msg.append("\n   Pattern:");
			this.appendSetContent(msg, patterns);
		});
		logger.finer(msg.toString());
	}

	private void appendSetContent(StringBuilder builder, Set<?> set) {
		for (Object obj : set) {
			if (obj == null)
				continue;
			Class<?> clazz = obj.getClass();
			builder.append("\n   - [" + clazz.getSimpleName() + "] " + clazz.getCanonicalName());
		}
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
	 * @param entityType
	 * @param anno
	 * @param obj
	 * @return {@code true} if all calls returned successfully
	 */
	public boolean callMethod(Class<?> entityType, Class<? extends Annotation> anno, Object obj, Object... args) {
		boolean success = true;
		for (IPattern pattern : this.patterns.get(entityType))
			if (!pattern.callMethod(anno, obj, args))
				success = false;
		return success;
	}

	public IQueryBuilder<?, ? extends IFilter> search(final Class<?> entityType, boolean lazy) throws Exception {
		IQueryBuilder<?, ?> builder = this.getPattern(entityType, IBaseQueryPattern.class)
				.search(lazy);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.search(builder);
		return builder;
	}

	// search exactly 1 node / querry deeper layers for node
	public IQueryBuilder<?, ? extends IFilter> search(final Class<?> entityType, Serializable id, boolean lazy)
			throws Exception {
		IQueryBuilder<?, ?> builder = this.getPattern(entityType, IBaseQueryPattern.class)
				.search(id, lazy);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.search(builder);
		return builder;
	}

	// TODO reduce to filter
	public ISaveContainer save(final Class<?> entityType, final IBuffer buffer, Object entity, Integer depth)
			throws Exception {
		ISaveContainer container = this.getPattern(entityType, IBaseQueryPattern.class)
				.save(buffer, entity, depth);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.save(container);
		return container;
	}

	// TODO reduce to filter
	public IDeleteContainer delete(final Class<?> entityType, final IBuffer buffer, Object entity) throws Exception {
		IDeleteContainer container = this.getPattern(entityType, IBaseQueryPattern.class)
				.delete(buffer, entity);
		for (IQueryPattern pattern : this.getPatterns(entityType, IQueryPattern.class))
			pattern.delete(container);
		return container;
	}

}
