package net.runeduniverse.libs.rogm.error;

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
