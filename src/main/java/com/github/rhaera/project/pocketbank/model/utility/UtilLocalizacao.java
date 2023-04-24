package com.github.rhaera.project.pocketbank.model.utility;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public final class UtilLocalizacao {
    private static String TODAS_AS_UFS_JUNTAS;

    private static final String NO_AGENCIE = "0000";

    private static final String BRASIL_UFS = "src/main/resources/static/states.txt";

    private UtilLocalizacao() {
    }

    private static void agregarEstados() {
        try (BufferedReader br = new BufferedReader(new FileReader(BRASIL_UFS))) {
            TODAS_AS_UFS_JUNTAS = br.lines()
                                    .reduce("", String::concat);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static String agenciaMaisProxima(String cep) throws IOException {
        agregarEstados();
        String uf;
        URL url = new URL("https://viacep.com.br/ws/" + cep.trim() + "/json/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() != 200) throw new IllegalStateException("HTTP error code: " + connection.getResponseCode());
        try (BufferedReader br =
            new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8))) {
            uf = br.lines()
                    .filter(s -> s.contains("uf"))
                    .map(s -> s.substring(s.lastIndexOf(" "), s.indexOf(",")).trim().substring(1, 3))
                    .reduce("", String::concat);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("CEP inválido!");
        }
        return TODAS_AS_UFS_JUNTAS.contains(uf) ? getAgencia(uf) : NO_AGENCIE;
    }

    private static String getAgencia(String uf) {
        return !ehDoNorte(uf).equals(NO_AGENCIE) ? ehDoNorte(uf) :
                (!ehDoNordeste(uf).equals(NO_AGENCIE) ? ehDoNordeste(uf) :
                        (!ehDoSudeste(uf).equals(NO_AGENCIE) ? ehDoSudeste(uf) :
                                (!ehDoSul(uf).equals(NO_AGENCIE) ? ehDoSul(uf) :
                                        (!ehDoCentroOeste(uf).equals(NO_AGENCIE) ? ehDoCentroOeste(uf) : NO_AGENCIE))));
    }

    private static String ehDoNorte(String uf) {
        switch (uf) {
            case "AC":
            case "AM":
            case "RO":
            case "RR":
                return "0008";
            case "AP":
            case "PA":
            case "TO":
                return "0007";
            default:
                return NO_AGENCIE;
        }
    }

    private static String ehDoNordeste(String uf) {
        switch (uf) {
            case "AL":
            case "CE":
            case "PB":
            case "PE":
            case "RN":
                return "0009";
            case "BA":
            case "MA":
            case "PI":
            case "SE":
                return "0010";
            default:
                return NO_AGENCIE;
        }
    }

    private static String ehDoSul(String uf) {
        switch (uf) {
            case "PR":
            case "SC":
                return "0005";
            case "RS":
                return "0006";
            default:
                return NO_AGENCIE;
        }
    }

    private static String ehDoSudeste(String uf) {
        switch (uf) {
            case "ES":
            case "RJ":
                return "0001";
            case "MG":
            case "SP":
                return "0002";
            default:
                return NO_AGENCIE;
        }
    }

    private static String ehDoCentroOeste(String uf) {
        switch (uf) {
            case "DF":
            case "GO":
                return "0003";
            case "MS":
            case "MT":
                return "0004";
            default:
                return NO_AGENCIE;
        }
    }
}
