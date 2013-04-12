package org.barcelonamedia.uima.consumer.solrConsumer.posting;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.barcelonamedia.uima.consumer.solrConsumer.types.Document;
import org.barcelonamedia.uima.consumer.solrConsumer.types.Field;

/**
 * The Class XMLWriter.
 */
public final class XMLWriter {

    /**
     * Disable external instantiation.
     */
    private XMLWriter() {
        // Disable external instantiation.
    }

    /**
     * Write doc.
     *
     * @param doc
     *            the doc
     * @param metadataHash
     *            the metadata_hash
     * @param writer
     *            the writer
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeDoc(final Document doc,
                                final Map<String, String> metadataHash,
                                final Writer writer)
            throws IOException {
        
        writer.write("<add>\n");
        writer.write("\t<doc>\n");
        if (metadataHash != null) {
            for (Entry<String, String> e : metadataHash.entrySet()) {
                Field f = new Field(e.getKey(), e.getValue());

                writeField(writer, f, "field", true);
            }
        }

        for (Object obj : doc.getFields()) {
            writeField(writer, (Field) obj, "field", true);
        }
        writer.write("\t</doc>\n");
        writer.write("</add>\n");
    }

    /**
     * Write doc.
     *
     * @param doc
     *            the doc
     * @param writer
     *            the writer
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeDoc(final Document doc,
                                final Writer writer)
            throws IOException {
        writeDoc(doc, null, writer);
    }

    /**
     * Write field.
     *
     * @param writer
     *            the writer
     * @param field
     *            the field
     * @param tag
     *            the tag
     * @param escape
     *            the escape
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void writeField(final Writer writer,
                                   final Field field,
                                   final String tag,
                                   final boolean escape)
            throws IOException {

        String fieldName = field.name();
        String val = field.stringValue();

        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put("name", fieldName);

        writer.write("\t\t");
        if (escape) {
            XML.writeXML(writer, tag, val, attrs);
        } else {
            XML.writeUnescapedXML(writer, tag, val, attrs);
        }
        writer.write("\n");
    }
}
