package it.smartcommunitylab.rna.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.rna.common.Utils;
import it.smartcommunitylab.rna.exception.ParseErrorException;

@Component
public class RnaManager {
	private static final transient Logger logger = LoggerFactory.getLogger(RnaManager.class);
	
	@Value("${rna.endpoint}")
	private String endpoint;
	
	@Value("${rna.user}")
	private String user;

	@Value("${rna.password}")
	private String password;
	
	private String authHeaderValue;
	
	@PostConstruct
	public void init() throws Exception {
    String auth = user + ":" + password;
    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    authHeaderValue = "Basic " + encodedAuth;
	}
	
	protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		// Create document parser 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db;
	}
	
	protected String postRequest(String contentString, String action) throws Exception {
		URL url = new URL(endpoint);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		conn.setRequestProperty("SOAPAction", action);
		conn.setRequestProperty("Authorization", authHeaderValue);
		
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		writer.write(contentString);
		writer.close();
		out.close();		

		int responseCode = conn.getResponseCode();
		if (responseCode >= 300) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((conn.getInputStream()), Charset.defaultCharset()));
		StringBuffer response = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		conn.disconnect();
		String res = new String(response.toString().getBytes(), Charset.forName("UTF-8"));
		return res;
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
			throw new ParseErrorException(e.getMessage());
		}
	}
}
