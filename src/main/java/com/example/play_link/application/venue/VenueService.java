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
        venueRepository.save(venue);
        return venue;
    }

    public Venue updateVenue(String venueId, Venue venue){
        Venue currentVenue = venueRepository.findById(venueId).orElseThrow(()-> new ResourceNotFoundException("ERROR"));
        currentVenue.setVenueName(venue.getVenueName());
        currentVenue.setDescription(venue.getDescription());
        currentVenue.setLocation(venue.getLocation());
        currentVenue.setGeoLocation(venue.getGeoLocation());
        currentVenue.setContactInfo(venue.getContactInfo());
        currentVenue.setImages(venue.getImages());
        currentVenue.setFacilities(venue.getFacilities());
        currentVenue.setOperatingHours(venue.getOperatingHours());
        currentVenue.setRating(venue.getRating());
        currentVenue.setVenueApprovalStatus(venue.getVenueApprovalStatus());
        currentVenue.setActive(venue.isActive());
        return venueRepository.save(currentVenue);

    }
    
}
