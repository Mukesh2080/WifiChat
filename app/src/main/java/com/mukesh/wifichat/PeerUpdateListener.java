package com.mukesh.wifichat;

import java.util.ArrayList;

public interface PeerUpdateListener {
    void onPeerListUpdated(ArrayList<String> updatedPeers);
}

