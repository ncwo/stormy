package me.tryfle.stormy;

import dev.stormy.client.main.Stormy;
import net.weavemc.loader.api.ModInitializer;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.StartGameEvent;

public class Main implements ModInitializer {
	@Override
	public void preInit() {
		EventBus.subscribe(StartGameEvent.Post.class, (startGameEvent) -> Stormy.init());
	}
}