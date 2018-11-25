/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq;

import java.io.Serializable;

/**
 *
 * @author lathsessakpamichel
 */
import com.lsmichel.akkaamqprabbitmq.CardregistryActor.Card;
import java.util.List;
import java.util.Map;
public interface ICardMessages {
    class CardCreate implements Serializable{
        private final Card card;

        public CardCreate(Card card) {
            this.card = card;
        }

        public Card getCard() {
            return card;
        }
        
        
    }
    class CardsPush implements Serializable{
        private final List<Card> cards;

        public CardsPush(List<Card> cards) {
            this.cards = cards;
        }

        public List<Card> getCards() {
            return cards;
        }
  
    }
    class PersistCreate implements Serializable{
        private final Card card;

        public PersistCreate(Card card) {
            this.card = card;
        }

        public Card getCard() {
            return card;
        }
        
        
    }
    class InfoCard implements Serializable{
        private final Boolean hasError;
        private final String errorMessage;
        private final Map<String , Object> info;

        public InfoCard(Boolean hasError, String errorMessage, Map<String , Object> info) {
            this.hasError = hasError;
            this.errorMessage = errorMessage;
            this.info = info;
        }

        public Boolean getHasError() {
            return hasError;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Map<String, Object> getInfo() {
            return info;
        }
        
     }
    class CardCreateActionPerformet implements Serializable{
        private final String message ;
        private final String numcard;
        private final Boolean hasError;
        private final String errorMessage;

        public CardCreateActionPerformet( String message, String numcard, Boolean hasError, String errorMessage) {
            this.message = message;
            this.numcard = numcard;
            this.hasError = hasError;
            this.errorMessage = errorMessage;
        }

       

        public String getMessage() {
            return message;
        }

        public String getNumcard() {
            return numcard;
        }

        public Boolean getHasError() {
            return hasError;
        }

       

        public String getErrorMessage() {
            return errorMessage;
        }
        
        
    }
    
        class CardPersistActionPerformet implements Serializable{
        private final String message ;
        private final String numcard;
        private final Boolean hasError;
        private final String errorMessage;

        public CardPersistActionPerformet( String message, String numcard, Boolean hasError, String errorMessage) {
            this.message = message;
            this.numcard = numcard;
            this.hasError = hasError;
            this.errorMessage = errorMessage;
        }

       

        public String getMessage() {
            return message;
        }

        public String getNumcard() {
            return numcard;
        }

        public Boolean getHasError() {
            return hasError;
        }

       

        public String getErrorMessage() {
            return errorMessage;
        }
        
        
    }

}
