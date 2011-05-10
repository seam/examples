/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.wicket.example.publish.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;

import org.jboss.seam.wicket.example.publish.scala.model.BaseEntity;

/**
 * @param <T>
 * @author oranheim
 */
abstract public class CustomEntityHome<T extends BaseEntity> implements Serializable {

    private static final long serialVersionUID = 664187882684237542L;

    abstract public T getInstance();

    abstract public void setInstance(T instance);

    abstract public T createInstance();

    abstract public T loadInstance();

    protected T findInstance(EntityManager em, Long id) {
        return em.find(getClassType(), id);
    }

    abstract public boolean isManaged();

    abstract public void initConversation();

    abstract public void completeConversation();

    abstract public String save();

    abstract public String cancel();

    abstract public String delete();

    @SuppressWarnings("unchecked")
    protected Class<T> getClassType() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

}
