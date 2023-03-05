package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;
    private HashMap<Song,Album> songAlbumMap;
    private HashMap<Album,Artist> albumArtistMap;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();
        songAlbumMap = new HashMap<>();
        albumArtistMap = new HashMap<>();


        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        users.add(new User(name,mobile));
        System.out.println(users.get(users.size()-1));
        return users.get(users.size()-1);
    }

    public Artist createArtist(String name) {
        artists.add(new Artist(name));
        System.out.println(artists.get(artists.size()-1));
        return artists.get(artists.size()-1);
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = null;
        for(Artist singer : artists){
            if(singer.getName().equals(artistName)){
                artist = singer;
                System.out.println(artist);
                break;
            }
        }
        if(artist==null){
            artist = createArtist(artistName);
            System.out.println(artist);
            artists.add(artist);
        }
        Album newalbum = new Album(title);
        albums.add(newalbum);
        System.out.println(newalbum);
        List<Album> albumList = new ArrayList<>();
        if(artistAlbumMap.containsKey(artist)){
            albumList=artistAlbumMap.get(artist);
        }
        albumArtistMap.put(newalbum,artist);
       albumList.add(newalbum);
       // List<Album> albumList = artistAlbumMap.getOrDefault(artist,new ArrayList<>());
        // albumList.add(newalbum);
        artistAlbumMap.put(artist,albumList);
        return newalbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        try {
            Album album = null;
           for(Album album1 : albums){
               System.out.println(album);
               if(album1.getTitle().equals(albumName)){
                   album = album1;
                   break;
               }
           }
            if(album == null) throw new Exception("Album does not exist");

            Song newsong = new Song(title, length);
            songAlbumMap.put(newsong,album);
            songs.add(newsong);
            System.out.println(newsong);
//            List<Song> songList = albumSongMap.getOrDefault(album, new ArrayList<>());
//            songList.add(newsong);
            List<Song> songList = new ArrayList<>();
            if(albumSongMap.containsKey(album)){
                songList = albumSongMap.get(album);
            }
            songList.add(newsong);
            albumSongMap.put(album, songList);
            return newsong;
        }
        catch (Exception r){
            System.out.println(r);
            return null;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist newplaylist = new Playlist(title);
        try {
            User user = null;
            for (User user1 : users) {
                if (user1.getMobile().equals(mobile)) {
                    user = user1;
                    break;
                }
            }
            System.out.println(newplaylist);
            if(user == null) throw new Exception("User does not exist");

            System.out.println(newplaylist);
            List<Song> songList = new ArrayList<>();
            for (Song song : songs) {
                System.out.println(song);
                if (song.getLength() == length) {
                    songList.add(song);
                }
            }
            playlistSongMap.put(newplaylist, songList);
            playlists.add(newplaylist);
            List<User> userList = playlistListenerMap.getOrDefault(newplaylist, new ArrayList<>());
            userList.add(user);
            playlistListenerMap.put(newplaylist, userList);
            creatorPlaylistMap.put(user, newplaylist);

        }
        catch (Exception e){
            System.out.println(e);

        }
        return newplaylist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist newplaylist = new Playlist(title);
        try {
            User user = new User();
            for (User user1 : users) {
                if (user1.getMobile().equals(mobile)) {
                    user = user1;
                    break;
                }
            }
            if (user == null) throw new Exception("User does not exist");

            System.out.println(newplaylist);
            List<Song> songList = new ArrayList<>();
            for (Song song : songs) {
                if (songTitles.contains(song.getTitle())) {
                    songList.add(song);
                    System.out.println(song);
                }
            }
            playlistSongMap.put(newplaylist, songList);
            playlists.add(newplaylist);
            List<User> userList = playlistListenerMap.getOrDefault(newplaylist, new ArrayList<>());
            userList.add(user);
            playlistListenerMap.put(newplaylist, userList);
            creatorPlaylistMap.put(user, newplaylist);

        }
        catch (Exception e){
            System.out.println(e);

        }
        return newplaylist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist1 = null;
        try {

            User user = null;
            for (User user1 : users) {
                if (user1.getMobile().equals(mobile)) {
                    user = user1;
                    break;
                }
            }
            if (user == null) throw new Exception("User not found");
            System.out.println(user);

            for (Playlist playlist : playlists) {
                if (playlist.getTitle().equals(playlistTitle)) {
                    playlist1 = playlist;
                    if (/*!creatorPlaylistMap.containsKey(playlist) ||*/ !playlistListenerMap.get(playlist).contains(user)) {
                        List<User> userList = playlistListenerMap.get(playlist);
                        userList.add(user);
                        playlistListenerMap.put(playlist, userList);
                    }
                    break;
                }
            }
            System.out.println(playlist1);

        }
        catch (Exception e){
            System.out.println(e);

        }
        return playlist1;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song song = null;
        try {
            User user = null;
            for (User user1 : users) {
                if (user1.getMobile().equals(mobile)) {
                    user = user1;
                    break;
                }
            }
            if (user == null) throw new Exception("User does not exist");


            for (Song song1 : songs) {
                if (song1.getTitle().equals(songTitle)) {
                    song = song1;

                    List<User> userList = songLikeMap.getOrDefault(song, new ArrayList<>());

                    if (!userList.contains(user)) {
                        userList.add(user);
                        songLikeMap.put(song, userList);
                        song.setLikes(song.getLikes()+1);
                        Album contains = songAlbumMap.get(song);
                        Artist belongsTo = albumArtistMap.get(contains);
                        artists.remove(belongsTo);
                        belongsTo.setLikes(belongsTo.getLikes() + 1);

                        artists.add(belongsTo);
                    }

                    System.out.println(song);

                }
                break;
            }
            return song;
        }
        catch (Exception e){
            System.out.println(e);
            return song;
        }

    }

    public String mostPopularArtist() {
        String name= null;
        int cnt=-1;
        for(Artist artist :artists){
            System.out.println(artist);
            if(artist.getLikes()>=cnt){
                cnt = artist.getLikes();
                name = artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name = null;
        int max = -1;
        for(Song song : songs){
            System.out.println(song);
            if(song.getLikes() >= max){
                name = song.getTitle();
                max = song.getLikes();
            }
        }

        return name;
    }
}
