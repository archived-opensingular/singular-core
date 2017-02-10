package org.opensingular.server.module.admin.healthPanel.service;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HealthPanelWebService {
	
	public List<?> getUrls(){
		// TESTES
		
		File teste = new File("singular.properties");
		
		System.out.println(teste);
		
		return null;
	}
}
