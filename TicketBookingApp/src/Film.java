public class Film {
    private String title;
    private String genre;
    private String director;
    private int releaseYear;
    private int duration;

    public Film(String title, String genre, String director, int releaseYear, int duration) {
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.releaseYear = releaseYear;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDuration() {
        return duration;
    }
}
