import java.util.Comparator;

public class Track implements Comparable<Track> {

    private String title;
    private String author;

    private int bpm;

    public Track(String title, String author, int bpm) {
        this.title = title;
        this.author = author;
        this.bpm = bpm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Track)
        {
            final Track t = (Track) obj;
            return title.equals(t.getTitle()) && author.equals(t.getAuthor());
        }
        else
        {
            return super.equals(obj);
        }
    }

    @Override
    public int compareTo(Track o) {
        return Integer.compare(this.bpm, o.getBpm());
    }
}
