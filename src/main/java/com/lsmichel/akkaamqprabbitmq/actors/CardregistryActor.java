/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq.actors;

import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.amqp.AmqpCredentials;
import akka.stream.alpakka.amqp.AmqpDetailsConnectionProvider;
import akka.stream.alpakka.amqp.AmqpSinkSettings;
import akka.stream.alpakka.amqp.IncomingMessage;
import akka.stream.alpakka.amqp.NamedQueueSourceSettings;
import akka.stream.alpakka.amqp.QueueDeclaration;
import akka.stream.alpakka.amqp.javadsl.AmqpSink;
import akka.stream.alpakka.amqp.javadsl.AmqpSource;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.lsmichel.akkaamqprabbitmq.message.ICardMessages;
import com.lsmichel.akkaamqprabbitmq.message.ICardMessages.CardCreateActionPerformet;
import com.lsmichel.akkaamqprabbitmq.message.ICardMessages.InfoCard;
import com.lsmichel.akkaamqprabbitmq.helper.Utilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author lathsessakpamichel
 */
public class CardregistryActor extends AbstractActor {
     private  Map<String , Integer> currentMapCount = new  HashMap<String , Integer> ();
     private  Map<String ,List<Map<String ,Object>>> curretcards = new HashMap<String ,List<Map<String ,Object>>>();
     private final ActorRef child = getContext().actorOf(Props.empty(), "target");
    public static Props props() {
    return Props.create(CardregistryActor.class);
  }
    @Override
      public void preStart() {
    }
     @Override
     public void preRestart(Throwable reason, Optional<Object> message) {
     
          ActorSystem system = ActorSystem.create();
          Materializer materializer = ActorMaterializer.create(system);
         for (ActorRef each : getContext().getChildren()) {
             
              getContext().unwatch(each);
              getContext().stop(each);
         }
         postStop();
     }
     
     @Override
     public void postRestart(Throwable reason) {
        preStart();
     }
     
    @Override
    public void postStop() {
     }
    
    @Override
    public Receive createReceive() {
           
            return receiveBuilder()   
            .match(ICardMessages.CardCreate.class, (ICardMessages.CardCreate cardInfo) -> {
                 ActorSystem system = ActorSystem.create();
                 Materializer materializer = ActorMaterializer.create(system);
                 ActorRef sender =  getSender();
                 if(cardInfo!= null && cardInfo.getCard() !=null && ! cardInfo.getCard().getCardNocaisse().isEmpty()){
                     Card card = cardInfo.getCard();
                     
                      try {
                          Boolean pushed = pushCardData(materializer ,cardInfo);
                          CardCreateActionPerformet cardCreateActionPerformet=null;
                           if(pushed ==true){
                               IncreasePosQueNum(card.getCardNocaisse());
                               cardCreateActionPerformet = new CardCreateActionPerformet("created", cardInfo.getCard().getCardNumber() , false , "" );
                            }
                           else {
                               cardCreateActionPerformet = new CardCreateActionPerformet("error" , cardInfo.getCard().getCardNumber() , true , "une erreur s'est produit pendant la transaction"); 
                            }
                            sender
                             .tell(cardCreateActionPerformet ,getSelf());
                            system.terminate();
                       } finally {
                         system.terminate();
                       }
                 }
            })
             .match(ICardMessages.CardsPush.class, (ICardMessages.CardsPush cardsInfo) -> {
                 ActorSystem system = ActorSystem.create();
                 Materializer materializer = ActorMaterializer.create(system);
                 ActorRef sender =  getSender();
                 if(cardsInfo!= null && cardsInfo.getCards() !=null &&  cardsInfo.getCards().size() >0){
                      try {
                          Boolean pushed = pushCardData(materializer ,cardsInfo);
                          CardCreateActionPerformet cardCreateActionPerformet=null;
                           if(pushed ==true){
                               cardCreateActionPerformet = new CardCreateActionPerformet("created", cardsInfo.getCards().get(0).getCardNocaisse() , false , "" );
                            }
                           else {
                               cardCreateActionPerformet = new CardCreateActionPerformet("error" , cardsInfo.getCards().get(0).getCardNocaisse() , true , "une erreur s'est produit pendant la transaction"); 
                            }
                            sender
                             .tell(cardCreateActionPerformet ,getSelf());
                            system.terminate();
                       } finally {
                         system.terminate();
                       }
                 }
            })
            .match(PosCard.class, (PosCard posCardInfo) -> {
                ActorRef sender =  getSender();
                ActorSystem system = ActorSystem.create();
                Materializer materializer = ActorMaterializer.create(system);
                try {
                      HashMap<String, Object>  pullResult = pullCardData(materializer , posCardInfo.getCardNocaisse()); 
                      InfoCard infoCard = new InfoCard(false, "", pullResult); 
                      DecreasePosQueNum(posCardInfo.getCardNocaisse());
                      sender.tell(infoCard ,getSelf());
                      system.terminate();
                      }catch(Exception ex){
                        ex.printStackTrace();
                        InfoCard infoCard = new InfoCard(true, ex.getMessage(), null); 
                        sender.tell(infoCard ,getSelf());
                        system.terminate();
                  } finally{
                system.terminate();
            }
            })
            .match(Terminated.class, t ->  {
            })
            .matchAny(t -> {
            })
            .build();
    }
    
    public static class PosCard{
         private final String cardNocaisse ; 
         
         public PosCard() {
            this.cardNocaisse = "test";
        }
        public PosCard(String cardNocaisse) {
            this.cardNocaisse = cardNocaisse;
        }

        public String getCardNocaisse() {
            return cardNocaisse;
        }
          
    }
    
     public static class Card {
        private final  int cardid;
        private final String cardUserFname ;
        private final String cardUserLname ;
        private final String cardDateEtabishment ;
        private final String cardDateEpiration ;
        private final String cardLocationEtabishment ;
        private final String cardImatriculation ;
        private final String cardNumber ;
        private final String cardUserSex ;
        private final String cardUserPhoto  ;
        private final String cardUserAdress ;
        private final String cardUserPofession ;
        private final String cardUserFatherName ;
        private final String cardUserFatherBirthDate ;
        private final String cardUserMatherBirthDate ;
        private final String cardUserMatherName ;
        private final String cardNocaisse ; 
        private final String cardUserBirthDate;
        private final String cardUserBrithPlace;
        private final String cardUserNationality;
        
     public Card() {
        this.cardid = 1;
        this.cardUserFname = "";
        this.cardUserLname = "";
        this.cardDateEtabishment = "";
        this.cardDateEpiration = "";
        this.cardLocationEtabishment = "";
        this.cardImatriculation = "";
        this.cardNumber = "";
        this.cardUserSex = "";
        this.cardUserPhoto = "";
        this.cardUserAdress = "";
        this.cardUserPofession = "";
        this.cardUserFatherName = "";
        this.cardUserFatherBirthDate = "";
        this.cardUserMatherBirthDate = "";
        this.cardUserMatherName = "";
        this.cardNocaisse = "";
        this.cardUserBirthDate="";
        this.cardUserBrithPlace="";
        this.cardUserNationality="";
        }

        public Card(int cardid, String cardUserFname, String cardUserLname, String cardDateEtabishment, 
            String cardDateEpiration, String cardLocationEtabishment, String cardImatriculation, String cardNumber, 
            String cardUserSex, String cardUserPhoto, String cardUserAdress, String cardUserPofession, 
            String cardUserFatherName, String cardUserFatherBirthDate, String cardUserMatherBirthDate, 
            String cardUserMatherName, String cardNocaisse , String cardUserBirthDate ,String cardUserBrithPlace ,
            String cardUserNationality) {
            this.cardid = cardid;
            this.cardUserFname = cardUserFname;
            this.cardUserLname = cardUserLname;
            this.cardDateEtabishment = cardDateEtabishment;
            this.cardDateEpiration = cardDateEpiration;
            this.cardLocationEtabishment = cardLocationEtabishment;
            this.cardImatriculation = cardImatriculation;
            this.cardNumber = cardNumber;
            this.cardUserSex = cardUserSex;
            this.cardUserPhoto = cardUserPhoto;
            this.cardUserAdress = cardUserAdress;
            this.cardUserPofession = cardUserPofession;
            this.cardUserFatherName = cardUserFatherName;
            this.cardUserFatherBirthDate = cardUserFatherBirthDate;
            this.cardUserMatherBirthDate = cardUserMatherBirthDate;
            this.cardUserMatherName = cardUserMatherName;
            this.cardNocaisse = cardNocaisse;
            this.cardUserBirthDate= cardUserBirthDate;
            this.cardUserBrithPlace=cardUserBrithPlace;
            this.cardUserNationality=cardUserNationality;
        }

       

        public int getCardid() {
            return cardid;
        }

        public String getCardDateEtabishment() {
            return cardDateEtabishment;
        }

        public String getCardDateEpiration() {
            return cardDateEpiration;
        }

        public String getCardLocationEtabishment() {
            return cardLocationEtabishment;
        }

        public String getCardImatriculation() {
            return cardImatriculation;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public String getCardUserSex() {
            return cardUserSex;
        }

        public String getCardUserPhoto() {
            return cardUserPhoto;
        }

        public String getCardUserAdress() {
            return cardUserAdress;
        }

        public String getCardUserPofession() {
            return cardUserPofession;
        }

        public String getCardUserFatherName() {
            return cardUserFatherName;
        }

        public String getCardUserFatherBirthDate() {
            return cardUserFatherBirthDate;
        }

        public String getCardUserMatherBirthDate() {
            return cardUserMatherBirthDate;
        }

        public String getCardUserMatherName() {
            return cardUserMatherName;
        }

        public String getCardNocaisse() {
            return cardNocaisse;
        }

        public String getCardUserFname() {
            return cardUserFname;
        }

        public String getCardUserLname() {
            return cardUserLname;
        }

        public String getCardUserBirthDate() {
            return cardUserBirthDate;
        }

        public String getCardUserBrithPlace() {
            return cardUserBrithPlace;
        }

        public String getCardUserNationality() {
            return cardUserNationality;
        }
        
        
     }
    
     private int GetCurrentPosQueNum(String pos){
         if(!currentMapCount.containsKey("clientID_"+pos))
           return 0;  
         else return currentMapCount.get("clientID_"+pos);
     }
    private void IncreasePosQueNum(String pos){
         int posQueNum=0; 
        if(currentMapCount.containsKey("clientID_"+pos))
             posQueNum = currentMapCount.get("clientID_"+pos);
         currentMapCount.put("clientID_"+pos, posQueNum+1);  
    }
    private void DecreasePosQueNum(String pos){
        if(currentMapCount.containsKey("clientID_"+pos)){
            int posQueNum = currentMapCount.get("clientID_"+pos);
            currentMapCount.put("clientID_"+pos, posQueNum-1);
        }
        
    }
    
    private void AddCardTocurrent(String pos , Map<String ,Object> card){
        if(!curretcards.containsKey("clientID_"+pos)){
            System.out.println("clientID_"+pos);
            List<Map<String ,Object>> poscurretcards = new ArrayList<Map<String ,Object>>();
            poscurretcards.add(card);
            curretcards.put("clientID_"+pos, poscurretcards);
        }
        else {
            curretcards.get("clientID_"+pos).add(card);
        }
    }
    
    private boolean pushCardData(Materializer materializer, ICardMessages.CardCreate cardInfo) throws IOException {
        if (cardInfo != null) {
            Card card = cardInfo.getCard();
            Map<String, Object> icardIn = Utilities.generateCardMap(card);
            
            if (icardIn != null && !icardIn.keySet().isEmpty()) {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = null;
                byte[] bytes = Utilities.MapToByteArray(icardIn);
                
                if (bytes != null) {
                    List<byte[]> input = Arrays.asList(bytes);
                    try {
                        
                        final QueueDeclaration queueDeclaration = QueueDeclaration.create("clientID_" + card.getCardNocaisse());
                        final String queueName = "clientID_" + card.getCardNocaisse();
                        AmqpCredentials amqpCredentials = AmqpCredentials.create("root", "root");
                        AmqpDetailsConnectionProvider connectionProvider
                                = AmqpDetailsConnectionProvider.create("rabbitmq", 5672)
                                        .withHostAndPort("rabbitmq", 5672)
                                        .withVirtualHost("vhost")
                                        .withCredentials(amqpCredentials);
                        ByteString.fromArray(bytes);
                        Sink<ByteString, CompletionStage<Done>> amqpSink
                                = AmqpSink.createSimple(
                                        AmqpSinkSettings.create(connectionProvider)
                                                .withRoutingKey(queueName)
                                                .withDeclaration(queueDeclaration));
                        Source
                                .from(input).map(ByteString::fromArray)
                                .runWith(amqpSink, materializer)
                                .toCompletableFuture()
                                .join();
                        return true;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }

                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        return false;
    }
     private boolean pushCardData(Materializer materializer , ICardMessages.CardsPush cardsInfo) throws IOException {
           if(  cardsInfo!= null 
                   && cardsInfo.getCards() !=null 
                   && ! cardsInfo.getCards().isEmpty()){
                   List<byte[]> input = new ArrayList<byte[]>();
                   cardsInfo.getCards().forEach(card -> {
                        Map<String, Object> icardIn = Utilities.generateCardMap(card);
                        if(icardIn!=null){
                            byte[] bytes = null;
                            try {
                                bytes = Utilities.MapToByteArray(icardIn);
                            } catch (IOException ex) {
                                Logger.getLogger(CardregistryActor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            input.add(bytes);
                        }
                    });
                     if(!input.isEmpty()){
                      
                          try {
                            final QueueDeclaration queueDeclaration = QueueDeclaration.create("clientID_"+cardsInfo.getCards().get(0).getCardNocaisse());
                            final String queueName = "clientID_"+cardsInfo.getCards().get(0).getCardNocaisse();
                            AmqpCredentials amqpCredentials = AmqpCredentials.create("root", "root");
                            AmqpDetailsConnectionProvider connectionProvider =
                            AmqpDetailsConnectionProvider.create("rabbitmq", 5672)
                              .withHostAndPort("rabbitmq", 5672)
                              .withVirtualHost("vhost")
                              .withCredentials(amqpCredentials);
                            Sink<ByteString, CompletionStage<Done>>  amqpSink =
                           AmqpSink.createSimple(
                           AmqpSinkSettings.create(connectionProvider)
                            .withRoutingKey(queueName)
                            .withDeclaration(queueDeclaration));
                           Source
                              .from(input).map(ByteString::fromArray)
                              .runWith(amqpSink, materializer)
                              .toCompletableFuture()
                              .join();
                          return true;
                     }catch(Exception ex){
                         ex.printStackTrace();
                         return false ;
                     }
                       
                   }
                }
           else {
               return false ;
           }
           
          return false;
    }
    public HashMap<String, Object> pullCardData(Materializer materializer, String pos){
         try {
             final HashMap<String, Object> pullResult ;
             final Integer bufferSize = 1;
             final QueueDeclaration queueDeclaration = QueueDeclaration.create("clientID_"+pos);
             final String queueName = "clientID_"+pos;
             AmqpCredentials amqpCredentials = AmqpCredentials.create("root", "root");
             AmqpDetailsConnectionProvider connectionProvider =
                     AmqpDetailsConnectionProvider.create("rabbitmq", 5672)
                             .withHostAndPort("rabbitmq", 5672)
                             .withVirtualHost("vhost")
                             .withCredentials(amqpCredentials);
            
             final Source<IncomingMessage, NotUsed> amqpSource =
                     AmqpSource.atMostOnceSource(
                             NamedQueueSourceSettings.create(connectionProvider, queueName)
                                     .withDeclaration(queueDeclaration),
                             bufferSize);
             final CompletionStage<List<Object>> result =
                     amqpSource.map(t ->
                     {
                         System.out.println("test");
                         Object obj = null;
                         ByteArrayInputStream byteIn = null;
                         ObjectInputStream In = null;
                         try {
                             byteIn = new ByteArrayInputStream(t.bytes().toArray());
                             In = new ObjectInputStream(byteIn);
                             obj = In.readObject();
                            
                           } catch(Exception ex) {
                               ex.printStackTrace();
                               return null;
                           }
                            finally {
                            if (byteIn != null) {
                                  byteIn.close();
                            }
                            if (In != null) {
                               In.close();
                           }
                         }
                         return obj;
                      }
                     ).take(1).runWith(Sink.seq(), materializer);
             
             List<Object> msg = result.toCompletableFuture().get(20000, TimeUnit.MILLISECONDS);
             if(msg!=null && msg.get(0)!=null){
                pullResult =  (HashMap<String, Object> ) msg.get(0);
                return pullResult;
             }
             
            } catch (InterruptedException ex) {
                 return null ;
            } catch (ExecutionException ex) {
                 return null;
            } catch (TimeoutException ex) {
                 return null;
           }
         return null;
    }
    
}
