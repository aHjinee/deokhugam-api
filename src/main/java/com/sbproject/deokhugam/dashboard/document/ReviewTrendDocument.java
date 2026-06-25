package com.sbproject.deokhugam.dashboard.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "review_trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTrendDocument {

    @Id
    private String id;

    private String date;

    private Long reviewCount;
}