package com.musicaltimemachine.backend.service;

import com.musicaltimemachine.backend.entity.Song;
import com.musicaltimemachine.backend.repository.SongRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSeederService {

    private final SongRepository songRepository;

    @Autowired
    public DataSeederService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public void seedBillboardData() {
        LocalDate startDate = LocalDate.of(1959, 8, 3);
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate current = startDate;

        while (current.isBefore(endDate)) {
            try {
                if (!songRepository.findByChartDate(current).isEmpty()) {
                    current = current.plusWeeks(1);
                    continue;
                }

                String formattedDate = current.format(formatter);
                String url = "https://www.billboard.com/charts/hot-100/" + formattedDate + "/";

                System.out.println("Fetching: " + url);

                Document doc = Jsoup.connect(url).get();

                Elements songElements = doc.select("li h3#title-of-a-story");
                Elements artistElements = doc.select("span.a-no-trucate");

                List<Song> songs = new ArrayList<>();

                int count = Math.min(songElements.size(), artistElements.size());
                for (int i = 0; i < count; i++) {
                    String title = songElements.get(i).text().trim();
                    String artist = artistElements.get(i).text().trim();

                    Song song = new Song(title, artist, current);
                    songs.add(song);
                }

                songRepository.saveAll(songs);

                System.out.println("Saved " + songs.size() + " songs for " + formattedDate);

                Thread.sleep(3000);

            } catch (IOException e) {
                System.err.println("Failed to fetch " + current + ": " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            current = current.plusWeeks(1);
        }
    }
}