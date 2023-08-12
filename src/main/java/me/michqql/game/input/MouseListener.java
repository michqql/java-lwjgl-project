package me.michqql.game.input;

public class MouseListener {

    private static MouseReceiver receiver;

    static void register(MouseReceiver receiverIn) {
        receiver = receiverIn;
    }

    public static void mousePosCallback(long windowId, double xPos, double yPos) {
        receiver.onMouseMove(xPos, yPos);
    }

    public static void mouseButtonCallback(long windowId, int button, int action, int modifiers) {
        receiver.onMousePress(button, action);
    }

    public static void mouseScrollCallback(long windowId, double dx, double dy) {
        receiver.onMouseScroll(dx, dy);
    }

    // End of static

    interface MouseReceiver {
        void onMouseMove(double newX, double newY);
        void onMousePress(int button, int action);
        void onMouseScroll(double dx, double dy);
    }
}
