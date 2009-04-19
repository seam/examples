package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import java.util.List;
import java.io.Serializable;

@Name("userSearch")
@Scope(ScopeType.CONVERSATION)
public class UserSearch implements Serializable {

    @In
    private UserDAO userDAO;

    @In
    private StatusMessages statusMessages;

    private User exampleUser;
    private String orderByProperty;
    private boolean orderDescending;
    private String[] ignoreProperties;
    private int rowCount;
    private int maxPageSize;
    private int pageSize;
    private int page;

    @DataModel
    private List<User> usersList;

    @Create
    public void initialize() {
        pageSize = 15;
        maxPageSize = 1000;
        exampleUser = new User();
        orderByProperty = "username";
        orderDescending = false;
        ignoreProperties = new String[]{"passwordHash", "activated", "createdOn", "lastLoginOn", "ratingScore" };
    }

    public void find() {
        page = 0;
        queryRowCount();
        if (rowCount != 0) queryUsers();
    }

    public void nextPage() {
        page++;
        queryUsers();
    }

    public void previousPage() {
        page--;
        queryUsers();
    }

    public void firstPage() {
        page = 0;
        queryUsers();
    }

    public void lastPage() {
        page = (rowCount / pageSize);
        if (rowCount % pageSize == 0) page--;
        queryUsers();
    }

    private void queryRowCount() {
        rowCount = userDAO.getRowCountByExample(exampleUser, ignoreProperties);
        if (rowCount == 0) {
            statusMessages.addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.NoUserFound",
                "No user with given attributes was found, please try again."
            );
        }
    }

    private void queryUsers() {
        usersList = userDAO.findByExample(exampleUser, orderByProperty, orderDescending, page * pageSize, pageSize, ignoreProperties);
    }

    public boolean isNextPageAvailable() {
        return usersList != null && rowCount > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return usersList != null && page > 0;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > maxPageSize ? maxPageSize : pageSize; // Prevent tampering
    }

    public int getRowCount() {
        return rowCount;
    }

    public User getExampleUser() {
        return exampleUser;
    }

    public void setExampleUser(User exampleUser) {
        this.exampleUser = exampleUser;
    }

    public String getOrderByProperty() {
        return orderByProperty;
    }

    public boolean isOrderDescending() {
        return orderDescending;
    }

    public void sortBy(String propertyName) {
        orderByProperty = propertyName;
        orderDescending = !isOrderDescending(); // Switch between ASC and DESC
        page = 0; // Reset to first page
        queryUsers();
    }

}
