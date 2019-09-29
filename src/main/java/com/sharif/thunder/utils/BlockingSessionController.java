package com.sharif.thunder.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.SessionControllerAdapter;

public class BlockingSessionController extends SessionControllerAdapter {
  private final int MAX_DELAY = 30000; // 30 second
  
  @Override
  protected void runWorker() {
    synchronized (lock) {
      if (workerHandle == null) {
        workerHandle = new BlockingQueueWorker();
        workerHandle.start();
      }
    }
  }
  
  protected class BlockingQueueWorker extends QueueWorker {
    @Override
    public void run() {
      try {
        if (this.delay > 0) {
          final long interval = System.currentTimeMillis() - lastConnect;
          if (interval < this.delay)
            Thread.sleep(this.delay - interval);
                }
      } catch (InterruptedException ex) {
        System.out.println("Unable to backoff " + ex);
      }
      while (!connectQueue.isEmpty()) {
        SessionConnectNode node = connectQueue.poll();
        try {
          node.run(connectQueue.isEmpty());
          lastConnect = System.currentTimeMillis();
          if (connectQueue.isEmpty())
            break;
          if (this.delay > 0)
            Thread.sleep(this.delay);
          int total = 0;
          while(node.getJDA().getStatus() != JDA.Status.CONNECTED 
                && node.getJDA().getStatus() != JDA.Status.SHUTDOWN 
                && total < MAX_DELAY) {
            total += 100;
            Thread.sleep(100);
          }
        } catch (InterruptedException e) {
          System.out.println("Failed to run node " + e);
          appendSession(node);
        }
      }
      synchronized (lock) {
        workerHandle = null;
        if (!connectQueue.isEmpty())
          runWorker();
      }
    }
  }
}