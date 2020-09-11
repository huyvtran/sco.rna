package it.smartcommunitylab.rna.model;

import java.util.List;

public class RequestAttachData {

	private String
		CAR,
		TITOLO_PROGETTO, 
		DESCRIZIONE_PROGETTO,
		COD_TIPO_INIZIATIVA,
		DATA_INIZIO_PROGETTO,
		DATA_FINE_PROGETTO,
		DATA_CONCESSIONE,
		NOTE_CONCESSIONE,
		FLAG_ATTO_CONCESSIONE,
		
		COD_TIPO_SOGGETTO,
		DENOMINAZIONE,
		COD_DIMENSIONE_IMPRESA,
		COD_FORMA_GIURIDICA,
		
		INDIRIZZO, 
		COD_COMUNE,
		COMUNE,
		CAP,
		COD_REGIONE,
		
		ID_COSTO_GEST,
		COD_TIPO_COSTO;
	private Double 
		IMPORTO_AMMESSO;
		
	private String
		ID_COMP_AIUTO_GEST,
		DESCR_BREVE,
		COD_TIPO_PROCEDIMENTO,
		CODICE_REGOLAMENTO,
		COD_OBIETTIVO,
		CUMULABILITA,
		FLAG_CE;
	private List<String>
		COD_ATECO;
		
	private String
		ID_STRUM_AIUTO_GEST,
		COD_TIPO_STRUMENTO_AIUTO;
	private Double 
		IMPORTO_NOMINALE,
		IMPORTO_AGEVOLAZIONE;
	
	private String
		INTENSITA_AIUTO;

	public String getCAR() {
		return CAR;
	}

	public void setCAR(String cAR) {
		CAR = cAR;
	}

	public String getTITOLO_PROGETTO() {
		return TITOLO_PROGETTO;
	}

	public void setTITOLO_PROGETTO(String tITOLO_PROGETTO) {
		TITOLO_PROGETTO = tITOLO_PROGETTO;
	}

	public String getDESCRIZIONE_PROGETTO() {
		return DESCRIZIONE_PROGETTO;
	}

	public void setDESCRIZIONE_PROGETTO(String dESCRIZIONE_PROGETTO) {
		DESCRIZIONE_PROGETTO = dESCRIZIONE_PROGETTO;
	}

	public String getCOD_TIPO_INIZIATIVA() {
		return COD_TIPO_INIZIATIVA;
	}

	public void setCOD_TIPO_INIZIATIVA(String cOD_TIPO_INIZIATIVA) {
		COD_TIPO_INIZIATIVA = cOD_TIPO_INIZIATIVA;
	}

	public String getDATA_INIZIO_PROGETTO() {
		return DATA_INIZIO_PROGETTO;
	}

	public void setDATA_INIZIO_PROGETTO(String dATA_INIZIO_PROGETTO) {
		DATA_INIZIO_PROGETTO = dATA_INIZIO_PROGETTO;
	}

	public String getDATA_FINE_PROGETTO() {
		return DATA_FINE_PROGETTO;
	}

	public void setDATA_FINE_PROGETTO(String dATA_FINE_PROGETTO) {
		DATA_FINE_PROGETTO = dATA_FINE_PROGETTO;
	}

	public String getDATA_CONCESSIONE() {
		return DATA_CONCESSIONE;
	}

	public void setDATA_CONCESSIONE(String dATA_CONCESSIONE) {
		DATA_CONCESSIONE = dATA_CONCESSIONE;
	}

	public String getNOTE_CONCESSIONE() {
		return NOTE_CONCESSIONE;
	}

	public void setNOTE_CONCESSIONE(String nOTE_CONCESSIONE) {
		NOTE_CONCESSIONE = nOTE_CONCESSIONE;
	}

	public String getFLAG_ATTO_CONCESSIONE() {
		return FLAG_ATTO_CONCESSIONE;
	}

	public void setFLAG_ATTO_CONCESSIONE(String fLAG_ATTO_CONCESSIONE) {
		FLAG_ATTO_CONCESSIONE = fLAG_ATTO_CONCESSIONE;
	}

	public String getCOD_TIPO_SOGGETTO() {
		return COD_TIPO_SOGGETTO;
	}

	public void setCOD_TIPO_SOGGETTO(String cOD_TIPO_SOGGETTO) {
		COD_TIPO_SOGGETTO = cOD_TIPO_SOGGETTO;
	}

	public String getDENOMINAZIONE() {
		return DENOMINAZIONE;
	}

	public void setDENOMINAZIONE(String dENOMINAZIONE) {
		DENOMINAZIONE = dENOMINAZIONE;
	}

	public String getCOD_DIMENSIONE_IMPRESA() {
		return COD_DIMENSIONE_IMPRESA;
	}

	public void setCOD_DIMENSIONE_IMPRESA(String cOD_DIMENSIONE_IMPRESA) {
		COD_DIMENSIONE_IMPRESA = cOD_DIMENSIONE_IMPRESA;
	}

	public String getCOD_FORMA_GIURIDICA() {
		return COD_FORMA_GIURIDICA;
	}

	public void setCOD_FORMA_GIURIDICA(String cOD_FORMA_GIURIDICA) {
		COD_FORMA_GIURIDICA = cOD_FORMA_GIURIDICA;
	}

	public String getINDIRIZZO() {
		return INDIRIZZO;
	}

	public void setINDIRIZZO(String iNDIRIZZO) {
		INDIRIZZO = iNDIRIZZO;
	}

	public String getCOD_COMUNE() {
		return COD_COMUNE;
	}

	public void setCOD_COMUNE(String cOD_COMUNE) {
		COD_COMUNE = cOD_COMUNE;
	}

	public String getCOMUNE() {
		return COMUNE;
	}

	public void setCOMUNE(String cOMUNE) {
		COMUNE = cOMUNE;
	}

	public String getCAP() {
		return CAP;
	}

	public void setCAP(String cAP) {
		CAP = cAP;
	}

	public String getCOD_REGIONE() {
		return COD_REGIONE;
	}

	public void setCOD_REGIONE(String cOD_REGIONE) {
		COD_REGIONE = cOD_REGIONE;
	}

	public String getID_COSTO_GEST() {
		return ID_COSTO_GEST;
	}

	public void setID_COSTO_GEST(String iD_COSTO_GEST) {
		ID_COSTO_GEST = iD_COSTO_GEST;
	}

	public String getCOD_TIPO_COSTO() {
		return COD_TIPO_COSTO;
	}

	public void setCOD_TIPO_COSTO(String cOD_TIPO_COSTO) {
		COD_TIPO_COSTO = cOD_TIPO_COSTO;
	}

	public Double getIMPORTO_AMMESSO() {
		return IMPORTO_AMMESSO;
	}

	public void setIMPORTO_AMMESSO(Double iMPORTO_AMMESSO) {
		IMPORTO_AMMESSO = iMPORTO_AMMESSO;
	}

	public String getID_COMP_AIUTO_GEST() {
		return ID_COMP_AIUTO_GEST;
	}

	public void setID_COMP_AIUTO_GEST(String iD_COMP_AIUTO_GEST) {
		ID_COMP_AIUTO_GEST = iD_COMP_AIUTO_GEST;
	}

	public String getDESCR_BREVE() {
		return DESCR_BREVE;
	}

	public void setDESCR_BREVE(String dESCR_BREVE) {
		DESCR_BREVE = dESCR_BREVE;
	}

	public String getCOD_TIPO_PROCEDIMENTO() {
		return COD_TIPO_PROCEDIMENTO;
	}

	public void setCOD_TIPO_PROCEDIMENTO(String cOD_TIPO_PROCEDIMENTO) {
		COD_TIPO_PROCEDIMENTO = cOD_TIPO_PROCEDIMENTO;
	}

	public String getCODICE_REGOLAMENTO() {
		return CODICE_REGOLAMENTO;
	}

	public void setCODICE_REGOLAMENTO(String cODICE_REGOLAMENTO) {
		CODICE_REGOLAMENTO = cODICE_REGOLAMENTO;
	}

	public String getCOD_OBIETTIVO() {
		return COD_OBIETTIVO;
	}

	public void setCOD_OBIETTIVO(String cOD_OBIETTIVO) {
		COD_OBIETTIVO = cOD_OBIETTIVO;
	}

	public String getCUMULABILITA() {
		return CUMULABILITA;
	}

	public void setCUMULABILITA(String cUMULABILITA) {
		CUMULABILITA = cUMULABILITA;
	}

	public String getFLAG_CE() {
		return FLAG_CE;
	}

	public void setFLAG_CE(String fLAG_CE) {
		FLAG_CE = fLAG_CE;
	}

	public List<String> getCOD_ATECO() {
		return COD_ATECO;
	}

	public void setCOD_ATECO(List<String> cOD_ATECO) {
		COD_ATECO = cOD_ATECO;
	}

	public String getID_STRUM_AIUTO_GEST() {
		return ID_STRUM_AIUTO_GEST;
	}

	public void setID_STRUM_AIUTO_GEST(String iD_STRUM_AIUTO_GEST) {
		ID_STRUM_AIUTO_GEST = iD_STRUM_AIUTO_GEST;
	}

	public String getCOD_TIPO_STRUMENTO_AIUTO() {
		return COD_TIPO_STRUMENTO_AIUTO;
	}

	public void setCOD_TIPO_STRUMENTO_AIUTO(String cOD_TIPO_STRUMENTO_AIUTO) {
		COD_TIPO_STRUMENTO_AIUTO = cOD_TIPO_STRUMENTO_AIUTO;
	}

	public Double getIMPORTO_NOMINALE() {
		return IMPORTO_NOMINALE;
	}

	public void setIMPORTO_NOMINALE(Double iMPORTO_NOMINALE) {
		IMPORTO_NOMINALE = iMPORTO_NOMINALE;
	}

	public Double getIMPORTO_AGEVOLAZIONE() {
		return IMPORTO_AGEVOLAZIONE;
	}

	public void setIMPORTO_AGEVOLAZIONE(Double iMPORTO_AGEVOLAZIONE) {
		IMPORTO_AGEVOLAZIONE = iMPORTO_AGEVOLAZIONE;
	}

	public String getINTENSITA_AIUTO() {
		return INTENSITA_AIUTO;
	}

	public void setINTENSITA_AIUTO(String iNTENSITA_AIUTO) {
		INTENSITA_AIUTO = iNTENSITA_AIUTO;
	}

	

}
