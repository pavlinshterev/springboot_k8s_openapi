package com.pshterev.microservices.uiservice.controller;

import com.pshterev.microservices.authorservice.client.api.AuthorEntityControllerApi;
import com.pshterev.microservices.authorservice.client.api.AuthorSearchControllerApi;
import com.pshterev.microservices.authorservice.client.model.CollectionModelEntityModelAuthor;
import com.pshterev.microservices.authorservice.client.model.EntityModelAuthor;
import com.pshterev.microservices.bookservice.client.api.BookEntityControllerApi;
import com.pshterev.microservices.bookservice.client.api.BookSearchControllerApi;
import com.pshterev.microservices.bookservice.client.app.ApiException;
import com.pshterev.microservices.bookservice.client.model.CollectionModelEntityModelBook;
import com.pshterev.microservices.bookservice.client.model.EntityModelBook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class UiServiceController {

   BookEntityControllerApi bookApi = new BookEntityControllerApi();
   BookSearchControllerApi bookSearchApi = new BookSearchControllerApi();
   AuthorEntityControllerApi authorApi = new AuthorEntityControllerApi();
   AuthorSearchControllerApi authorSearchApi = new AuthorSearchControllerApi();

   @GetMapping("/searchBooksByTitle")
   public List<EntityModelBook> searchBooksByTitle(@RequestParam String title) throws ApiException {
      CollectionModelEntityModelBook res = bookSearchApi.executeSearchBookGet1(title);
      return res.getEmbedded() != null ? res.getEmbedded().getBooks() : Collections.emptyList();
   }

   @GetMapping("/searchBooksByAuthorId")
   public List<EntityModelBook> searchBooksByAuthorId(@RequestParam Long authorId) throws ApiException {
      CollectionModelEntityModelBook res = bookSearchApi.executeSearchBookGet(authorId);
      return res.getEmbedded() != null ? res.getEmbedded().getBooks() : Collections.emptyList();
   }

   @GetMapping("/searchBooksByAuthorName")
   public List<EntityModelBook> searchBooksByAuthorName(@RequestParam String authorName)
           throws com.pshterev.microservices.authorservice.client.app.ApiException, ApiException {
      CollectionModelEntityModelAuthor res = authorSearchApi.executeSearchAuthorGet(authorName);
      // for simplicity let's just use the first found author
      EntityModelAuthor author =
              res.getEmbedded() != null &&
              res.getEmbedded().getAuthors() != null &&
              res.getEmbedded().getAuthors().size() > 0 ?
              res.getEmbedded().getAuthors().get(0) : null;
      if (author != null) {
         return searchBooksByAuthorId(author.getId());
      } else {
         return Collections.emptyList();
      }
   }

}

