package com.linkopus.ms.utils.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogsColorsTest {

	private final LogsColors logsColors = new LogsColors();

	@Test
	void testGetForegroundColorCode_ErrorLevel() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getLevel()).thenReturn(Level.ERROR);

		String colorCode = logsColors.getForegroundColorCode(event);

		assertEquals(ANSIConstants.BOLD + ANSIConstants.RED_FG, colorCode,
				"Error level should return bold red foreground color code.");
	}

	@Test
	void testGetForegroundColorCode_WarnLevel() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getLevel()).thenReturn(Level.WARN);

		String colorCode = logsColors.getForegroundColorCode(event);

		assertEquals(ANSIConstants.BOLD + ANSIConstants.YELLOW_FG, colorCode,
				"Warn level should return bold yellow foreground color code.");
	}

	@Test
	void testGetForegroundColorCode_InfoLevel() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getLevel()).thenReturn(Level.INFO);

		String colorCode = logsColors.getForegroundColorCode(event);

		assertEquals(ANSIConstants.BOLD + ANSIConstants.GREEN_FG, colorCode,
				"Info level should return bold green foreground color code.");
	}

	@Test
	void testGetForegroundColorCode_DebugLevel() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getLevel()).thenReturn(Level.DEBUG);

		String colorCode = logsColors.getForegroundColorCode(event);

		assertEquals(ANSIConstants.DEFAULT_FG, colorCode, "Debug level should return default foreground color code.");
	}

	@Test
	void testGetForegroundColorCode_TraceLevel() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getLevel()).thenReturn(Level.TRACE);

		String colorCode = logsColors.getForegroundColorCode(event);

		assertEquals(ANSIConstants.DEFAULT_FG, colorCode, "Trace level should return default foreground color code.");
	}
}
