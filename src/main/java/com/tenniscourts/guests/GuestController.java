package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/tenniscourts-api/guest")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }

    @PutMapping(value = "/update/{guestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Guest> update(@PathVariable("guestId") Long guestId,
                                        @RequestBody GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.update(guestId, guestDTO));
    }

    @DeleteMapping(value = "/delete/{guestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable("guestId") Long guestId) {
        return ResponseEntity.ok(guestService.delete(guestId));
    }

    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDTO> findById(@RequestParam Long guestId) {
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @GetMapping(value = "/findByName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDTO> findByName(@RequestParam String name) {
        return ResponseEntity.ok(guestService.findGuestByName(name));
    }

    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GuestDTO>> findAll() {
        return ResponseEntity.ok(guestService.findAll());
    }
}
