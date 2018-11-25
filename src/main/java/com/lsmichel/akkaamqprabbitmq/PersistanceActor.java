/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq;

import akka.Done;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.slick.javadsl.Slick;
import akka.stream.alpakka.slick.javadsl.SlickRow;
import akka.stream.alpakka.slick.javadsl.SlickSession;
import akka.stream.javadsl.Sink;
import com.lsmichel.akkaamqprabbitmq.CardregistryActor.Card;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.CardPersistActionPerformet;
import java.util.concurrent.CompletionStage;

/**
 *
 * @author lathsessakpamichel
 */
public class PersistanceActor extends AbstractActor {
     static Props props() {
    return Props.create(PersistanceActor.class);
  }
    @Override
    public Receive createReceive() {
       return receiveBuilder()
      .match(CardregistryActor.Card.class, card -> {
          ActorRef sender =  getSender();
          ActorSystem system = ActorSystem.create();
          Materializer materializer = ActorMaterializer.create(system);
          SlickSession session = SlickSession.forConfig("slick-h2");
          system.registerOnTermination(session::close);
          CompletionStage<Done> done =
          Slick.source(
            session,
            SetCardDataInQuery(card),
              (SlickRow row) ->
                { 
                  return row;
                }
            )
         .runWith(Sink.ignore(), materializer);
          done.whenComplete(
        (value, exception) -> {
          if(value !=null){
              CardPersistActionPerformet cardPersistActionPerformet= new CardPersistActionPerformet("succes", card.getCardNumber(), false , "");
              sender
                   .tell(cardPersistActionPerformet ,getSelf());
          }
          else {
              CardPersistActionPerformet cardPersistActionPerformet= new CardPersistActionPerformet("error", card.getCardNumber(), true , exception.getMessage());
               sender
                   .tell(cardPersistActionPerformet ,getSelf());
          }
        }).toCompletableFuture().join();
        })
      .build();
    }
    
     private static String SetCardDataInQuery(CardregistryActor.Card card){
        String insertData = "INSERT INTO card_tbl VALUES (";
        insertData+=card.getCardNumber() +" , '";
        insertData+=card.getCardUserFname() +"' , '";
        insertData+=card.getCardUserLname() +"' , '";
        insertData+=card.getCardDateEtabishment() +"' , '";
        insertData+=card.getCardDateEpiration() +"' , '";
        insertData+=card.getCardLocationEtabishment() +"' , '";
        insertData+=card.getCardImatriculation() +"' , '";
        insertData+=card.getCardNumber() +"' , '";
        insertData+=card.getCardUserSex() +"' , '";
        insertData+=card.getCardUserPhoto() +"' , '";
        insertData+=card.getCardUserAdress() +"' , '";
        insertData+=card.getCardUserPofession() +"' , '";
        insertData+=card.getCardUserFatherName() +"' , '";
        insertData+=card.getCardUserFatherBirthDate() +"' , '";
        insertData+=card.getCardUserMatherName() +"' , '";
        insertData+=card.getCardUserMatherBirthDate() +"' , '";
        insertData+=card.getCardNocaisse()+"'" ;
        insertData+= ")";
        return insertData; 
     }
    
}
