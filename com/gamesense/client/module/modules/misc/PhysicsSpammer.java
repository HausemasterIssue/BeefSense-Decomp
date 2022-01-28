



package com.gamesense.client.module.modules.misc;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import java.util.*;
import java.net.*;
import java.io.*;
import com.gamesense.api.util.misc.*;

public class PhysicsSpammer extends Module
{
    private List<String> cache;
    private long lastTime;
    private long delay;
    private Setting.Integer minDelay;
    private Setting.Integer maxDelay;
    private Random random;
    
    public PhysicsSpammer() {
        super("PhysicsSpammer", Module.Category.Misc);
        this.cache = new LinkedList<String>();
        this.random = new Random(System.currentTimeMillis());
        this.minDelay = this.registerInteger("Min Delay", 5, 1, 100);
        this.maxDelay = this.registerInteger("Max Delay", 5, 1, 100);
        this.updateTimes();
    }
    
    public void onUpdate() {
        if (this.delay > Math.max(this.minDelay.getValue(), this.maxDelay.getValue())) {
            this.delay = Math.max(this.minDelay.getValue(), this.maxDelay.getValue());
        }
        else if (this.delay < Math.min(this.minDelay.getValue(), this.maxDelay.getValue())) {
            this.delay = Math.min(this.minDelay.getValue(), this.maxDelay.getValue());
        }
        if (System.currentTimeMillis() >= this.lastTime + 1000L * this.delay) {
            if (this.cache.size() == 0) {
                try {
                    final Scanner scanner = new Scanner(new URL("http://snarxiv.org/").openStream());
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("<p>")) {
                            if (line.startsWith("<p><a")) {
                                continue;
                            }
                            if (line.startsWith("<p>Links to:")) {
                                continue;
                            }
                            line = line.substring(3);
                            while (true) {
                                final int pos = line.indexOf(". ");
                                if (pos < 0) {
                                    break;
                                }
                                this.cache.add(line.substring(0, pos + 1));
                                line = line.substring(pos + 2);
                            }
                            this.cache.add(line);
                        }
                    }
                    scanner.close();
                }
                catch (MalformedURLException ex) {}
                catch (IOException ex2) {}
            }
            if (this.cache.size() == 0) {
                this.cache.add("Error! :(");
            }
            MessageBus.sendServerMessage("> " + this.cache.get(0));
            this.cache.remove(0);
            this.updateTimes();
        }
    }
    
    private void updateTimes() {
        this.lastTime = System.currentTimeMillis();
        final int bound = Math.abs(this.maxDelay.getValue() - this.minDelay.getValue());
        this.delay = ((bound == 0) ? 0 : this.random.nextInt(bound)) + Math.min(this.maxDelay.getValue(), this.minDelay.getValue());
    }
}
