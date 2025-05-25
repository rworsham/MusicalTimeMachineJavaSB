package com.musicaltimemachine.backend.controller;


import com.musicaltimemachine.backend.dto.SongDataDetail;
import com.musicaltimemachine.backend.service.DataSeederService;
import com.musicaltimemachine.backend.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/")
public class AdminSongController {

    private final DataSeederService dataSeederService;
    private final SongService songService;

    AdminSongController(DataSeederService dataSeederService, SongService songService) {
        this.dataSeederService = dataSeederService;
        this.songService = songService;
    }

    @PostMapping("/seed-song-data")
    public ResponseEntity<?> startSongSeeding() {
        dataSeederService.runAsyncSeeding();
        return ResponseEntity.ok(Map.of("message", "Seeding started check logs for progress"));
    }

    @GetMapping("/song-data")
    public ResponseEntity<List<SongDataDetail>> songData(
            @RequestParam("date") String date
    ) {
        List<SongDataDetail> songData = songService.getSongData(date);
        return ResponseEntity.ok(songData);
    }

}
