public class Trackinfo {

    private int bpm;
    private int durationSeconds;

    public Trackinfo(int bpm, int durationSeconds) {
        this.bpm = bpm;
        this.durationSeconds = durationSeconds;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
