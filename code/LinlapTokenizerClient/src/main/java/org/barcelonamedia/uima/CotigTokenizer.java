package org.barcelonamedia.uima;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.barcelonamedia.cotigclient.CotigWebService;
import org.barcelonamedia.cotigclient.CotigWebServiceSoap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CotigTokenizer extends JCasAnnotator_ImplBase {

	private static final String COMPONENT_NAME = "org.barcelonamedia.uima.CotigTokenizer";
	private static final String SENTENCE_PARAM = "SentenceType";
	private static final String TOKEN_PARAM = "TokenType";

	private String sentenceTypeName;
	private String tokenTypeName;

	private CotigWebService cws;
	private CotigWebServiceSoap cwss;

	private Logger logger;
	public void initialize(UimaContext aContext)
	throws ResourceInitializationException {
		super.initialize(aContext);
		this.logger = aContext.getLogger();
		this.sentenceTypeName = (String) aContext.getConfigParameterValue(SENTENCE_PARAM);
		this.tokenTypeName = (String) aContext.getConfigParameterValue(TOKEN_PARAM);
		this.cws = new CotigWebService();
		this.cwss = cws.getCotigWebServiceSoap();

	}

	private Annotation makeAnnotation(JCas jCas, int beginIndex, int endIndex, Type outputType){
		if (outputType == null) {
			return null;
		}
		Annotation annotation = null;
		annotation = (Annotation) jCas.getCas().createAnnotation(outputType, beginIndex, endIndex);
		Feature feature = outputType.getFeatureByBaseName("componentId");
		if (feature != null) {
			annotation.setFeatureValueFromString(feature, COMPONENT_NAME);
		} else {
			logger.log(Level.FINE,"no feature componentId for "+outputType.getName());
		}
		annotation.addToIndexes();
		return annotation;
	}

	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String document = jCas.getDocumentText();
		Type tokenType = jCas.getTypeSystem().getType(this.tokenTypeName);
		if (tokenType == null && !this.tokenTypeName.equals("None")) {
			logger.log(Level.WARNING,"token type not found: "+this.tokenTypeName+", won't output tokens.");
			//String[] arguments =  {this.tokenTypeName, COMPONENT_NAME};
			//throw new AnalysisEngineProcessException("class_not_found", arguments);
		}
		Type sentenceType = jCas.getTypeSystem().getType(this.sentenceTypeName);
		if (sentenceType == null && !this.sentenceTypeName.equals("None")) {
			logger.log(Level.WARNING,"sentence type not found: "+this.sentenceTypeName+", won't output sentences.");
			//String[] arguments =  {this.sentenceTypeName, COMPONENT_NAME};
			//throw new AnalysisEngineProcessException("class_not_found", arguments);
		}
		String request = String.format("<Request UserID=\"identificador\" Document=\"id_doc\" Action =\"Tokenize\"><Text Format=\"Plain\">%s</Text></Request>",StringEscapeUtils.escapeXml(document));
		String response = cwss.tokenize(request);
		logger.log(Level.INFO, response);
		//TODO: error handling
		List<Integer> sentBreaks = new ArrayList<Integer>();
		try {
			for (CotigToken token : xml2tokens(response)) {
				if ((token.type.equals("BreakSentence") || token.type.equals("BreakLine"))) { // sentence boundary
					sentBreaks.add(token.end);
					logger.log(Level.FINE, String.format("sentence break: (%d,%d), %s",token.begin,token.end,token.type));
				} else if (token.type.equals("BreakWord")) { // word boundary
					//not a token in UIMA
				} else {
					makeAnnotation(jCas, token.begin, token.end, tokenType);
					logger.log(Level.FINE, String.format("token: (%d,%d), %s",token.begin,token.end,token.type));
				}
			}
			
			Collections.sort(sentBreaks);
			int endOffset = document.length();
			if (endOffset < sentBreaks.get(sentBreaks.size()-1)) {
				logger.log(Level.SEVERE, String.format("sentence break (%d) after document end (%d)", sentBreaks.get(sentBreaks.size()), endOffset));
			} else {
				sentBreaks.add(endOffset);
			}
			int prevBreak = 0;
			for (Integer sentBreak : sentBreaks) { // sentBreaks needs to be ordered
				if (sentBreak>prevBreak) {
					makeAnnotation(jCas, prevBreak, sentBreak, sentenceType);
				}
				prevBreak = sentBreak;
			}
		} catch (ParserConfigurationException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private List<CotigToken> xml2tokens(String response) throws ParserConfigurationException, SAXException, IOException {
		//get the factory
		List<CotigToken> tokens = new ArrayList<CotigToken>();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document dom = db.parse(new InputSource(new StringReader(response)));
		//get the root element
		Element docEle = dom.getDocumentElement();
		//get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("Token");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				//get the Token element
				Element el = (Element)nl.item(i);
				//get the CotigToken object
				CotigToken t = getToken(el);
				//add it to list
				tokens.add(t);
			}
		}
		return tokens;
	}

	private CotigToken getToken(Element tokenElement) {
		int begin = Integer.parseInt(tokenElement.getAttribute("SpanFromChar"));
		int end = Integer.parseInt(tokenElement.getAttribute("SpanToChar"))+1;
		String type = tokenElement.getAttribute("Type");
		CotigToken cotigToken;
		// check for additional information
		NodeList nl = tokenElement.getElementsByTagName("Text");
		if(nl != null && nl.getLength() > 0) {
			Element textElement = (Element)nl.item(0);
			String source = textElement.getAttribute("Source");
			String normalized = textElement.getAttribute("Normalized");
			String shape = textElement.getAttribute("Shape");
			cotigToken = new CotigToken(begin,end,type,source,normalized,shape);
		} else {
			cotigToken = new CotigToken(begin,end,type);
		}
		return cotigToken;
	}

}
