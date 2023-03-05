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

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

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
        if(!artists.contains(artistName)) artists.add(new Artist(artistName));
        Album newalbum = new Album(title);
        Artist artist = new Artist();
        for(Artist singer : artists){
            if(singer.getName().equals(artistName)){
                artist = singer;
                break;
            }
        }
        List<Album> albumList = artistAlbumMap.getOrDefault(artist,new ArrayList<>());
        albumList.add(newalbum);
        artistAlbumMap.put(artist,albumList);
        return newalbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        try{
            if(!albums.contains(albumName)) throw new Exception();
            Song newsong = new Song(title,length);
            songs.add(newsong);
            Album album = new Album();
            for(Album albumm : albums){
                if(albumm.getTitle().equals(albumName)){
                    album = albumm;
                    break;
                }
            }
            List<Song> songList = albumSongMap.getOrDefault(album,new ArrayList<>());
            songList.add(newsong);
            albumSongMap.put(album,songList);
            return newsong;
        }
        catch (Exception e){
            return null;
        }

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist newplaylist = new Playlist(title);
        List<Song> songList = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength()==length){
                songList.add(song);
            }
        }
        playlistSongMap.put(newplaylist,songList);
        playlists.add(newplaylist);
        User user = new User("",mobile);
        users.add(user);
        List<User> userList = playlistListenerMap.getOrDefault(newplaylist,new ArrayList<>());
        userList.add(user);
        playlistListenerMap.put(newplaylist,userList);
        creatorPlaylistMap.put(user,newplaylist);
        return newplaylist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist newplaylist = new Playlist(title);
        List<Song> songList = new ArrayList<>();
        for(Song song : songs){
            if(songTitles.contains(song.getTitle())){
                songList.add(song);
            }
        }
        playlistSongMap.put(newplaylist,songList);
        playlists.add(newplaylist);
        User user = new User("",mobile);
        users.add(user);
        List<User> userList = playlistListenerMap.getOrDefault(newplaylist,new ArrayList<>());
        userList.add(users.get(users.size()-1));
        playlistListenerMap.put(newplaylist,userList);
        creatorPlaylistMap.put(user,newplaylist);
        return newplaylist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User newuser = new User("",mobile);
        Playlist playlist1 = new Playlist();
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                playlist1 = playlist;
                if(!creatorPlaylistMap.containsKey(playlist) && !playlistListenerMap.get(playlist).contains(newuser)){
                    List<User> user = playlistListenerMap.get(playlist);
                    user.add(newuser);
                    playlistListenerMap.put(playlist,user);
                }
                break;
            }
        }
        return playlist1;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song song = new Song();
        for(Song song1 : songs){
            if(song1.getTitle().equals(songTitle)){
                song = song1;
                List<User> userList = songLikeMap.getOrDefault(song,new ArrayList<>());
                User newuser = new User("",mobile);
                if(!userList.contains(newuser)){
                    userList.add(newuser);
                    songLikeMap.put(song,userList);
                }
                for(Album album: albumSongMap.keySet()){
                    if(albumSongMap.get(album).contains(song)){
                        for(Artist artist : artistAlbumMap.keySet()){
                            if(artistAlbumMap.get(artist).contains(album)){
                                artist.setLikes(artist.getLikes()+1);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            break;
        }
        return song;

    }

    public String mostPopularArtist() {
        String name="" ;
        int cnt=0;
        for(Artist artist :artists){
            if(artist.getLikes()>cnt){
                cnt = artist.getLikes();
                name = artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        String name = "";
        int max = 0;
        for(Song song : songLikeMap.keySet()){
            int size = songLikeMap.get(song).size();
            if(size > max){
                name = song.getTitle();
                max = size;
            }
        }

        return name;
    }
}
