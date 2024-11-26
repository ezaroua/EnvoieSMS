package fr.esgi.envoie_sms.service;

import fr.esgi.envoie_sms.config.OvhConfig;
import fr.esgi.envoie_sms.model.Sms;
import fr.esgi.envoie_sms.repository.SmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@Service
public class SmsService {

    @Autowired
    private SmsRepository smsRepository;

    public String sendSms(String phoneNumber, String message) throws Exception {
        Sms sms = new Sms();
        sms.setPhoneNumber(phoneNumber);
        sms.setMessage(message);

        try {
            // Le code existant pour envoyer le SMS
            String response = sendSmsToOvh(phoneNumber, message);

            // Sauvegarde en base de données
            sms.setSuccess(response.contains("totalCreditsRemoved"));
            sms.setApiResponse(response);
            smsRepository.save(sms);

            return response;
        } catch (Exception e) {
            sms.setSuccess(false);
            sms.setApiResponse("Erreur: " + e.getMessage());
            smsRepository.save(sms);
            throw e;
        }
    }

    public List<Sms> getHistory() {
        return smsRepository.findAllByOrderBySentAtDesc();
    }
    private String sendSmsToOvh(String phoneNumber, String message) throws Exception {
        String serviceUrl = "https://eu.api.ovh.com/1.0/sms/" + OvhConfig.SERVICE_NAME + "/jobs";
        System.out.println("URL appelée: " + serviceUrl);

        URL url = new URL(serviceUrl);
        // Modification du body pour correspondre au format qui fonctionne
        String body = String.format("{\"receivers\":[\"%s\"],\"message\":\"%s\",\"priority\":\"high\",\"senderForResponse\":true}",
                phoneNumber, message);

        System.out.println("Body de la requête: " + body);

        long timestamp = new Date().getTime() / 1000;

        String toSign = OvhConfig.APPLICATION_SECRET + "+" +
                OvhConfig.CONSUMER_KEY + "+POST+" +
                url + "+" + body + "+" + timestamp;
        String signature = "$1$" + hashSHA1(toSign);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-Ovh-Application", OvhConfig.APPLICATION_KEY);
        conn.setRequestProperty("X-Ovh-Consumer", OvhConfig.CONSUMER_KEY);
        conn.setRequestProperty("X-Ovh-Signature", signature);
        conn.setRequestProperty("X-Ovh-Timestamp", String.valueOf(timestamp));
        conn.setDoOutput(true);

        // Envoi du body
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.writeBytes(body);  // Utilisation de writeBytes comme dans ProgramSendSms
            wr.flush();
        }

        // Lecture de la réponse
        int responseCode = conn.getResponseCode();
        System.out.println("Code de réponse: " + responseCode);

        // Lecture de la réponse
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            if (responseCode != 200) {
                throw new RuntimeException("Erreur API OVH (" + responseCode + "): " + response.toString());
            }

            return response.toString();
        }
    }

    private String hashSHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}