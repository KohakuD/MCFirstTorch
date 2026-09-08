package ch.minenox.firsttorch.client;

/** Session-local delivery guard; persistent acknowledgement belongs to the server. */
final class WelcomePromptState {
    private boolean pending;
    private boolean handled;

    void request() { if (!handled) pending = true; }
    boolean ready(boolean worldReady, boolean screenFree, int playerTicks) {
        return pending && worldReady && screenFree && playerTicks >= 40;
    }
    boolean pending() { return pending; }
    void handled() { pending = false; handled = true; }
    void clear() { pending = false; handled = false; }
}
