package com.sbproject.deokhugam.book.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

import com.sbproject.deokhugam.book.dto.BookDto;
import com.sbproject.deokhugam.book.dto.CursorPageResponseBookDto;
import com.sbproject.deokhugam.book.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

	BookDto toBookDto(Book book);

}
