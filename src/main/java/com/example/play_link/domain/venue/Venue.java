package com.example.play_link.domain.venue;

import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.play_link.domain.venue.enums.VenueApprovalStatus;

@Document(collection = "venues")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @MongoId
    private String id;

    private String ownerId;

    private String venueName;

    private String description;

    private Location location;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoLocation geoLocation;

    private ContactInfo contactInfo;

    private List<String> images;

    private List<String> facilities;

    private Map<String, OperatingHour> operatingHours;

    private Rating rating;

    private VenueApprovalStatus venueApprovalStatus;

    @JsonProperty("isActive")  // Force Jackson to use "isActive" instead of "active"
    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
