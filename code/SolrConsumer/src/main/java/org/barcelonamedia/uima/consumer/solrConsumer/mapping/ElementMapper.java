package org.barcelonamedia.uima.consumer.solrConsumer.mapping;

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
