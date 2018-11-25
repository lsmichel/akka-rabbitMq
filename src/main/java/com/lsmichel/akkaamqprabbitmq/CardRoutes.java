package com.lsmichel.akkaamqprabbitmq;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCode;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.AccessControlAllowOrigin;
import static akka.http.javadsl.model.headers.HttpOriginRanges.ALL;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;



import akka.http.javadsl.server.AllDirectives;
import akka.http.scaladsl.model.HttpResponse;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.CardCreateActionPerformet;
import com.lsmichel.akkaamqprabbitmq.CardregistryActor.Card;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.CardCreate;
import com.lsmichel.akkaamqprabbitmq.CardregistryActor.PosCard;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.CardPersistActionPerformet;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.CardsPush;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.InfoCard;
import com.lsmichel.akkaamqprabbitmq.ICardMessages.PersistCreate;


/**
 *
 * @author lathsessakpamichel
 */
public class CardRoutes extends AllDirectives{
    final private ActorRef cardRegistryActor;
    final private ActorRef cardPersistanceActor;
    final private LoggingAdapter log;
    Timeout timeout = new Timeout(Duration.create(200000, TimeUnit.SECONDS));
            
    public CardRoutes(ActorSystem system, ActorRef cardRegistryActor ,ActorRef cardPersistanceActor ) {
        this.cardRegistryActor = cardRegistryActor;
        this.cardPersistanceActor=cardPersistanceActor;
        log = Logging.getLogger(system, this);
    }
    public Route routes() {
        
        return route(
                options (() ->
                      complete(
                              HttpResponse
                                      .create()
                                      .withStatus(StatusCodes.OK)
                                      .addHeader(AccessControlAllowOrigin.create(ALL))
                      )
                    )
                   ,
        
                pathPrefix("saveCard", () ->
                   route(
                      PostCard()
                   )
                 ),
                 pathPrefix("PushCards", () ->
                   route(
                      PushCards()
                   )
                 ),
                pathPrefix("persistCard", () ->
                   route(
                      persistCard()
                   )
                 ),
                  pathPrefix("getCard", () ->
                   route(
                      getCard()
                  )
               )
        );
    }
    private Route PostCard() {
        return pathEnd(() ->
            route(
                post(() ->
                    entity(
                        Jackson.unmarshaller(Card.class),
                        card -> {
                            
                            CompletionStage<CardCreateActionPerformet> cardCreated = PatternsCS
                                .ask(cardRegistryActor, new CardCreate (card), timeout)
                                .thenApply(obj ->(CardCreateActionPerformet) obj);
                            return onComplete(() -> cardCreated,
                                performed -> {
                                     if(performed.isSuccess()){
                                         
                                         return complete(StatusCodes.CREATED, performed.get(), Jackson.marshaller()) ;
                                     }
                                     else{
                                         String exceptionMessage = performed.failed().get().getMessage();
                                         CardCreateActionPerformet   cardCreateActionPerformet = new CardCreateActionPerformet("error" , card.getCardNumber() , true , "exception");
                                        return complete(StatusCodes.CREATED, cardCreateActionPerformet , Jackson.marshaller());  
                                     }
                                    
                                });
                        }))
            )
        );
    }
    
      private Route PushCards() {
        return pathEnd(() ->
            route(
                post(() ->
                    entity(
                        Jackson.unmarshaller(CardsPush.class),
                        cards -> {
                            
                            CompletionStage<CardCreateActionPerformet> cardCreated = PatternsCS
                                .ask(cardRegistryActor, cards, timeout)
                                .thenApply(obj ->(CardCreateActionPerformet) obj);
                            return onComplete(() -> cardCreated,
                                performed -> {
                                     if(performed.isSuccess()){
                                         
                                         return complete(StatusCodes.CREATED, performed.get(), Jackson.marshaller()) ;
                                     }
                                     else{
                                         String exceptionMessage = performed.failed().get().getMessage();
                                         CardCreateActionPerformet   cardCreateActionPerformet = new CardCreateActionPerformet("error" , "" , true , "exception");
                                        return complete(StatusCodes.CREATED, cardCreateActionPerformet , Jackson.marshaller());  
                                     }
                                    
                                });
                        }))
            )
        );
    }
    
   // private Route = cors(){
    
 //}
    
     private Route getCard() {
         
        return pathEnd(() ->
            route(  parameter("nocaisse", nocaisse ->
                get(() -> 
                    { 
                        System.out.println(nocaisse);
                        CompletionStage<InfoCard> cardCreated = PatternsCS
                                .ask(cardRegistryActor, new PosCard (nocaisse), timeout)
                                .thenApply(obj ->(InfoCard) obj);
                        return onComplete(() -> cardCreated,
                                infoOrException -> {
                                    if(infoOrException.isSuccess()){
                                        return complete(StatusCodes.CREATED, infoOrException.get(), Jackson.marshaller());
                                    }
                                    else{
                                        InfoCard infoCard = new InfoCard(true, "exception", null); 
                                        return complete(StatusCodes.CREATED, infoCard, Jackson.marshaller());
                                    }
                                   
                                });
                        }
                   )  
                )
            )
        );
    }
     
     private Route persistCard() {
         return pathEnd(() ->
            route(
                post(() ->
                    entity(
                        Jackson.unmarshaller(Card.class),
                        card -> {
                            CompletionStage<CardPersistActionPerformet> cardPersist = PatternsCS
                                .ask(cardPersistanceActor, card, timeout)
                                .thenApply(obj ->(CardPersistActionPerformet) obj);
                            return onComplete(() -> cardPersist,
                                performed -> {
                                    
                                    return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
                                });
                        }))
            )
        );
    }

    private Object HttpResponse(StatusCode OK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
