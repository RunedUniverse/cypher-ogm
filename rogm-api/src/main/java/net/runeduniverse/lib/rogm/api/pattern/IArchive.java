/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.rogm.api.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.api.annotations.IConverter;
import net.runeduniverse.lib.rogm.api.container.IDeleteContainer;
import net.runeduniverse.lib.rogm.api.container.ISaveContainer;
import net.runeduniverse.lib.rogm.api.errors.ScannerException;
import net.runeduniverse.lib.rogm.api.info.PackageInfo;
import net.runeduniverse.lib.rogm.api.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.IQueryBuilderInstance;
import net.runeduniverse.lib.rogm.api.querying.QueryBuilder;
import net.runeduniverse.lib.utils.scanner.ITypeScanner;

public interface IArchive {
	public PackageInfo getInfo();

	public IdTypeResolver getIdTypeResolver();

	public QueryBuilder getQueryBuilder();

	public void scan(ITypeScanner... scanner) throws ScannerException;

	public void addEntry(Class<?> type, IPattern pattern);

	public void logPatterns(Logger logger);

	public Set<IPattern> getPatterns(Class<?> type);

	public <P extends IPattern> Set<P> getPatterns(Class<?> entityType, Class<P> patternType);

	public <P extends IPattern> P getPattern(Class<?> entityType, Class<P> patternType);

	public IPattern getPattern(Class<?> entityType, Class<?>... patternTypes);

	public boolean isIdSet(Object entity);

	public Object setId(Object entity, Serializable id);

	public IConverter<?> getIdFieldConverter(Class<?> type);

	public boolean callMethod(Class<?> entityType, Class<? extends Annotation> anno, Object obj, Object... args);

	public IQueryBuilderInstance<?, ?, ? extends IFilter> search(final Class<?> entityType, boolean lazy)
			throws Exception;

	public IQueryBuilderInstance<?, ?, ? extends IFilter> search(final Class<?> entityType, Serializable id,
			boolean lazy) throws Exception;

	public ISaveContainer save(final Class<?> entityType, Object entity, Integer depth) throws Exception;

	public IDeleteContainer delete(final Class<?> entityType, final Serializable id, Object entity) throws Exception;
}
