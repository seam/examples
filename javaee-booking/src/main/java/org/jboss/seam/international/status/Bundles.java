/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.international.status;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Maintains a global map of {@link ResourceBundle} objects.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named
@ApplicationScoped
public class Bundles implements Map<String, ResourceBundle>, Serializable
{
   private static final long serialVersionUID = 1207758648760266247L;

   private final Map<String, ResourceBundle> bundles = new ConcurrentHashMap<String, ResourceBundle>();

   @Inject
   private Instance<Locale> locale;

   public void clear()
   {
      bundles.clear();
   }

   public boolean containsKey(final Object key)
   {
      return bundles.containsKey(key);
   }

   public boolean containsValue(final Object value)
   {
      return bundles.containsValue(value);
   }

   public Set<java.util.Map.Entry<String, ResourceBundle>> entrySet()
   {
      return bundles.entrySet();
   }

   public ResourceBundle get(final Object key)
   {
      Locale loc = locale.get();
      String lookup = key.toString() + loc.toString();
      if (!containsKey(lookup))
      {
         ResourceBundle bundle = ResourceBundle.getBundle(key.toString(), loc);
         bundles.put(lookup, bundle);
      }
      return bundles.get(lookup);
   }

   public boolean isEmpty()
   {
      return bundles.isEmpty();
   }

   public Set<String> keySet()
   {
      return keySet();
   }

   public ResourceBundle put(final String key, final ResourceBundle value)
   {
      return put(key, value);
   }

   public void putAll(final Map<? extends String, ? extends ResourceBundle> m)
   {
      bundles.putAll(m);
   }

   public ResourceBundle remove(final Object key)
   {
      return bundles.remove(key);
   }

   public int size()
   {
      return bundles.size();
   }

   public Collection<ResourceBundle> values()
   {
      return bundles.values();
   }
}