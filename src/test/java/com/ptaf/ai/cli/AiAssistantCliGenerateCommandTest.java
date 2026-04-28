package com.ptaf.ai.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiAssistantCliGenerateCommandTest {

    @Test
    void defaultModeIsPreview() {
        AiAssistantCli.GenerateCommand command = new AiAssistantCli.GenerateCommand();
        new CommandLine(command).parseArgs();
        assertEquals("preview", command.mode);
    }
}
