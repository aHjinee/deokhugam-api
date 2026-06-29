package com.sbproject.deokhugam.book.repository;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.validator.constraints.ISBN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbproject.deokhugam.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, UUID>, BookQueryRepository {

	@Query("SELECT COUNT(b) FROM Book b WHERE (:keyword IS NULL OR b.title LIKE CONCAT('%', :keyword, '%') OR b.author LIKE CONCAT('%', :keyword, '%') OR b.isbn LIKE CONCAT('%', :keyword, '%'))")
	Long countByKeyword(@Param("keyword") String keyword);

	@Query("SELECT b FROM Book b WHERE b.deletedAt IS NULL AND b.id = :bookId")
	Optional<Book> findByIdAndDeletedAtIsNull(UUID bookId);

	Optional<Book> findByIsbn(@ISBN String isbn);
}
