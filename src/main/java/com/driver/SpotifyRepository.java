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
        return users.get(users.size()-1);
    }

    public Artist createArtist(String name) {
        artists.add(new Artist(name));
        return artists.get(artists.size()-1);
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = null;
        for(Artist singer : artists){
            if(singer.getName().equals(artistName)){
                artist = singer;
                break;
            }
        }
        if(artist==null){
            artist = createArtist(artistName);
            artists.add(artist);
        }
        Album newalbum = new Album(title);
        albums.add(newalbum);
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
               if(album1.getTitle().equals(albumName)){
                   album = album1;
                   break;
               }
           }
            if(album == null) throw new Exception("Album does not exist");

            Song newsong = new Song(title, length);
            songAlbumMap.put(newsong,album);
            songs.add(newsong);
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

            return null;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        User user = null;
        for (User user1 : users) {
            if (user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }

        if(user == null) throw new Exception("User does not exist");

        Playlist newplaylist = new Playlist(title);

        List<Song> songList = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLength() == length) {
                songList.add(song);
            }
        }

        playlistSongMap.put(newplaylist, songList);
        playlists.add(newplaylist);

        List<Playlist> userplayList = new ArrayList<>();
        if(userPlaylistMap.containsKey(user)){
            userplayList = userPlaylistMap.get(user);
        }
        userplayList.add(newplaylist);
        userPlaylistMap.put(user,userplayList);

        List<User> userList = new ArrayList<>();
        userList.add(user);

//        List<User> userList = playlistListenerMap.getOrDefault(newplaylist, new ArrayList<>());
//        userList.add(user);

        playlistListenerMap.put(newplaylist, userList);
        creatorPlaylistMap.put(user, newplaylist);
        return newplaylist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

       User user = null;
       for(User user1 : users){
           if (user1.getMobile().equals(mobile)){
               user = user1;
               break;
           }
       }
       if(user == null ) throw new Exception("User does not exist");

       List<Song> listOfSongToBeAdded = new ArrayList<>();
       for(Song song : songs){
           if(songTitles.contains(song.getTitle())){
               listOfSongToBeAdded.add(song);
           }
       }

       Playlist playlist = new Playlist(title);
       playlists.add(playlist);

       List<Playlist> playlistCreatedByUser = new ArrayList<>();
       if(userPlaylistMap.containsKey(user)){
           playlistCreatedByUser = userPlaylistMap.get(user);
       }
       playlistCreatedByUser.add(playlist);
       userPlaylistMap.put(user,playlistCreatedByUser);

       creatorPlaylistMap.put(user,playlist);
       List<User> currentlyListeningUsers = new ArrayList<>();
       currentlyListeningUsers.add(user);

       playlistListenerMap.put(playlist,currentlyListeningUsers);
       playlistSongMap.put(playlist,listOfSongToBeAdded);

       return playlist;
//        User user = new User();
//        for (User user1 : users) {
//            if (user1.getMobile().equals(mobile)) {
//                user = user1;
//                break;
//            }
//        }
//
//        if (user == null) throw new Exception("User does not exist");
//
//        Playlist newplaylist = new Playlist(title);
//
//        List<Song> songList = new ArrayList<>();
//        for (Song song : songs) {
//            if (songTitles.contains(song.getTitle())) {
//                songList.add(song);
//            }
//        }
//
//        playlistSongMap.put(newplaylist, songList);
//        playlists.add(newplaylist);
//
//        List<Playlist> userplayList = new ArrayList<>();
//        if(userPlaylistMap.containsKey(user)){
//            userplayList = userPlaylistMap.get(user);
//        }
//        userplayList.add(newplaylist);
//        userPlaylistMap.put(user,userplayList);
//
////        List<User> userList = playlistListenerMap.getOrDefault(newplaylist, new ArrayList<>());
////        userList.add(user);
//        List<User> userList = new ArrayList<>();
//        userList.add(user);
//
//        playlistListenerMap.put(newplaylist, userList);
//        creatorPlaylistMap.put(user, newplaylist);
//
//        return newplaylist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(User user1 : users){
            if(user1.getMobile().equals(mobile)){
                user = user1;
                break;
            }
        }
        if(user == null) throw new Exception("User does not exist");

        Playlist playlist = null;
        for (Playlist playlist1 : playlists){
            if (playlist1.getTitle().equals(playlistTitle)){
                playlist = playlist1;
                break;
            }
        }
        if(playlist == null) throw new Exception("Playlist does not exist");

        if(creatorPlaylistMap.containsKey(user)){
            return playlist;
        }

        List<User> listenerlist = playlistListenerMap.get(playlist);
        for (User user1 : listenerlist){
            if(user1 == user){
                return playlist;
            }
        }

        listenerlist.add(user);
        playlistListenerMap.put(playlist,listenerlist);

        List<Playlist> userList = new ArrayList<>();
        if(userPlaylistMap.containsKey(user)){
            userList = userPlaylistMap.get(user);
        }
        userList.add(playlist);
        userPlaylistMap.put(user,userList);

        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song song = null;
        User user = null;

        for (User user1 : users) {
            if (user1.getMobile().equals(mobile)) {
                user = user1;
                break;
            }
        }
        if (user == null) throw new Exception("User does not exist");

        for(Song song1 : songs){
            if(song1.getTitle().equals(songTitle)){
                song = song1;
                break;
            }
        }
        if(song == null) throw new Exception("Song does not exist");

        int currentLikes = song.getLikes();
        song.setLikes(currentLikes+1);

        List<User> usersWhoLikesThisSong = songLikeMap.get(song);
        if(usersWhoLikesThisSong.contains(user)) return song;

        Album album = null;
        for (Album album1 : albumSongMap.keySet()){
            if(albumSongMap.get(album1).contains(song)){
                album = album1;
                break;
            }
        }

        Artist artist = null;
        for(Artist artist1 : artistAlbumMap.keySet()){
            if(artistAlbumMap.get(artist1).contains(album)){
                artist = artist1;
                break;
            }
        }

        assert artist != null;
        currentLikes = artist.getLikes();
        artist.setLikes(currentLikes+1);

        return song;
    }

    public String mostPopularArtist() {
        String name= null;
        int cnt=-1;
        for(Artist artist :artists){
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
            if(song.getLikes() >= max){
                name = song.getTitle();
                max = song.getLikes();
            }
        }

        return name;
    }
}
