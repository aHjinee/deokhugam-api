package com.sbproject.deokhugam.book.mapper;

import org.mapstruct.Mapper;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

	default BookDto toBookDto(Book book) {
		return new BookDto(
			book.getId(),
			book.getTitle(),
			book.getAuthor(),
			book.getDescription(),
			book.getPublisher(),
			book.getPublishedDate(),
			book.getIsbn(),
			book.getThumbnailUrl(),
			book.getReviewCount(),
			book.getRating(),
			book.getCreatedAt(),
			book.getUpdatedAt()
		);
	}

}
