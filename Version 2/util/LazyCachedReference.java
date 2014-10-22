package edu.sdstate.eastweb.prototype.util;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Wraps a thread-safe object reference that is lazily initialized by atomic compare-and-set.
 * 
 * @author Michael VanBemmel
 *
 * @param <TReference> Type of the stored reference
 * @param <TException> Type of the exception thrown by get()
 */
public abstract class LazyCachedReference<TReference, TException extends Exception> {
    private final AtomicReference<TReference> mReference = new AtomicReference<TReference>();

    public LazyCachedReference() {
    }

    protected abstract TReference makeInstance() throws TException;

    public final TReference get() throws TException {
        // Attempt to retrieve the reference
        TReference reference = mReference.get();
        if (reference != null) {
            return reference;
        }

        // Make a new reference
        reference = makeInstance();

        // Cache this reference
        if (mReference.compareAndSet(null, reference)) {
            return reference; // This one was cached successfully
        } else {
            return mReference.get(); // Another thread beat us to it
        }
    }
}