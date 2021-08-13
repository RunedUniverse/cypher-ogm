package net.runeduniverse.libs.rogm.error;

import java.util.Collection;

public class ExceptionSurpression extends Exception {
	private static final long serialVersionUID = 215607527815606000L;

	public ExceptionSurpression(String message) {
		super(message);
	}

	public ExceptionSurpression addSuppressed(Collection<Exception> errors) {
		for (Exception e : errors)
			this.addSuppressed(e);
		return this;
	}
}
