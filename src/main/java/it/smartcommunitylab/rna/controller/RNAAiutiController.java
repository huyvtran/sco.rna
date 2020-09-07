package it.smartcommunitylab.rna.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.smartcommunitylab.rna.beans.AddRichiestaList;
import it.smartcommunitylab.rna.beans.ConfermaConcessione;
import it.smartcommunitylab.rna.beans.EsitoRichiesta;
import it.smartcommunitylab.rna.beans.GetRichiestaList;
import it.smartcommunitylab.rna.manager.RnaAiutiManager;
import it.smartcommunitylab.rna.model.RegistrazioneAiuto;

@RestController
@Api(tags = { "RNA Richieste API" })
public class RNAAiutiController {

	@Autowired
	private RnaAiutiManager manager;
	
	
	@PostMapping("/api/batch/{codiceBando}/get")
	@ApiOperation(value="Leggere lo stato della lista di richieste ")
	public @ResponseBody ResponseEntity<List<RegistrazioneAiuto>> getRegistrazioneAiutoBatch(@PathVariable Long codiceBando, @RequestBody GetRichiestaList req) {
		req.setCodiceBando(codiceBando);
		return ResponseEntity.ok(manager.getRegistrazioneAiuto(req.getIds(), req.getCodiceBando()));
	}
	
	@PostMapping("/api/requests/{codiceBando}/{id}")
	@ApiOperation(value="Leggere lo stato della singola richiesta specificata")
	public @ResponseBody ResponseEntity<RegistrazioneAiuto> getRegistrazioneAiuto(@PathVariable Long codiceBando, @PathVariable String id) {
		return ResponseEntity.ok(manager.getRegistrazioneAiuto(id, codiceBando));
	}

	@PostMapping("/api/batch/{codiceBando}/add")
	@ApiOperation(value="Creare nuove richieste RNA")
	public @ResponseBody ResponseEntity<EsitoRichiesta> addRegistrazioneAiutoBatch(@PathVariable Long codiceBando, @RequestBody AddRichiestaList req) {
		req.setCodiceBando(codiceBando);
		EsitoRichiesta res = new EsitoRichiesta();
		try {
			manager.addRegistrazioneAiuto(req.getRichieste(), req.getCodiceBando());
			res.setCode(0);
			res.setSuccess(true);
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			res.setCode(1);
			res.setMessage(e.getMessage());
			res.setSuccess(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
		}
	}
	
	@PutMapping("/api/requests/{codiceBando}/{id}/confirm")
	@ApiOperation(value="Confermare una richiesta processata")
	public @ResponseBody  ResponseEntity<Object> confirmRegistrazioneAiuto(@PathVariable Long codiceBando, @RequestBody ConfermaConcessione concessione) {
		try {
			return ResponseEntity.ok(manager.confermaAiuto(concessione));
		} catch (Exception e) {
			EsitoRichiesta res = new EsitoRichiesta();
			res.setCode(1);
			res.setMessage(e.getMessage());
			res.setSuccess(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
		}
	}
	@PutMapping("/api/requests/{codiceBando}/{id}/cancel/{cor}")
	@ApiOperation(value="Annullare una richiesta processata ma non ancora confermata")
	public @ResponseBody  ResponseEntity<Object> cancelRegistrazioneAiuto(@PathVariable Long codiceBando, @PathVariable Long cor) {
		try {
			return ResponseEntity.ok(manager.annullaAiuto(cor));
		} catch (Exception e) {
			EsitoRichiesta res = new EsitoRichiesta();
			res.setCode(1);
			res.setMessage(e.getMessage());
			res.setSuccess(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
		}
	}
	@PutMapping("/api/requests/{codiceBando}/{id}/repeat/{id:.*}")
	@ApiOperation(value="Reiterare una richiesta processata con errore")
	public @ResponseBody  ResponseEntity<Object> repeatRegistrazioneAiuto(@PathVariable Long codiceBando, @PathVariable String id) {
		try {
			return ResponseEntity.ok(manager.reiteraRegistrazioneAiuto(id, codiceBando));
		} catch (Exception e) {
			EsitoRichiesta res = new EsitoRichiesta();
			res.setCode(1);
			res.setMessage(e.getMessage());
			res.setSuccess(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
		}
	}	
	
}
