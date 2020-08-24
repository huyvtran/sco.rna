package it.smartcommunitylab.rna;

import java.util.Base64;

import org.junit.jupiter.api.Test;

public class EncodingTest {
	@Test
	public void decodeEsito() {
		String encoded = "";
		System.out.println(new String(Base64.getDecoder().decode(encoded)));
	}
}
