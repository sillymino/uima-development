
package org.barcelonamedia.cotigclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.barcelonamedia.cotigclient package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _String_QNAME = new QName("urn:CotigWebService", "string");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.barcelonamedia.cotigclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddToIgnoreErrorsResponse }
     * 
     */
    public AddToIgnoreErrorsResponse createAddToIgnoreErrorsResponse() {
        return new AddToIgnoreErrorsResponse();
    }

    /**
     * Create an instance of {@link SegmentBlocks }
     * 
     */
    public SegmentBlocks createSegmentBlocks() {
        return new SegmentBlocks();
    }

    /**
     * Create an instance of {@link CreateCorrectorResponse }
     * 
     */
    public CreateCorrectorResponse createCreateCorrectorResponse() {
        return new CreateCorrectorResponse();
    }

    /**
     * Create an instance of {@link DetectErrorsResponse }
     * 
     */
    public DetectErrorsResponse createDetectErrorsResponse() {
        return new DetectErrorsResponse();
    }

    /**
     * Create an instance of {@link AddToIgnoreErrors }
     * 
     */
    public AddToIgnoreErrors createAddToIgnoreErrors() {
        return new AddToIgnoreErrors();
    }

    /**
     * Create an instance of {@link ClearIgnoreErrorsResponse }
     * 
     */
    public ClearIgnoreErrorsResponse createClearIgnoreErrorsResponse() {
        return new ClearIgnoreErrorsResponse();
    }

    /**
     * Create an instance of {@link DetectErrorsJSONResponse }
     * 
     */
    public DetectErrorsJSONResponse createDetectErrorsJSONResponse() {
        return new DetectErrorsJSONResponse();
    }

    /**
     * Create an instance of {@link DetectCorrectErrorsResponse }
     * 
     */
    public DetectCorrectErrorsResponse createDetectCorrectErrorsResponse() {
        return new DetectCorrectErrorsResponse();
    }

    /**
     * Create an instance of {@link GetDebugLevelResponse }
     * 
     */
    public GetDebugLevelResponse createGetDebugLevelResponse() {
        return new GetDebugLevelResponse();
    }

    /**
     * Create an instance of {@link CorrectWithParametersX0020 }
     * 
     */
    public CorrectWithParametersX0020 createCorrectWithParametersX0020() {
        return new CorrectWithParametersX0020();
    }

    /**
     * Create an instance of {@link SegmentBlocksResponse }
     * 
     */
    public SegmentBlocksResponse createSegmentBlocksResponse() {
        return new SegmentBlocksResponse();
    }

    /**
     * Create an instance of {@link AddToUserDictionaryResponse }
     * 
     */
    public AddToUserDictionaryResponse createAddToUserDictionaryResponse() {
        return new AddToUserDictionaryResponse();
    }

    /**
     * Create an instance of {@link DetectErrors }
     * 
     */
    public DetectErrors createDetectErrors() {
        return new DetectErrors();
    }

    /**
     * Create an instance of {@link DetectErrorsJSON }
     * 
     */
    public DetectErrorsJSON createDetectErrorsJSON() {
        return new DetectErrorsJSON();
    }

    /**
     * Create an instance of {@link SetDebugLevel }
     * 
     */
    public SetDebugLevel createSetDebugLevel() {
        return new SetDebugLevel();
    }

    /**
     * Create an instance of {@link ClearIgnoreErrors }
     * 
     */
    public ClearIgnoreErrors createClearIgnoreErrors() {
        return new ClearIgnoreErrors();
    }

    /**
     * Create an instance of {@link TokenizeResponse }
     * 
     */
    public TokenizeResponse createTokenizeResponse() {
        return new TokenizeResponse();
    }

    /**
     * Create an instance of {@link SetDebugLevelResponse }
     * 
     */
    public SetDebugLevelResponse createSetDebugLevelResponse() {
        return new SetDebugLevelResponse();
    }

    /**
     * Create an instance of {@link Tokenize }
     * 
     */
    public Tokenize createTokenize() {
        return new Tokenize();
    }

    /**
     * Create an instance of {@link GetDebugLevel }
     * 
     */
    public GetDebugLevel createGetDebugLevel() {
        return new GetDebugLevel();
    }

    /**
     * Create an instance of {@link DetectCorrectErrors }
     * 
     */
    public DetectCorrectErrors createDetectCorrectErrors() {
        return new DetectCorrectErrors();
    }

    /**
     * Create an instance of {@link CorrectWithParametersX0020Response }
     * 
     */
    public CorrectWithParametersX0020Response createCorrectWithParametersX0020Response() {
        return new CorrectWithParametersX0020Response();
    }

    /**
     * Create an instance of {@link CreateCorrector }
     * 
     */
    public CreateCorrector createCreateCorrector() {
        return new CreateCorrector();
    }

    /**
     * Create an instance of {@link AddToUserDictionary }
     * 
     */
    public AddToUserDictionary createAddToUserDictionary() {
        return new AddToUserDictionary();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:CotigWebService", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

}
