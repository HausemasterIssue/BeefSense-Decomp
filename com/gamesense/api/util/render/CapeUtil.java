



package com.gamesense.api.util.render;

import java.util.*;
import java.net.*;
import java.io.*;

public class CapeUtil
{
    List<UUID> uuids;
    
    public CapeUtil() {
        this.uuids = new ArrayList<UUID>();
        try {
            final URL capesList = new URL("https://raw.githubusercontent.com/SmokeKief/capes/master/capelist.txt");
            final BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                this.uuids.add(UUID.fromString(inputLine));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasCape(final UUID id) {
        return this.uuids.contains(id);
    }
}
