package com.example.demo.Controller;

import com.example.demo.Model.Song;
import com.example.demo.Service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*") 
public class SongController {

  @Autowired
  private SongService songService;

  @GetMapping("/top")
  public List<Song> getTopSongs() {
    return songService.getTopSongs();
  }


  @GetMapping("/category/{id}")
  public List<Song> getSongsByCategory(@PathVariable Integer id) {
    return songService.getSongsByCategory(id);
  }

  @GetMapping("/search")
  public List<Song> searchSongs(@RequestParam("keyword") String keyword) {
    return songService.searchSongs(keyword);
  }
}
