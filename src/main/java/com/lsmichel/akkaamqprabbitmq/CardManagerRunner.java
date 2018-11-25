/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq;

import akka.NotUsed;
import akka.actor.ActorRef;
import static akka.actor.ActorRef.noSender;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

/**
 *
 * @author lathsessakpamichel
 */
public class CardManagerRunner extends AllDirectives{
    private final CardRoutes cardRoutes;

    public CardManagerRunner(ActorSystem system, ActorRef cardRegistryActor ,  ActorRef cardPersistanceActor) {
        cardRoutes = new CardRoutes(system, cardRegistryActor, cardPersistanceActor);
    }
   
    
    public static void run(String ip) {

        
        ActorSystem system = ActorSystem.create("cardAkkaHttpServer");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        
        ActorRef supervisorActor = system.actorOf(SupervisorActor.props(), "supervisorActor");
        
        ActorRef cardRegistryActor = system.actorOf(CardregistryActor.props(), "cardRegistryActor");
        ActorRef cardPersistanceActor = system.actorOf(PersistanceActor.props(), "cardPersistanceActor");
        
        supervisorActor.tell(cardRegistryActor, noSender());
        
        CardManagerRunner app = new CardManagerRunner(system, cardRegistryActor, cardPersistanceActor);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        http.bindAndHandle(routeFlow, ConnectHttp.toHost(ip, 8080), materializer);

        System.out.println("Serveur demarré a http://"+ip+":8080/");
        system.registerOnTermination( () -> {
             System.out.println("==========>  System is terminate");
        });
        Runtime.getRuntime().addShutdownHook(new Thread() 
          { 
             public void run() 
            { 
              system.terminate();
            } 
       }); 
     }
    
  protected Route createRoute() {
        return cardRoutes.routes();
    }
}
