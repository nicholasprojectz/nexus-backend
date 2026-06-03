package com.trabalho.nexus.integracao;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BcbIntegrationService {

    private final RestClient restClient;
    private static final String URL_BCB = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.4390/dados?formato=json&dataInicial={inicio}&dataFinal={fim}";

    public BcbIntegrationService() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(2));
        
        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

 
    public Map<String, Double> buscarTaxasSelicHistoricas(LocalDate dataInicial, LocalDate dataFinal) {
        Map<String, Double> mapaTaxas = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            String inicioStr = dataInicial.format(formatter);
            String fimStr = dataFinal.format(formatter);

            List<TaxaBcbResponseDTO> resposta = restClient.get()
                    .uri(URL_BCB, inicioStr, fimStr)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TaxaBcbResponseDTO>>() {});

            if (resposta != null) {
                for (TaxaBcbResponseDTO dto : resposta) {
                    String mesAno = dto.data().substring(3); 
                    
                    Double taxaDecimal = Double.parseDouble(dto.valor()) / 100.0;
                    mapaTaxas.put(mesAno, taxaDecimal);
                }
            }
            return mapaTaxas;

        } catch (Exception e) {
            return mapaTaxas;
        }
    }
}