package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(Song::getSongName).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
                                     .filter(e -> e.getValue().equals(year))
                                     .map(e -> e.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().isPresent())
                         .filter(s -> s.getAlbumName().get().equals(albumName))
                         .mapToInt(c -> 1).sum();
    }

    @Override
    public int countSongsInNoAlbum() {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().isEmpty())
                         .mapToInt(c -> 1).sum();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().equals(Optional.of(albumName)))
                         .mapToDouble(Song::getDuration)
                         .average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                         .max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration()))
                         .map(Song::getSongName);
    }

    /**
     * 
     * @param albumName the album name
     * @return the duration of the album
     */
    private Double durationOfAlbum(final String albumName) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().equals(Optional.of(albumName)))
                         .mapToDouble(Song::getDuration)
                         .sum();
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.albums.keySet().stream()
                                   .max((a1, a2) -> Double.compare(durationOfAlbum(a1), durationOfAlbum(a2)));
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
