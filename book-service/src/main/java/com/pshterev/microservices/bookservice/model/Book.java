package com.pshterev.microservices.bookservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TBL_BOOKS")
public class Book {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private long id;

   public long getId() {
      return id;
   }

   private long authorId;
   private String title;

   public long getAuthorId() {
      return authorId;
   }

   public void setAuthorId(long authorId) {
      this.authorId = authorId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}
