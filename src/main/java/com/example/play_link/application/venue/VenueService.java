package com.example.play_link.application.venue;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.play_link.application.common.exceptions.ResourceNotFoundException;
import com.example.play_link.domain.venue.Venue;
import com.example.play_link.infrastructure.venue.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public List<Venue> allVenues(){
        return venueRepository.findAll();
    }

    public Venue addNewVenue(Venue venue){
        // Validate venue is not null
        if (venue == null) {
            throw new IllegalArgumentException("Venue cannot be null");
        }
        
        // Save and return the venue
        return venueRepository.save(venue);
    }

    public Venue updateVenue(String venueId, Venue venue){
        // Validate inputs
        if (venueId == null || venueId.isBlank()) {
            throw new IllegalArgumentException("Venue ID cannot be null or empty");
        }
        
        if (venue == null) {
            throw new IllegalArgumentException("Venue data cannot be null");
        }
        
        // Find existing venue or throw exception
        Venue currentVenue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + venueId));
        
        // Update fields only if they are not null
        if (venue.getVenueName() != null) {
            currentVenue.setVenueName(venue.getVenueName());
        }
        if (venue.getDescription() != null) {
            currentVenue.setDescription(venue.getDescription());
        }
        if (venue.getLocation() != null) {
            currentVenue.setLocation(venue.getLocation());
        }
        if (venue.getGeoLocation() != null) {
            currentVenue.setGeoLocation(venue.getGeoLocation());
        }
        if (venue.getContactInfo() != null) {
            currentVenue.setContactInfo(venue.getContactInfo());
        }
        if (venue.getImages() != null) {
            currentVenue.setImages(venue.getImages());
        }
        if (venue.getFacilities() != null) {
            currentVenue.setFacilities(venue.getFacilities());
        }
        if (venue.getOperatingHours() != null) {
            currentVenue.setOperatingHours(venue.getOperatingHours());
        }
        if (venue.getRating() != null) {
            currentVenue.setRating(venue.getRating());
        }
        if (venue.getVenueApprovalStatus() != null) {
            currentVenue.setVenueApprovalStatus(venue.getVenueApprovalStatus());
        }
        currentVenue.setActive(venue.isActive());
        
        return venueRepository.save(currentVenue);
    }
    
}
