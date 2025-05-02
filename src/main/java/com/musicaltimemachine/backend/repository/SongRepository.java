package com.musicaltimemachine.backend.repository;

import com.musicaltimemachine.backend.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByChartDate(LocalDate chartDate);
    List<String> findUrisByChartDate(@Param("chartDate") LocalDate chartDate);
}
