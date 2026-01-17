package com.example.play_link.infrastructure.venue;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.play_link.domain.venue.Venue;

@Repository
public interface VenueRepository extends MongoRepository<Venue, String> {
}
