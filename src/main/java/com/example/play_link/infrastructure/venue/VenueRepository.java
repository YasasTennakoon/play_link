package com.example.play_link.infrastructure.venue;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.play_link.domain.venue.Venue;

public interface VenueRepository extends MongoRepository<Venue, String> {
}
