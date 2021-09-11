package net.runeduniverse.libs.rogm.error;

import java.util.Collection;

public class ExceptionSurpression extends ATrunkableException {
	private static final long serialVersionUID = 215607527815606000L;

	public ExceptionSurpression(String message) {
		super(message);
	}

	public ExceptionSurpression(String message, boolean trunk) {
		super(message, trunk);
	}

	public ExceptionSurpression addSuppressed(Collection<Exception> errors) {
		for (Exception e : errors)
			this.addSuppressed(e);
		return this;
	}
}
