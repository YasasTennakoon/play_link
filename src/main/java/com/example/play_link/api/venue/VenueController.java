package com.example.play_link.api.venue;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.play_link.application.venue.VenueService;
import com.example.play_link.domain.venue.Venue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues(){
        return ResponseEntity.ok(venueService.allVenues());
    }

    @PostMapping
    public ResponseEntity<Venue> createVenue(@Valid @RequestBody Venue venue){
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.addNewVenue(venue));
    }

    @PutMapping("/{venueId}")
    public ResponseEntity<Venue> updateVenue(@PathVariable String venueId, @Valid @RequestBody Venue venue){
        return ResponseEntity.ok(venueService.updateVenue(venueId, venue));
    }
    
}
