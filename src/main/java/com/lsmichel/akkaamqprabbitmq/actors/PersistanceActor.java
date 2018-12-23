/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq.actors;

import com.lsmichel.akkaamqprabbitmq.actors.CardregistryActor;
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
import com.lsmichel.akkaamqprabbitmq.message.ICardMessages.CardPersistActionPerformet;
import com.lsmichel.akkaamqprabbitmq.helper.Utilities;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lathsessakpamichel
 */
public class PersistanceActor extends AbstractActor {
    public static Props props() {
    return Props.create(PersistanceActor.class);
  }
    @Override
    public Receive createReceive() {
       return receiveBuilder()
      .match(CardregistryActor.Card.class, card -> {
          HashMap<String , Boolean> mapInsertQuery = new HashMap<String , Boolean>();
          ActorRef sender =  getSender();
           ActorSystem system = ActorSystem.create();
           Materializer materializer = ActorMaterializer.create(system);
           SlickSession session = SlickSession.forConfig("slick-mysql");
           system.registerOnTermination(session::close);
          String insertQuery  = Utilities.SetCardDataInQuery(card);
          if(insertQuery == null){
              CardPersistActionPerformet cardPersistActionPerformet= new CardPersistActionPerformet("une erreur s'est produite pendant le traitement de la requette", null , true , "");
              sender
                   .tell(cardPersistActionPerformet, getSelf());
          }
          else {
              try {
          
                  Done done = Slick.source(
                          session,
                          insertQuery,
                              (SlickRow row)
                                     -> {
                                         return row;
                                       }
                          )
                          .runWith(Sink.ignore(), materializer)
                          .toCompletableFuture()
                          .join();
                  CardPersistActionPerformet cardPersistActionPerformet = new CardPersistActionPerformet("sucess", card.getCardNumber(), false, "");
                  sender
                   .tell(cardPersistActionPerformet, getSelf());
                   system.terminate();
              
              }catch(Exception ex){
                  CardPersistActionPerformet cardPersistActionPerformet = new CardPersistActionPerformet("error", card.getCardNumber(), true, ex.getMessage());
                  sender
                   .tell(cardPersistActionPerformet, getSelf());
                  system.terminate();
              }
          }
       })
      .build();
    } 
}
