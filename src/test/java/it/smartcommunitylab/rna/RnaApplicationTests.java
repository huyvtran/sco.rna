package it.smartcommunitylab.rna;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.smartcommunitylab.rna.beans.ConfermaConcessione;
import it.smartcommunitylab.rna.manager.RnaAiutiManager;
import it.smartcommunitylab.rna.manager.RnaVisureManager;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto.Stato;
import it.smartcommunitylab.rna.model.Visura;

@SpringBootTest
class RnaApplicationTests {
	@Autowired
	RnaAiutiManager aiutiManager;
	@Autowired
	RnaVisureManager visureManager;
	
	@Test
	void annullaConcessione() throws Exception {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		RegistrazioneAiuto aiuto = new RegistrazioneAiuto();
		String id = UUID.randomUUID().toString();
		aiuto.setConcessioneGestoreId(id);
		aiuto.setCf("CTLNGL68L45A462B");
		pratiche.add(aiuto);
		aiutiManager.addRegistrazioneAiuto(pratiche, Long.valueOf(8726));
		boolean completata = false;
		while(!completata) {
			TimeUnit.SECONDS.sleep(15);
			RegistrazioneAiuto ra = aiutiManager.getRegistrazioneAiuto(id);
			if((ra != null) && (ra.getCor() != null)) {
				ra = aiutiManager.annullaAiuto(ra.getCor());
				assertTrue(ra.getStato() == Stato.annullato);
				completata = true;
			}
		}
	}
	
	@Test
	void confermaConcessione() throws Exception {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		RegistrazioneAiuto aiuto = new RegistrazioneAiuto();
		String id = UUID.randomUUID().toString();
		aiuto.setConcessioneGestoreId(id);
		aiuto.setCf("CTLNGL68L45A462B");
		pratiche.add(aiuto);
		aiutiManager.addRegistrazioneAiuto(pratiche, Long.valueOf(8726));
		boolean completata = false;
		while(!completata) {
			TimeUnit.SECONDS.sleep(15);
			RegistrazioneAiuto ra = aiutiManager.getRegistrazioneAiuto(id);
			if((ra != null) && (ra.getCor() != null)) {
				ConfermaConcessione concessione = new ConfermaConcessione();
				concessione.setCor(ra.getCor());
				concessione.setAttoConcessione("111112222333");
				concessione.setDataConcessione(new Date());
				ra = aiutiManager.confermaAiuto(concessione);
				assertTrue(ra.getStato() == Stato.confermato);
				completata = true;
			}
		}
	}
	
	@Test
	void scarivaVisure() throws Exception {
		visureManager.addRichiestaVisuraAiuto("CTLNGL68L45A462B");
		boolean completata = false;
		while(!completata) {
			TimeUnit.SECONDS.sleep(15);
			Visura visura = visureManager.getVisura("CTLNGL68L45A462B");
			if((visura != null) && (visura.getXmlVisuraAiuti() != null) && (visura.getXmlVisuraDeggendorf() != null)) {
				System.out.println(visura.getXmlVisuraAiuti());
				System.out.println(visura.getXmlVisuraDeggendorf());
				completata = true;
			}
		}
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
