package com.example.homework_score;

public class wait extends Thread {
    @Override
    public void run() {
        super.run();
        try
        {
            sleep(100);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
