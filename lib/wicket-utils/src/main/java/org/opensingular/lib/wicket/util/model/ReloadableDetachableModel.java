/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReloadableDetachableModel<T> implements IModel<T>
{
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger log = LoggerFactory.getLogger(ReloadableDetachableModel.class);

    /** keeps track of whether this model is attached or detached */
    private transient boolean attached = false;

    /** temporary, transient object. */
    private transient T transientModelObject;

    /**
     * Construct.
     */
    public ReloadableDetachableModel()
    {
    }

    /**
     * This constructor is used if you already have the object retrieved and want to wrap it with a
     * detachable model.
     * 
     * @param object
     *            retrieved instance of the detachable object
     */
    public ReloadableDetachableModel(T object)
    {
        this.transientModelObject = object;
        attached = true;
    }

    /**
     * @see org.apache.wicket.model.IDetachable#detach()
     */
    @Override
    public void detach()
    {
        if (attached)
        {
            try
            {
                onDetach();
            } finally
            {
                attached = false;
                transientModelObject = null;

                log.debug("removed transient object for {}, requestCycle {}", this,
                    RequestCycle.get());
            }
        }
    }

    /**
     * @see org.apache.wicket.model.IModel#getObject()
     */
    @Override
    public final T getObject()
    {
        if (attached && transientModelObject != null && needsReload(transientModelObject)) {
            detach();
        }

        if (!attached)
        {
            attached = true;
            transientModelObject = load();

            if (log.isDebugEnabled()) {
                log.debug("loaded transient object {} for {}, {}", transientModelObject, this, RequestCycle.get());
            }

            onAttach();
        }
        return transientModelObject;
    }

    protected boolean needsReload(T cachedObject) {
        return false;
    }

    /**
     * Gets the attached status of this model instance
     * 
     * @return true if the model is attached, false otherwise
     */
    public final boolean isAttached()
    {
        return attached;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(":attached=").append(attached).append(":tempModelObject=[").append(
            this.transientModelObject).append("]");
        return sb.toString();
    }

    /**
     * Loads and returns the (temporary) model object.
     * 
     * @return the (temporary) model object
     */
    protected abstract T load();

    /**
     * Attaches to the current request. Implement this method with custom behavior, such as loading
     * the model object.
     */
    protected void onAttach()
    {
    }

    /**
     * Detaches from the current request. Implement this method with custom behavior, such as
     * setting the model object to null.
     */
    protected void onDetach()
    {
    }

    /**
     * Manually loads the model with the specified object. Subsequent calls to {@link #getObject()}
     * will return {@code object} until {@link #detach()} is called.
     * 
     * @param object
     *            The object to set into the model
     */
    @Override
    public void setObject(final T object)
    {
        attached = true;
        transientModelObject = object;
    }

}
