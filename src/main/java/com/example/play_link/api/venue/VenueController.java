package com.example.play_link.api.venue;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.play_link.application.venue.VenueService;
import com.example.play_link.domain.venue.Venue;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public List<Venue> getAllVenues(){
        return venueService.allVenues();
    }

    @PostMapping
    public Venue createVenue(@RequestBody Venue venue){
        return venueService.addNewVenue(venue);
    }

    @PutMapping("/{venueId}")
    public Venue updateVenue(@PathVariable String venueId, @RequestBody Venue venue){
        return venueService.updateVenue(venueId, venue);
    }
    
}
