package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public abstract class CronHandler extends Handler<ScheduledEvent, Void> {
}
