package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.dto.SongDataDetail;
import com.musicaltimemachine.backend.entity.Song;
import com.musicaltimemachine.backend.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<SongDataDetail> getSongData(String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Song> songs = songRepository.findByChartDate(parsedDate);
        return songs.stream()
                .map(song -> new SongDataDetail(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getUri(),
                        song.getChartDate().toString()
                ))
                .collect(Collectors.toList());
    }
}
