package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.analysis;

/**
 * The Class InexistentAnnotationException.
 */
public class InexistentAnnotationException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new inexistent annotation exception.
     *
     * @param cause
     *            the cause
     */
    public InexistentAnnotationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new inexistent annotation exception.
     *
     * @param message
     *            the message
     */
    public InexistentAnnotationException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new inexistent annotation exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public InexistentAnnotationException(final String message,
                                         final Throwable cause) {
        super(message, cause);
    }
}
