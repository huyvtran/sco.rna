package it.smartcommunitylab.rna.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.smartcommunitylab.rna.manager.RnaVisureManager;
import it.smartcommunitylab.rna.model.Visura;

@RestController
@Api(tags = { "RNA Visure API" })
public class RNAVisureController {

	@Autowired
	private RnaVisureManager manager;
	
	
	@PostMapping("/api/visure/{cf}")
	@ApiOperation(value="Leggere Visure per CF specificato ")
	public @ResponseBody ResponseEntity<Visura> getVisure(@PathVariable String cf) throws Exception {
		Visura vis = manager.getVisura(cf);
		if (vis == null) {
			vis = manager.addRichiestaVisuraAiuto(cf);
		}
		return ResponseEntity.ok(vis);
	}
	
}
