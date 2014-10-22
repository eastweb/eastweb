package edu.sdstate.eastweb.prototype.scheduler.framework;

public interface RunnableTask extends Task, ThrowingRunnable {
    boolean getCanSkip();
}