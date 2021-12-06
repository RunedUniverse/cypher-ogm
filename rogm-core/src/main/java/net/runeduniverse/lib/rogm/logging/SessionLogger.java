/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.rogm.logging;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.runeduniverse.lib.rogm.info.SessionInfo;
import net.runeduniverse.libs.logging.ALogger;

public final class SessionLogger extends ALogger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public SessionLogger(Class<?> clazz, PipelineLogger pipelineLogger) {
		super("ROGM", null, pipelineLogger);
		prefix = "[" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}

	public SessionLogger logSessionInfo(final SessionInfo info) {
		super.log(Level.CONFIG, this.prefix + '\n' + info.toString());
		return this;
	}
}
