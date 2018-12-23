/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq.helper;

import com.lsmichel.akkaamqprabbitmq.actors.CardregistryActor;
import com.lsmichel.akkaamqprabbitmq.actors.PersistanceActor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lathsessakpamichel
 */
public class Utilities {
    public static String SetCardDataInQuery(CardregistryActor.Card card) {
        if (card != null && card.getCardNumber() != null
                && !card.getCardNumber().isEmpty()
                && card.getCardNocaisse() != null
                && !card.getCardNocaisse().isEmpty()) {
            MessageDigest digest = null;
            String cardid = card.getCardNumber() + ":" + card.getCardNocaisse();
            try {
                digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhash = digest.digest(
                        cardid.getBytes(StandardCharsets.UTF_8));
                cardid = bytesToHex(encodedhash);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(PersistanceActor.class.getName()).log(Level.SEVERE, null, ex);
            }
            String insertData = "INSERT INTO card_details VALUES (" + "'";
            insertData += cardid + "' , '";
            insertData += card.getCardNumber() + "' , '";
            insertData += card.getCardUserFname() + "' , '";
            insertData += card.getCardUserLname() + "' , '";
            insertData += card.getCardDateEtabishment() + "' , '";
            insertData += card.getCardDateEpiration() + "' , '";
            insertData += card.getCardLocationEtabishment() + "' , '";
            insertData += card.getCardImatriculation() + "' , '";
            insertData += card.getCardUserSex() + "' , '";
            insertData += card.getCardUserPhoto() + "' , '";
            insertData += card.getCardUserAdress() + "' , '";
            insertData += card.getCardUserPofession() + "' , '";
            insertData += card.getCardUserFatherName() + "' , '";
            insertData += card.getCardUserFatherBirthDate() + "' , '";
            insertData += card.getCardUserMatherName() + "' , '";
            insertData += card.getCardUserMatherBirthDate() + "' , '";
            insertData += card.getCardNocaisse() + "' , '";
            insertData += card.getCardUserBirthDate() + "' , '";
            insertData += card.getCardUserBrithPlace() + "' , '";
            insertData += card.getCardUserNationality() + "'";
            insertData += ")";
            return insertData;

        }
        return null;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
     public static Map<String, Object> generateCardMap(CardregistryActor.Card card) {
        if (card!=null && card.getCardNocaisse() != null && ! card.getCardNocaisse().isEmpty()) {
            Map<String, Object> icardIn = new HashMap<>();
            if (card.getCardUserFname() != null && !card.getCardUserFname().isEmpty()) {
                icardIn.put("cardUserFname", card.getCardUserFname());
            }
            if (card.getCardUserLname() != null && !card.getCardUserLname().isEmpty()) {
                icardIn.put("cardUserLname", card.getCardUserLname());
            }
            if (card.getCardDateEtabishment() != null && !card.getCardDateEtabishment().isEmpty()) {
                icardIn.put("cardDateEtabishment", card.getCardDateEtabishment());
            }
            if (card.getCardDateEpiration() != null && !card.getCardDateEpiration().isEmpty()) {
                icardIn.put("cardDateEpiration", card.getCardDateEpiration());
            }
            if (card.getCardLocationEtabishment() != null && !card.getCardLocationEtabishment().isEmpty()) {
                icardIn.put("cardLocationEtabishment", card.getCardLocationEtabishment());
            }
            if (card.getCardImatriculation() != null && !card.getCardImatriculation().isEmpty()) {
                icardIn.put("cardImatriculation", card.getCardImatriculation());
            }
            if (card.getCardNumber() != null && !card.getCardNumber().isEmpty()) {
                icardIn.put("cardNumber", card.getCardNumber());
            }
            if (card.getCardUserSex() != null && !card.getCardUserSex().isEmpty()) {
                icardIn.put("cardUserSex", card.getCardUserSex());
            }
            if (card.getCardUserPhoto() != null && !card.getCardUserPhoto().isEmpty()) {
                icardIn.put("cardUserPhoto", card.getCardUserPhoto());
            }
            if (card.getCardUserAdress() != null && !card.getCardUserAdress().isEmpty()) {
                icardIn.put("cardUserAdress", card.getCardUserAdress());
            }
            if (card.getCardUserPofession() != null && !card.getCardUserPofession().isEmpty()) {
                icardIn.put("cardUserPofession", card.getCardUserPofession());
            }
            if (card.getCardUserFatherName() != null && !card.getCardUserFatherName().isEmpty()) {
                icardIn.put("cardUserFatherName", card.getCardUserFatherName());
            }
            if (card.getCardUserFatherBirthDate() != null && !card.getCardUserFatherBirthDate().isEmpty()) {
                icardIn.put("cardUserFatherBirthDate", card.getCardUserFatherBirthDate());
            }
            if (card.getCardUserMatherBirthDate() != null && !card.getCardUserMatherBirthDate().isEmpty()) {
                icardIn.put("cardUserMatherBirthDate", card.getCardUserMatherBirthDate());
            }
            if (card.getCardUserMatherName() != null && !card.getCardUserMatherName().isEmpty()) {
                icardIn.put("cardUserMatherName", card.getCardUserMatherName());
            }
            if (card.getCardUserBirthDate() != null && !card.getCardUserBirthDate().isEmpty()) {
                icardIn.put("cardUserBirthDate", card.getCardUserBirthDate());
            }
            if (card.getCardUserBrithPlace() != null && !card.getCardUserBrithPlace().isEmpty()) {
                icardIn.put("cardUserBrithPlace", card.getCardUserBrithPlace());
            }
            if (card.getCardNocaisse() != null && !card.getCardNocaisse().isEmpty()) {
                icardIn.put("cardNocaisse", card.getCardNocaisse());
            }
            return icardIn;
        }
        return null;
    }
     
      public static byte[] MapToByteArray(Map<String, Object> map) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(map);
            out.flush();
            bytes = byteOut.toByteArray();
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
            if (byteOut != null) {
                byteOut.close();
            }
        }
    }
      
}
