package com.pshterev.microservices.authorservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TBL_AUTHORS")
public class Author {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private long id;
   private String name;

   public long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
