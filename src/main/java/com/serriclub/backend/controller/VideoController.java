package com.serriclub.backend.controller;

import com.serriclub.backend.entity.Video;
import com.serriclub.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @GetMapping
    public Page<Video> listVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return videoRepository.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Video> searchVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return videoRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            query, query, pageable);
    }
}