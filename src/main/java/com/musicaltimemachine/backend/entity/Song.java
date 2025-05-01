package com.musicaltimemachine.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "songs", indexes = @Index(columnList = "chartDate"))
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String artist;

    private String uri;

    @Column(nullable = false)
    private LocalDate chartDate;

    public Song() {
    }

    public Song(String title, String artist, String uri, LocalDate chartDate) {
        this.title = title;
        this.artist = artist;
        this.uri = uri;
        this.chartDate = chartDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public LocalDate getChartDate() {
        return chartDate;
    }

    public void setChartDate(LocalDate chartDate) {
        this.chartDate = chartDate;
    }
}