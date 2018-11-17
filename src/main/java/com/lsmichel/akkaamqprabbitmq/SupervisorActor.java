/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;

/**
 *
 * @author lathsessakpamichel
 */
public class SupervisorActor extends AbstractActor{
    static Props props() {
      return Props.create(SupervisorActor.class, () -> new SupervisorActor());
    }
    public SupervisorActor() {
    }

   @Override
  public Receive createReceive() {
    return receiveBuilder()
      .match(ActorRef.class, actor -> {
          System.out.println("========================================================> "+ actor.path().name());
          getContext().watch(actor);
      })
      .match(Terminated.class, t  -> {
         System.out.println("=========================================================> ");
      })
      .matchAny(t -> {
          System.out.println("=========================================================> ");
      })
      .build();
  }
  
}
