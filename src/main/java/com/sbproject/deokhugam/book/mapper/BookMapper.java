package com.sbproject.deokhugam.book.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.entity.Book;
import com.sbproject.deokhugam.storage.FileStorage;

@Mapper(componentModel = "spring")
public abstract class BookMapper {

	@Autowired
	protected FileStorage fileStorage;

	public BookDto toBookDto(Book book) {
		return new BookDto(
			book.getId(),
			book.getTitle(),
			book.getAuthor(),
			book.getDescription(),
			book.getPublisher(),
			book.getPublishedDate(),
			book.getIsbn(),
			fileStorage.getUrl(book.getThumbnailUrl()),
			book.getReviewCount(),
			book.getRating(),
			book.getCreatedAt(),
			book.getUpdatedAt()
		);
	}

}
