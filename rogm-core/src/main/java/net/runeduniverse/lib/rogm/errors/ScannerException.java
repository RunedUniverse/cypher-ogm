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
package net.runeduniverse.lib.rogm.errors;

import net.runeduniverse.lib.utils.errors.ExceptionSuppressions;

public class ScannerException extends ExceptionSuppressions {
	private static final long serialVersionUID = 4295891405650593671L;

	public ScannerException(String message) {
		super(message, true);
	}

	public ScannerException(String message, Exception exSuppression) {
		super(message, true);
		this.initCause(exSuppression);
		for (Throwable t : exSuppression.getSuppressed())
			this.addSuppressed(t);
	}
}
