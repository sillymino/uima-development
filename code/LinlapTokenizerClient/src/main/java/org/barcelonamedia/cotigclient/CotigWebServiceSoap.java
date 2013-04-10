
package org.barcelonamedia.cotigclient;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "CotigWebServiceSoap", targetNamespace = "urn:CotigWebService")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CotigWebServiceSoap {


    /**
     * Instancia El Corrector principal, amb totes les dades compartides.
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CreateCorrector", action = "urn:CotigWebService/CreateCorrector")
    @WebResult(name = "CreateCorrectorResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "CreateCorrector", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.CreateCorrector")
    @ResponseWrapper(localName = "CreateCorrectorResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.CreateCorrectorResponse")
    public String createCorrector();

    /**
     * Correct texts in catalan. Used for the Web.
     * 
     * @param sourceText
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "DetectErrorsJSON", action = "urn:CotigWebService/DetectErrorsJSON")
    @WebResult(name = "DetectErrorsJSONResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "DetectErrorsJSON", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectErrorsJSON")
    @ResponseWrapper(localName = "DetectErrorsJSONResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectErrorsJSONResponse")
    public String detectErrorsJSON(
        @WebParam(name = "sourceText", targetNamespace = "urn:CotigWebService")
        String sourceText);

    /**
     * Correct texts in catalan. Used for the Web.
     * 
     * @param parameters
     * @return
     *     returns org.barcelonamedia.cotigclient.CorrectWithParametersX0020Response
     */
    @WebMethod(operationName = "CorrectWithParameters", action = "urn:CotigWebService/CorrectWithParameters ")
    @WebResult(name = "CorrectWithParameters_x0020_Response", targetNamespace = "urn:CotigWebService", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public CorrectWithParametersX0020Response correctWithParameters(
        @WebParam(name = "CorrectWithParameters_x0020_", targetNamespace = "urn:CotigWebService", partName = "parameters")
        CorrectWithParametersX0020 parameters);

    /**
     * Detecta i corregeix automaticament els errors en un text.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "DetectCorrectErrors", action = "urn:CotigWebService/DetectCorrectErrors")
    @WebResult(name = "DetectCorrectErrorsResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "DetectCorrectErrors", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectCorrectErrors")
    @ResponseWrapper(localName = "DetectCorrectErrorsResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectCorrectErrorsResponse")
    public String detectCorrectErrors(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Detecta els errors en un text.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "DetectErrors", action = "urn:CotigWebService/DetectErrors")
    @WebResult(name = "DetectErrorsResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "DetectErrors", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectErrors")
    @ResponseWrapper(localName = "DetectErrorsResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.DetectErrorsResponse")
    public String detectErrors(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Elimina la llista d'errors ignorats d'un usuari/document.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "ClearIgnoreErrors", action = "urn:CotigWebService/ClearIgnoreErrors")
    @WebResult(name = "ClearIgnoreErrorsResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "ClearIgnoreErrors", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.ClearIgnoreErrors")
    @ResponseWrapper(localName = "ClearIgnoreErrorsResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.ClearIgnoreErrorsResponse")
    public String clearIgnoreErrors(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Afegeix un error a la llista d'errors ignorats.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "AddToIgnoreErrors", action = "urn:CotigWebService/AddToIgnoreErrors")
    @WebResult(name = "AddToIgnoreErrorsResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "AddToIgnoreErrors", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.AddToIgnoreErrors")
    @ResponseWrapper(localName = "AddToIgnoreErrorsResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.AddToIgnoreErrorsResponse")
    public String addToIgnoreErrors(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Afegeix una paraula al diccionari d'usuari.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "AddToUserDictionary", action = "urn:CotigWebService/AddToUserDictionary")
    @WebResult(name = "AddToUserDictionaryResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "AddToUserDictionary", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.AddToUserDictionary")
    @ResponseWrapper(localName = "AddToUserDictionaryResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.AddToUserDictionaryResponse")
    public String addToUserDictionary(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Segmenta un text en blocs.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "SegmentBlocks", action = "urn:CotigWebService/SegmentBlocks")
    @WebResult(name = "SegmentBlocksResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "SegmentBlocks", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.SegmentBlocks")
    @ResponseWrapper(localName = "SegmentBlocksResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.SegmentBlocksResponse")
    public String segmentBlocks(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Tokenitza un text.
     * 
     * @param text
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "Tokenize", action = "urn:CotigWebService/Tokenize")
    @WebResult(name = "TokenizeResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "Tokenize", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.Tokenize")
    @ResponseWrapper(localName = "TokenizeResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.TokenizeResponse")
    public String tokenize(
        @WebParam(name = "text", targetNamespace = "urn:CotigWebService")
        String text);

    /**
     * Sets a new debug level
     * 
     * @param newDebugLevel
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:CotigWebService/setDebugLevel")
    @WebResult(name = "setDebugLevelResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "setDebugLevel", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.SetDebugLevel")
    @ResponseWrapper(localName = "setDebugLevelResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.SetDebugLevelResponse")
    public String setDebugLevel(
        @WebParam(name = "newDebugLevel", targetNamespace = "urn:CotigWebService")
        int newDebugLevel);

    /**
     * Retrieves current debug level
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "urn:CotigWebService/getDebugLevel")
    @WebResult(name = "getDebugLevelResult", targetNamespace = "urn:CotigWebService")
    @RequestWrapper(localName = "getDebugLevel", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.GetDebugLevel")
    @ResponseWrapper(localName = "getDebugLevelResponse", targetNamespace = "urn:CotigWebService", className = "org.barcelonamedia.cotigclient.GetDebugLevelResponse")
    public String getDebugLevel();

}