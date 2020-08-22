package it.smartcommunitylab.rna.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.common.Utils;
import it.smartcommunitylab.rna.exception.ServiceErrorException;

@Component
public class RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaManager.class);
	
	@Value("${rna.endpoint}")
	private String endpoint;
	
	@Value("${rna.user}")
	private String user;

	@Value("${rna.password}")
	private String password;
	
	@PostConstruct
	public void init() throws Exception {}
	
	protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		// Create document parser 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db;
	}
	
	protected Document getDocument(String content) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilder db = getDocumentBuilder();
		InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(content));
    Document doc = db.parse(is);
    return doc;
	}
	
	protected String postRequest(String contentString, String action) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(endpoint);
    UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
    httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
    //httpPost.addHeader("SOAPAction", action);  
    httpPost.addHeader("Content-Type", "text/xml;charset=UTF-8");
    StringEntity entity = new StringEntity(contentString);
    httpPost.setEntity(entity);    	

    CloseableHttpResponse response = client.execute(httpPost);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent(), Charset.forName("UTF-8")));
		StringBuffer responseBody = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			responseBody.append(output);
		}
		client.close();
    int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode >= 300) {
			logger.info(String.format("postRequest error:%s - %s", statusCode, responseBody.toString()));
			throw new RuntimeException("Failed : HTTP error code : " + statusCode);
		}    
		return responseBody.toString();		
	}
	
	protected String velocityParser(String template, Map<String, Object> contextMap) throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		VelocityContext context = new VelocityContext();
		context.put("Utils", Utils.class); 
		for(String key : contextMap.keySet()) {
			context.put(key, contextMap.get(key));
		}
		try {
			Template t = velocityEngine.getTemplate(template);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String result = writer.toString();
			return result;
		} catch (Exception e) {
			logger.error("velocityParser error: " + e.getClass() + " " + e.getMessage());
			throw new ServiceErrorException(e.getMessage());
		}
	}
	
	protected EsitoRichiesta getEsitoRichiesta(String content) {
		//TODO estrazione risultato richiesta
		return new EsitoRichiesta();
	}
	
	protected Binary getFile(String content, String tag) throws Exception {
		//TODO estrazione file
		Document doc = getDocument(content);
		NodeList nodeList = doc.getElementsByTagNameNS("*", tag);
		if(nodeList.getLength() > 0) {
			Element element = (Element) nodeList.item(0);
			String byteString = getStringDataFromElement(element);
			Binary file = new Binary(byteString.getBytes("UTF-8"));
			return file;
		}
		return null;
	}
	
	protected String getStringDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof CharacterData) {
      CharacterData cd = (CharacterData) child;
      return cd.getData();
    }
    return "";
  }

}
