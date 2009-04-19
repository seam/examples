package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;

import java.io.Serializable;

@Name("pager")
@Scope(ScopeType.PAGE)
@AutoCreate
public class Pager implements Serializable {

    private String pagerEventName = "Pager";
    private Long numOfRecords = 0l;
    private Integer page = 0;
    private Long pageSize = 15l;

    public Pager() {}

    public Pager(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Pager(String pagerEventName) {
        this.pagerEventName = pagerEventName;
    }

    public Pager(String pagerEventName, Long pageSize) {
        this.pagerEventName = pagerEventName;
        this.pageSize = pageSize;
    }

    public Long getNumOfRecords() {
        return numOfRecords;
    }

    public void setNumOfRecords(Long numOfRecords) {
        this.numOfRecords = numOfRecords;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page != null) this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    @RequestParameter
    public void setPageSize(Long pageSize) {
        if (pageSize != null) this.pageSize = pageSize;
    }

    public int getNextPage() {
        return page + 1;
    }

    public int getPreviousPage() {
        return page - 1;
    }

    public int getFirstPage() {
        return 0;
    }

    public long getFirstRecord() {
        return page * pageSize + 1;
    }

    public long getLastRecord() {
        return (page * pageSize + pageSize) > numOfRecords
                ? numOfRecords
                : page * pageSize + pageSize;
    }

    public long getNextRecord() {
        return page * pageSize;
    }

    public long getLastPage() {
        long lastPage = (numOfRecords / pageSize);
        if (numOfRecords % pageSize == 0) lastPage--;
        return lastPage;
    }

    public boolean isNextPageAvailable() {
        return numOfRecords > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return numOfRecords != null && page > 0;
    }

    public int getQueryFirstResult() {
        return new Long(getPage() * getPageSize()).intValue();
    }

    public int getQueryMaxResults() {
        return new Long(getPageSize()).intValue();
    }

    public boolean isSeveralPages() {
        return getNumOfRecords() != 0 && getNumOfRecords() > getPageSize();
    }

    public void setFirstPage() {
        setPage(getFirstPage());
        Events.instance().raiseEvent(pagerEventName + "pageChanged");
    }

    public void setPreviousPage() {
        setPage(getPreviousPage());
        Events.instance().raiseEvent(pagerEventName + ".pageChanged");
    }

    public void setNextPage() {
        setPage(getNextPage());
        Events.instance().raiseEvent(pagerEventName + ".pageChanged");
    }

    public void setLastPage() {
        setPage(new Long(getLastPage()).intValue());
        Events.instance().raiseEvent(pagerEventName + ".pageChanged");
    }

    public String toString() {
        return "Pager - Records: " + getNumOfRecords() + " Page size: " + getPageSize();
    }
}
