/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 *
 * $Id$
 */
package org.jboss.seam.examples.permalink;

import javax.annotation.Named;
import javax.context.RequestScoped;
import javax.inject.Current;

import org.jboss.seam.international.StatusMessages;

public
@Named
@RequestScoped
class CommentBoard {
    @Current
    BlogEntryRepository repository;

    @Current
    Comment comment;

    @Current
    Blog blog;

    @Current
    StatusMessages statusMessages;

    public Boolean post() {
        if (comment == null || blog == null) {
            return null;
        }

        BlogEntry entry = repository.getEntry(blog.getEntryId());
        if (entry == null) {
            return null;
        }

        repository.addComment(comment, entry);
        statusMessages.add("Thanks for leaving a comment!");
        return true;
    }
}
