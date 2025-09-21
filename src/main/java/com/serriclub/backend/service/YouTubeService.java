package com.serriclub.backend.service;

import com.serriclub.backend.entity.Video;
import com.serriclub.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.time.Instant;

@Service
public class YouTubeService {

    private final VideoRepository videoRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Queue<String> apiKeys;
    private String query = "cricket";

    public YouTubeService(VideoRepository videoRepository,
                          @Value("${youtube.api.keys}") String youtubeApiKeys) {
        this.videoRepository = videoRepository;
        this.apiKeys = new LinkedList<>(List.of(youtubeApiKeys.split(",")));
    }

    @Scheduled(fixedRate = 10000)
    public void fetchAndSaveNewVideos() {
        String apiKey = apiKeys.peek();
        String url = "https://www.googleapis.com/youtube/v3/search" +
            "?part=snippet&type=video&order=date&maxResults=5&q=" +
            query + "&key=" + apiKey;
        try {
            ResponseEntity<YouTubeSearchResult> response =
                restTemplate.getForEntity(url, YouTubeSearchResult.class);
            YouTubeSearchResult result = response.getBody();
            if (result != null) {
                for (YouTubeSearchResult.Item item : result.getItems()) {
                    String vid = item.getId().getVideoId();
                    if (videoRepository.findByVideoId(vid).isEmpty()) {
                        Video v = new Video();
                        v.setVideoId(vid);
                        v.setTitle(item.getSnippet().getTitle());
                        v.setDescription(item.getSnippet().getDescription());
                        v.setPublishedAt(Instant.parse(item.getSnippet().getPublishedAt()));
                        v.setThumbnailDefaultUrl(item.getSnippet().getThumbnails().getDefault().getUrl());
                        v.setThumbnailMediumUrl(item.getSnippet().getThumbnails().getMedium().getUrl());
                        v.setThumbnailHighUrl(item.getSnippet().getThumbnails().getHigh().getUrl());
                        videoRepository.save(v);
                    }
                }
            }
        } catch (HttpClientErrorException.Forbidden ex) {
            apiKeys.add(apiKeys.poll());
            System.err.println("Quota exceeded. Rotating API key.");
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    public static class YouTubeSearchResult {
        private List<Item> items;
        public List<Item> getItems() { return items; }
        public void setItems(List<Item> items) { this.items = items; }
        public static class Item {
            private Id id;
            private Snippet snippet;
            public Id getId() { return id; }
            public Snippet getSnippet() { return snippet; }
            public void setId(Id id) { this.id = id; }
            public void setSnippet(Snippet snippet) { this.snippet = snippet; }
        }
        public static class Id {
            private String videoId;
            public String getVideoId() { return videoId; }
            public void setVideoId(String videoId) { this.videoId = videoId; }
        }
        public static class Snippet {
            private String title;
            private String description;
            private String publishedAt;
            private Thumbnails thumbnails;
            public String getTitle() { return title; }
            public String getDescription() { return description; }
            public String getPublishedAt() { return publishedAt; }
            public Thumbnails getThumbnails() { return thumbnails; }
            public void setTitle(String title) { this.title = title; }
            public void setDescription(String description) { this.description = description; }
            public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
            public void setThumbnails(Thumbnails thumbnails) { this.thumbnails = thumbnails; }
        }
        public static class Thumbnails {
            @com.fasterxml.jackson.annotation.JsonProperty("default")
            private Thumbnail aDefault;
            private Thumbnail medium;
            private Thumbnail high;
            public Thumbnail getDefault() { return aDefault; }
            public Thumbnail getMedium() { return medium; }
            public Thumbnail getHigh() { return high; }
            public void setDefault(Thumbnail aDefault) { this.aDefault = aDefault; }
            public void setMedium(Thumbnail medium) { this.medium = medium; }
            public void setHigh(Thumbnail high) { this.high = high; }
        }
        public static class Thumbnail {
            private String url;
            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
        }
    }
}