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

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.wicket.example.publish.qualifier.ConversationalDataRepository;

/**
 * @author oranheim
 */
public class EntityDao implements Serializable {

    private static final long serialVersionUID = 8882687308482165893L;

    @Inject
    @ConversationalDataRepository
    private EntityManager em;

    final public EntityManager getEntityManager() {
        return em;
    }

    public void create(Object entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    public <T> T update(T entity) {
        em.getTransaction().begin();
        entity = em.merge(entity);
        em.getTransaction().commit();
        return entity;
    }

    public void delete(Object entity) {
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    public void refresh(Object entity) {
        em.refresh(entity);
    }

    public <T> T find(Class<T> clazz, Long id) {
        return em.find(clazz, id);
    }

}
