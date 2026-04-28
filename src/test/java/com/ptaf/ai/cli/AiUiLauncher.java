package com.ptaf.ai.cli;

/**
 * IntelliJ-friendly launcher for the local AI UI server.
 * Run this class directly from the play button.
 */
public final class AiUiLauncher {

    private AiUiLauncher() {
    }

    public static void main(String[] args) {
        AiAssistantCli.main(new String[]{"serve"});
    }
}
