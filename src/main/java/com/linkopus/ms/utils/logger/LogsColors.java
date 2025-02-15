package com.linkopus.ms.utils.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class LogsColors extends ForegroundCompositeConverterBase<ILoggingEvent> {

	@Override
	protected String getForegroundColorCode(ILoggingEvent event) {
		Level level = event.getLevel();
		return switch (level.toInt()) {
			case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG;
			case Level.WARN_INT -> ANSIConstants.BOLD + ANSIConstants.YELLOW_FG;
			case Level.INFO_INT -> ANSIConstants.BOLD + ANSIConstants.GREEN_FG;
			default -> ANSIConstants.DEFAULT_FG;
		};
	}
}
