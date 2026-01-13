package com.example.play_link.domain.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHour {

    private String open;   // e.g., "06:00"
    private String close;  // e.g., "22:00"
    private Boolean isOpen;
}
