package edu.sdstate.eastweb.prototype.scheduler.framework;

public interface Action<T> {
    public void act(T value) throws Exception;
}