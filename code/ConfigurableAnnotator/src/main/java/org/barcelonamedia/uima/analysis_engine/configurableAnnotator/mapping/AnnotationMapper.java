package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping;

import org.xml.sax.Attributes;

/**
 * The Class AnnotationMapper.
 */
public class AnnotationMapper implements ElementMapper<Locateable> {

    /**
     * The constant string representing the type attribute of annotation
     * element. Defines the type of the annotation to search for.
     */
    private static final String ANNOTATION_TYPE = "type";

    /**
     * The constant string representing the sofa attribute of annotation
     * element. Defines the source sofa of annotations.
     */
    private static final String ANNOTATION_SOFA = "sofa";

    /**
     * Extract the information from the annotation element attributes.
     *
     * @param attributes The attributes.
     *
     * @return The annotation description.
     *
     * @see org.barcelonamedia.uima.analysis_engine.configurableAnnotator.mapping.ElementMapper#mapElement(org.xml.sax.Attributes)
     */
    public final AnnotationDescription mapElement(final Attributes attributes) {

        AnnotationDescription annotationDescription;
        annotationDescription = new AnnotationDescription();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            if (name.equals(ANNOTATION_SOFA)) {
                annotationDescription.setSofa(value);

            } else if (name.equals(ANNOTATION_TYPE)) {
                annotationDescription.setType(value);
            }
        }
        return annotationDescription;
    }

}
