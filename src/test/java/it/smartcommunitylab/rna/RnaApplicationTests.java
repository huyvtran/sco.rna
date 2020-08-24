package it.smartcommunitylab.rna;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.smartcommunitylab.rna.manager.RnaAiutiManager;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;

@SpringBootTest
class RnaApplicationTests {
	@Autowired
	RnaAiutiManager aiutiManager;
	
	@Test
	void addRichiestaAiuto() throws Exception {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		RegistrazioneAiuto aiuto = new RegistrazioneAiuto();
		String id = UUID.randomUUID().toString();
		aiuto.setConcessioneGestoreId(id);
		aiuto.setPraticaId(id);
		pratiche.add(aiuto);
		aiutiManager.addRegistrazioneAiuto(pratiche, Long.valueOf(8726));
	}
	
	@Test
	void appendFileToXML() throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		VelocityContext context = new VelocityContext();
		Template tAttach = velocityEngine.getTemplate("templates/registra-aiuto-attach.xml");
		StringWriter writer = new StringWriter();
		tAttach.merge(context, writer);
		String attachString = writer.toString();
		context.put("codiceBando", Long.valueOf(8726));
		context.put("attach", Base64.getEncoder().encodeToString(attachString.getBytes()));
		Template tAiuto = velocityEngine.getTemplate("templates/registra-aiuto.xml");
		writer = new StringWriter();
		tAiuto.merge(context, writer);
		String result = writer.toString();
		System.out.println(result);
	}

}
