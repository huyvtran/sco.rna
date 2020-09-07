package it.smartcommunitylab.rna;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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
import it.smartcommunitylab.rna.model.RequestAttachData;
import it.smartcommunitylab.rna.model.Visura;

@SpringBootTest
class RnaApplicationTests {
	private static final long CODICE_BANDO = 8726l;
	@Autowired
	RnaAiutiManager aiutiManager;
	@Autowired
	RnaVisureManager visureManager;
	
	@Test
	public void annullaConcessione() throws Exception {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		RegistrazioneAiuto aiuto = createRequest();
		pratiche.add(aiuto);
		aiutiManager.addRegistrazioneAiuto(pratiche, CODICE_BANDO);
		boolean completata = false;
		while(!completata) {
			TimeUnit.SECONDS.sleep(15);
			RegistrazioneAiuto ra = aiutiManager.getRegistrazioneAiuto(aiuto.getId(), CODICE_BANDO);
			if((ra != null) && (ra.getCor() != null)) {
				ra = aiutiManager.annullaAiuto(ra.getCor());
				assertTrue(ra.getStato() == Stato.annullato);
				completata = true;
			}
		}
	}
	
	@Test
	public void confermaConcessione() throws Exception {
		List<RegistrazioneAiuto> pratiche = new ArrayList<>();
		RegistrazioneAiuto aiuto = createRequest();
		pratiche.add(aiuto);
		aiutiManager.addRegistrazioneAiuto(pratiche, CODICE_BANDO);
		boolean completata = false;
		while(!completata) {
			TimeUnit.SECONDS.sleep(15);
			RegistrazioneAiuto ra = aiutiManager.getRegistrazioneAiuto(aiuto.getId(), CODICE_BANDO);
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

	private RegistrazioneAiuto createRequest() {
		RegistrazioneAiuto aiuto = new RegistrazioneAiuto();
		String id = UUID.randomUUID().toString();
		aiuto.setConcessioneGestoreId(id);
		aiuto.setCf("CTLNGL68L45A462B");
		
		RequestAttachData attach = new RequestAttachData();
//		attach.setCAR("13008");
		attach.setTITOLO_PROGETTO("Linee di finanziamento PLAFOND RIPRESA TRENTINO");
		attach.setDESCRIZIONE_PROGETTO("Concessioni di contributi in conto interessi agli operatori economici per contenere gli effetti negativi causati dal COVID-19");
		attach.setCOD_TIPO_INIZIATIVA("07.99");
		attach.setDATA_INIZIO_PROGETTO(LocalDate.now());
		attach.setDATA_FINE_PROGETTO(attach.getDATA_INIZIO_PROGETTO().plusYears(1));
		attach.setFLAG_ATTO_CONCESSIONE("SI");
		attach.setDATA_CONCESSIONE(LocalDate.now());
//		attach.setNOTE_CONCESSIONE(null);
		attach.setCOD_TIPO_SOGGETTO("1"); // var
		attach.setDENOMINAZIONE("CATALUCCI ANGELA"); // var
		attach.setCOD_DIMENSIONE_IMPRESA("2"); // var
		attach.setCOD_FORMA_GIURIDICA("DI"); // var
		attach.setINDIRIZZO("FRAZIONE CAGNANO 39"); // var
		attach.setCOD_COMUNE("044001"); // var
		attach.setCAP("63095"); // var
		attach.setCOMUNE("Acquasanta Terme"); // var
		attach.setCOD_REGIONE("ITI3"); 
		attach.setID_COSTO_GEST("19");
		attach.setCOD_TIPO_COSTO("19");
		attach.setIMPORTO_AMMESSO(300.0); // var
		attach.setDESCR_BREVE("Contributo in un'unica soluzione");
		attach.setCOD_TIPO_PROCEDIMENTO("2");
		attach.setCODICE_REGOLAMENTO("CE1863-3.10/20");
		attach.setCOD_OBIETTIVO("702200");
		attach.setCUMULABILITA("0");
		attach.setFLAG_CE("NO");
		attach.setCOD_ATECO(Collections.singletonList("47.64.20")); // var
		attach.setID_STRUM_AIUTO_GEST("2");
		attach.setCOD_TIPO_STRUMENTO_AIUTO("4");
		attach.setIMPORTO_NOMINALE(300.0); // var
		attach.setIMPORTO_AGEVOLAZIONE(300.0); // var
		attach.setINTENSITA_AIUTO("100");
		
		aiuto.setAttach(attach);
		
		return aiuto;
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
		context.put("codiceBando", CODICE_BANDO);
		context.put("attach", Base64.getEncoder().encodeToString(attachString.getBytes()));
		Template tAiuto = velocityEngine.getTemplate("templates/registra-aiuto.xml");
		writer = new StringWriter();
		tAiuto.merge(context, writer);
		String result = writer.toString();
		System.out.println(result);
	}

}
