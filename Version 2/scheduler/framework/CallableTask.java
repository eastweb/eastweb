package edu.sdstate.eastweb.prototype.scheduler.framework;

import java.util.concurrent.Callable;

public interface CallableTask<T> extends Task, Callable<T> {
    T getCanSkip();
}