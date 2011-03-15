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
package org.jboss.seam.examples.booking.inventory;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Named
@SessionScoped
public class SearchCriteria implements Serializable {
    private static final char SQL_WILDCARD_CHAR = '%';
    private static final String SQL_WILDCARD_STR = String.valueOf(SQL_WILDCARD_CHAR);
    private static final String REPEAT_SQL_WIDCARD_REGEX = SQL_WILDCARD_STR + "+";
    private static final char HUMAN_WILDCARD_CHAR = '*';

    private String query = "";
    private int pageSize = 5;
    private int page = 0;

    public String getSearchPattern() {
        if (query == null || query.length() == 0) {
            return SQL_WILDCARD_STR;
        }

        StringBuilder pattern = new StringBuilder();
        pattern.append(query.toLowerCase().replace(HUMAN_WILDCARD_CHAR, SQL_WILDCARD_CHAR)
                .replaceAll(REPEAT_SQL_WIDCARD_REGEX, SQL_WILDCARD_STR));
        if (pattern.length() == 0 || pattern.charAt(0) != SQL_WILDCARD_CHAR) {
            pattern.insert(0, SQL_WILDCARD_CHAR);
        }
        if (pattern.length() > 1 && pattern.charAt(pattern.length() - 1) != SQL_WILDCARD_CHAR) {
            pattern.append(SQL_WILDCARD_CHAR);
        }
        return pattern.toString();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFetchSize() {
        return pageSize + 1;
    }

    public int getFetchOffset() {
        return page * pageSize;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = (query != null ? query.trim() : null);
    }

    public void nextPage() {
        page++;
    }

    public void previousPage() {
        if (page > 0) {
            page--;
        }
    }

    public void firstPage() {
        page = 0;
    }
}
