package com.pshterev.microservices.bookservice.data;

import com.pshterev.microservices.bookservice.model.Book;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
   List<Book> findByTitle(@Param("title") String title);
   List<Book> findByAuthorId(@Param("authorId") long authorId);
}
