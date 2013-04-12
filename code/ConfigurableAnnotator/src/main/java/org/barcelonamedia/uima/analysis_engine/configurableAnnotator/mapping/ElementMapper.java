package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

import org.xml.sax.Attributes;

/**
 * The Interface ElementMapper.
 */
public interface ElementMapper<T> {

    /**
     * Map element.
     *
     * @param attributes
     *            The element attributes.
     *
     * @return the t
     */
    T mapElement(Attributes attributes);
}
