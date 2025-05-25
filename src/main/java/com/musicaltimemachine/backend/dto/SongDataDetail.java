package com.musicaltimemachine.backend.dto;

public class SongDataDetail {
    private Long id;
    private String title;
    private String artist;
    private String uri;
    private String chartDate;

    public SongDataDetail(Long id, String title, String artist, String uri, String chartDate) {
        this.id = id;
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

    public void setTitle() {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist() {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri() {
        this.uri = uri;
    }

    public String getChartDate() {
        return chartDate;
    }

    public void setChartDate() {
        this.chartDate = chartDate;
    }
}