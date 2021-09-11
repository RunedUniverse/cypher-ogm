package net.runeduniverse.libs.rogm.error;

public class ChainLayerCallException extends ATrunkableException {
	private static final long serialVersionUID = -6315371891932847527L;

	public ChainLayerCallException(String message, Throwable cause) {
		super(message, cause, true);
	}
}
