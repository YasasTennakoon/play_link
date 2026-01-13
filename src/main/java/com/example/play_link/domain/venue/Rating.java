package com.example.play_link.domain.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Builder.Default
    private Double average = 0.0;

    @Builder.Default
    private Integer totalReviews = 0;
}
