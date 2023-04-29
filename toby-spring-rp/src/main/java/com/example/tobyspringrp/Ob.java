package com.example.tobyspringrp;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

  public static void main(String[] args) {
    Iterable<Integer> iter = () ->
        new Iterator<>() {
          int i = 0;
          static final int MAX = 10;

          @Override
          public boolean hasNext() {
            return i < MAX;
          }

          @Override
          public Integer next() {
            return ++i;
          } // pull
        };

    for (Integer i : iter) {
      System.out.println(i);
    }

    Observer ob = (o, arg) -> System.out.println(Thread.currentThread().getName() + " " + arg);

    IntObservable io = new IntObservable();
    io.addObserver(ob);

    ExecutorService es = Executors.newSingleThreadExecutor();
    es.execute(io);

    System.out.println(Thread.currentThread().getName() + " EXIT");
    es.shutdown();
  }

  static class IntObservable extends Observable implements Runnable {

    @Override
    public void run() {
      for (int i = 1; i <= 10; i++) {
        setChanged();
        notifyObservers(i); // push <-> Integer next()
      }
    }
  }
}
