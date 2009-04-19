package org.jboss.seam.example.openid;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="post")
public class Post {
    long   id;
    String creator;
    String message;
    Date   createDate;
    
    @Id
    @GeneratedValue
    @Column(name="id")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    @Column(name="creator")
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    @Column(name="message")
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    
    @Column(name="createDate")
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
