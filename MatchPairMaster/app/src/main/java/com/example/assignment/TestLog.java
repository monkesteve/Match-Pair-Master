package com.example.assignment;

public class TestLog {
    public int testNo;
    public String playerName;
    public String testDate;
    public double duration;
    public int moves;
    public int difficulties;

    // No-argument constructor required for Firebase
    public TestLog() {
    }

    public TestLog(int testNo, String playerName, String testDate, double duration, int moves, int difficulties) {
        this.testNo = testNo;
        this.playerName = playerName;
        this.testDate = testDate;
        this.duration = duration;
        this.moves = moves;
        this.difficulties = difficulties;
    }
}
