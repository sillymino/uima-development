package org.barcelonamedia.uima.consumer.solrConsumer.analysis;

import java.util.Collection;

import org.barcelonamedia.uima.consumer.solrConsumer.types.Document;
import org.barcelonamedia.uima.consumer.solrConsumer.types.Field;


/**
 * The Class DocumentBuilder.
 */
public class DocumentBuilder {

    /**
     * Instantiates a new document builder.
     */
    public DocumentBuilder() {
        super();
    }

    /**
     * Creates the document.
     *
     * @param fields
     *            The fields to add to the document.
     *
     * @return the document
     */
    public final Document createDocument(final Collection<Field> fields) {
        Document document = new Document();

        for (Field field : fields) {
            document.add(field);
        }

        return document;
    }
}
