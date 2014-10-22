package edu.sdstate.eastweb.prototype.download.cache;

import java.io.Serializable;

import edu.sdstate.eastweb.prototype.DataDate;

public interface Cache extends Serializable {
    public DataDate getLastUpdated();
}
