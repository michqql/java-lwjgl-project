package me.michqql.game.input;

public class KeyListener {

    private static Receiver receiver;

    public static void setReceiver(Receiver receiver) {
        KeyListener.receiver = receiver;
    }

    public static void keyCallback(long windowId, int key, int scanCode, int action, int mods) {
        receiver.onKeyPressed(key, action);
    }

    interface Receiver {
        void onKeyPressed(int key, int action);
    }
}
