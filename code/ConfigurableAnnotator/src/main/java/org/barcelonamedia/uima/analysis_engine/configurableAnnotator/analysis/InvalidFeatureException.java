package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis;

/**
 * The Class InvalidFeatureException.
 */
public class InvalidFeatureException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new invalid feature exception.
     *
     * @param cause
     *            the cause
     */
    public InvalidFeatureException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new invalid feature exception.
     *
     * @param message
     *            the message
     */
    public InvalidFeatureException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new invalid feature exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public InvalidFeatureException(final String message,
                                   final Throwable cause) {
        super(message, cause);
    }
}
